package com.codepath.frimfram.gridimagesearch.activities;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.frimfram.gridimagesearch.R;
import com.codepath.frimfram.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.frimfram.gridimagesearch.fragments.FilterDialog;
import com.codepath.frimfram.gridimagesearch.fragments.FilterDialog.OnFragmentInteractionListener;
import com.codepath.frimfram.gridimagesearch.helpers.EndlessScrollListener;
import com.codepath.frimfram.gridimagesearch.helpers.NetworkHelper;
import com.codepath.frimfram.gridimagesearch.models.ImageResult;
import com.codepath.frimfram.gridimagesearch.models.SearchFilter;
import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


public class SearchActivity extends FragmentActivity implements OnFragmentInteractionListener {
	private SearchView searchView;
	//private GridView gvResults;
	private StaggeredGridView gvResults;
	private ArrayList<ImageResult> imageResults;
	private ImageResultsAdapter aImageResults;
	private EndlessScrollListener scrollListener;
	
	private  JsonHttpResponseHandler jsonResponseHandler;
	private ArrayList<Integer> startPositions;
	private int currentPageIndex = -1;
	private int lastPageRequested = -1;
	private String currentQuery;
	
	private SearchFilter searchFilter = new SearchFilter();
	
	public static int SEARCH_FILTER_ACTIVITY_REQUEST_CODE = 59;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResults = new ArrayList<ImageResult>();
        aImageResults = new ImageResultsAdapter(this, imageResults);
        gvResults.setAdapter(aImageResults);
        gvResults.setEmptyView(findViewById(R.id.imgGridEmpty));
        
    }
    
    private void setupViews() {
    	gvResults = (StaggeredGridView)findViewById(R.id.gvResults);
    	gvResults.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
				ImageResult result = imageResults.get(position);
				i.putExtra("result", result);
				startActivity(i);
			}
    		
		});
    	
        jsonResponseHandler = new JsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				
				JSONArray imageResultsJson = null;
				try {
					JSONObject resultsJson = response.getJSONObject("responseData");
					//get the page and cursor information
					populatePageCursorInfo(resultsJson);
					
					imageResultsJson = resultsJson.getJSONArray("results");
					if(currentPageIndex == 0) {
						scrollListener.reset();
						aImageResults.clear(); //when it's a new search 
					}
					aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
				Toast.makeText(SearchActivity.this, getResources().getString(R.string.network_failed), 
	    				Toast.LENGTH_LONG).show();
			}
        	
        };  
        
        scrollListener = new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if((currentPageIndex+1) < startPositions.size()) {
					//also make sure that there is no duplicate calls
					if(page > lastPageRequested) {
						makeDataLoadRequest();
						lastPageRequested = page;
					}
				}
			}
    		
    	};
    	
    	gvResults.setOnScrollListener(scrollListener);
    	
    }
    
    private void populatePageCursorInfo(JSONObject resultsJson) {    	
    	try {
    		JSONObject cursor = resultsJson.getJSONObject("cursor");    		
    		currentPageIndex = cursor.getInt("currentPageIndex");
    		JSONArray pages = cursor.getJSONArray("pages");
    		startPositions = new ArrayList<Integer>();
    		for(int i=0; i<pages.length(); i++) {
    			startPositions.add(Integer.parseInt(((JSONObject)pages.get(i)).getString("start")));
    		}
    	}catch(Exception e){
    		currentPageIndex = 0;
    		startPositions = null;
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_prompt));
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				//hide the keyboard
				InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				//perform query
				onImageSearch();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
               
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	showFiterInDialogFragment();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == SEARCH_FILTER_ACTIVITY_REQUEST_CODE) {
    		if(resultCode == RESULT_OK) {
    			searchFilter = (SearchFilter)data.getParcelableExtra("filter");
    			if(searchView.getQuery()==null || searchView.getQuery().toString().length()<1) {
    				startNewImageSearch();
    			}
    		}
    	}
    }
    
    public void onImageSearch() {       
    	//check for network availability
    	if(NetworkHelper.isNetworkAvailable(this)) {
    		startNewImageSearch();
    	}else{
    		Toast.makeText(this, getResources().getString(R.string.network_not_available), 
    				Toast.LENGTH_LONG).show();
    	}
    	if(searchView.getQuery()==null || searchView.getQuery().toString().length()<1) {
    		Toast.makeText(this, getResources().getString(R.string.search_prompt), 
    				Toast.LENGTH_LONG).show();
    	}
    }
    
    private void startNewImageSearch() {
        currentPageIndex = -1;
        lastPageRequested = -1;
        startPositions = null;
        currentQuery = searchView.getQuery().toString();
        makeDataLoadRequest();    	
    }
    
    private void makeDataLoadRequest() {
        AsyncHttpClient client = new AsyncHttpClient(); 
        int start = 0;
        if(currentPageIndex > -1 && startPositions != null) {
        	start = startPositions.get(currentPageIndex + 1);
        }
        String searchUrl = getSearchUrl(currentQuery, start, searchFilter); 
        Log.d("DEBUG", "making request: " + searchUrl);
        client.get(searchUrl, jsonResponseHandler);     	
    }
    
    private String getSearchUrl(String query, int start, SearchFilter filter) {
    	if(query == null) query = "";
    	StringBuilder filterParam = new StringBuilder("");
    	if(filter != null) {
    		String imgcolor = filter.getImageColor();
    		if(imgcolor != null && !"any".equalsIgnoreCase(imgcolor)) {
    			filterParam.append("&imgcolor=").append(imgcolor.toLowerCase());
    		}
    		int imgszIndex = filter.getImageSize();
    		if(imgszIndex > 0 && imgszIndex <= SearchFilter.IMAGE_SIZE_PARAM_VALUES.length) {
    			filterParam.append("&imgsz=").append(SearchFilter.IMAGE_SIZE_PARAM_VALUES[imgszIndex-1]);
    		}
    		int imgtypeIndex = filter.getImageType();
    		if(imgtypeIndex > 0 && imgtypeIndex <= SearchFilter.IMAGE_TYPE_PARAM_VALUES.length) {
    			filterParam.append("&imgtype=").append(SearchFilter.IMAGE_TYPE_PARAM_VALUES[imgtypeIndex-1]);
    		}
    		String site = filter.getSite();
    		if(site != null && site.length() > 0) {
    			try {
    				site = URLEncoder.encode(site, "UTF-8");
    				filterParam.append("&as_sitesearch=").append(site);
    			}catch(Exception e) {
    				
    			}   			
    		}
    	}
    	return "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" +
		query + "&rsz=8&start=" + start + filterParam.toString();
    }

	@Override
	public void onFragmentInteraction(SearchFilter filter) {
		searchFilter = filter;
		if(searchView.getQuery()!=null && searchView.getQuery().toString().length()>0) {
			startNewImageSearch();
		}		
	}
	
	private void showFiterInDialogFragment() {
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		FilterDialog filterDialog = FilterDialog.newInstance(searchFilter);
		filterDialog.show(fm, "fragment_filter_settings");
	}
	
	private void showFilterInActivity() {
    	Intent settingsIntent = new Intent(this, SettingsActivity.class);
    	if(searchFilter != null) {
    		settingsIntent.putExtra("filter", searchFilter);
    	}
    	startActivityForResult(settingsIntent, SEARCH_FILTER_ACTIVITY_REQUEST_CODE);
	}
}
