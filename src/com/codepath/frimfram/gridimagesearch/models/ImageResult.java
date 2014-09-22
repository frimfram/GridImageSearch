package com.codepath.frimfram.gridimagesearch.models;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageResult implements Serializable {

	private static final long serialVersionUID = 7434155485653022327L;
	public String fullUrl;
	public String thumbUrl;
	public String title;
	public int height;
	public int width;
	public int tbWidth;
	public int tbHeight;
	
	public ImageResult(JSONObject json) {
		try {
			this.title = json.getString("title");
			this.height = Integer.parseInt(json.getString("height"));
			this.width = Integer.parseInt(json.getString("width"));
			this.tbWidth = Integer.parseInt(json.getString("tbWidth"));
			this.tbHeight = Integer.parseInt(json.getString("tbHeight"));
			this.fullUrl = json.getString("url");
			this.thumbUrl = json.getString("tbUrl");
		}catch(JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ImageResult> fromJSONArray(JSONArray array) {
		ArrayList<ImageResult> results = new ArrayList<ImageResult>();
		for(int i=0; i<array.length(); i++) {
			try {
				results.add(new ImageResult(array.getJSONObject(i)));
			}catch(JSONException e) {
				e.printStackTrace();
			}
		}
		return results;		
	}
}
