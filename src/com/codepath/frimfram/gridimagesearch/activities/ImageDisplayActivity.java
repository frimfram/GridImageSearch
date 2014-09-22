package com.codepath.frimfram.gridimagesearch.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.frimfram.gridimagesearch.R;
import com.codepath.frimfram.gridimagesearch.helpers.TouchImageView;
import com.codepath.frimfram.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageDisplayActivity extends Activity {
	
	private TouchImageView ivImageResult;
	private TextView tvTitle;
	private ShareActionProvider miShareAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_display);		
		//getActionBar().hide();		
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		int screenHeight = metrics.heightPixels;
		
		ImageResult result = (ImageResult) getIntent().getSerializableExtra("result");
		int imageResizeWidth = screenWidth - 100;
		int imageResizeHeight = (imageResizeWidth * result.height) / result.width;

		ivImageResult = (TouchImageView)findViewById(R.id.ivImageResult);
		ivImageResult.setMaxHeight(imageResizeHeight + 300);
		Picasso.with(this).load(result.fullUrl).resize(imageResizeWidth, imageResizeHeight).into(ivImageResult, new Callback() {
			
			@Override
			public void onSuccess() {
				setupShareIntent();				
			}
			
			@Override
			public void onError() {
				//handle image load error
				Toast.makeText(ImageDisplayActivity.this, 
						getResources().getString(R.string.error_loading_image), Toast.LENGTH_LONG).show();
			}
		});
				
		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvTitle.setText(Html.fromHtml(result.title));
	}
	
	public void setupShareIntent() {
		Uri bmpUri = getLocalBitmapUri(ivImageResult);
		if(bmpUri != null) {
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
			shareIntent.setType("image/*");
			miShareAction.setShareIntent(shareIntent);
		}
	}
	
	public Uri getLocalBitmapUri(ImageView imageView) {
	    // Extract Bitmap from ImageView drawable
	    Drawable drawable = imageView.getDrawable();
	    Bitmap bmp = null;
	    if (drawable instanceof BitmapDrawable){
	       bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
	    } else {
	       return null;
	    }
	    // Store image to default external storage directory
	    Uri bmpUri = null;
	    try {
	        File file =  new File(Environment.getExternalStoragePublicDirectory(  
		        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
	        file.getParentFile().mkdirs();
	        FileOutputStream out = new FileOutputStream(file);
	        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	        out.close();
	        bmpUri = Uri.fromFile(file);
	    } catch (IOException e) {
	        e.printStackTrace();
	        
	    }
	    return bmpUri;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image_display, menu);
		MenuItem item = menu.findItem(R.id.menu_item_share);
		miShareAction = (ShareActionProvider)item.getActionProvider();
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
