package com.codepath.frimfram.gridimagesearch.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchFilter implements Parcelable {
	
	public static String[] IMAGE_SIZE_PARAM_VALUES = {"small", "medium", "large", "xlarge" };
	public static String[] IMAGE_TYPE_PARAM_VALUES = {"face", "photo", "clipart", "lineart" };
	
	private int imageSize;
	private int imageType;
	private String imageColor;
	private String site = "";
	private int imageColorIndex;
	
	public int getImageColorIndex() {
		return imageColorIndex;
	}

	public void setImageColorIndex(int imageColorIndex) {
		this.imageColorIndex = imageColorIndex;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public SearchFilter() {
		
	}
	
	public static final Parcelable.Creator<SearchFilter> CREATOR =
			new Parcelable.Creator<SearchFilter>() {

				@Override
				public SearchFilter createFromParcel(Parcel source) {
					return new SearchFilter(source);
				}

				@Override
				public SearchFilter[] newArray(int size) {
					return new SearchFilter[size];
				}
			};
	
	private SearchFilter(Parcel in) {
		imageSize = in.readInt();
		imageType = in.readInt();
		imageColor = in.readString();
		site = in.readString();
		imageColorIndex = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(imageSize);
		out.writeInt(imageType);
		out.writeString(imageColor);
		out.writeString(site);
		out.writeInt(imageColorIndex);
	}	
	
	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public String getImageColor() {
		return imageColor;
	}

	public void setImageColor(String imageColor) {
		this.imageColor = imageColor;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
