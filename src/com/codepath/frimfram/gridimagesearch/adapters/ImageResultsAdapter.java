package com.codepath.frimfram.gridimagesearch.adapters;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.frimfram.gridimagesearch.R;
import com.codepath.frimfram.gridimagesearch.models.ImageResult;
import com.squareup.picasso.Picasso;

public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {

	public ImageResultsAdapter(Context context, List<ImageResult> objects) {
		super(context, R.layout.item_image_result, objects);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageResult imageInfo = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.item_image_result, parent, false);
		}
		DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
		int screenWidth = metrics.widthPixels;
		long estimatedGridWidth = (screenWidth / 3);
		int tbWidth = imageInfo.tbWidth;
		int tbHeight = imageInfo.tbHeight;
		int maxHeight = ((int)estimatedGridWidth * tbHeight)/tbWidth + 20;
		ImageView ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
		ivImage.setImageResource(0);
		ivImage.setMaxHeight(maxHeight);
		
		Picasso.with(getContext()).load(imageInfo.thumbUrl).into(ivImage);
		return convertView;
	}
}
