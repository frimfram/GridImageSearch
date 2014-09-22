package com.codepath.frimfram.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.frimfram.gridimagesearch.R;
import com.codepath.frimfram.gridimagesearch.helpers.NetworkHelper;
import com.codepath.frimfram.gridimagesearch.models.SearchFilter;

public class SettingsActivity extends Activity {
	
	private Spinner spImageSize;
	private Spinner spColor;
	private Spinner spImageType;
	private EditText etSite;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		spImageSize = (Spinner)findViewById(R.id.spImageSize);
		spColor = (Spinner)findViewById(R.id.spColorFilter);
		spImageType = (Spinner)findViewById(R.id.spImageType);
		etSite = (EditText)findViewById(R.id.etSiteFilterUri);
		
		SearchFilter filter = getIntent().getParcelableExtra("filter");
		if(filter != null) {
			spImageSize.setSelection(filter.getImageSize());
			spColor.setSelection(filter.getImageColorIndex());
			spImageType.setSelection(filter.getImageType());
			etSite.setText(filter.getSite());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void onSave(View v) {
		if(validate()) {
			SearchFilter filter = new SearchFilter();
			filter.setImageSize(spImageSize.getSelectedItemPosition());
			filter.setImageType(spImageType.getSelectedItemPosition());
			filter.setImageColor((String)spColor.getSelectedItem());
			filter.setImageColorIndex(spColor.getSelectedItemPosition());
			if(etSite.getText() != null) {
				filter.setSite(etSite.getText().toString());
			}
			Intent intent = new Intent();
			intent.putExtra("filter", filter);
			setResult(RESULT_OK, intent);
			
			finish();
		}else{
			etSite.setError(getResources().getString(R.string.invalid_site_url));
		}
	}
	
	private boolean validate() {
		if(etSite.getText() != null) {
			String siteUrl = etSite.getText().toString();
			if(Patterns.WEB_URL.matcher(siteUrl).matches()) {
				//return NetworkHelper.isOnline(siteUrl);
				return true;
			}else{
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
