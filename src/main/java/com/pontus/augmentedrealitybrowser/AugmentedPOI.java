package com.pontus.augmentedrealitybrowser;

import android.widget.ImageView;

/**
 * Created by gabriel yibirin.
 */
public class AugmentedPOI {
	private int mId;
	private String mName;
	private String mDescription;
	private double mLatitude;
	private double mLongitude;
	private double mHeight;

	private double mAzimuthTheoretical = 0;

	public ImageView getPointerIcon() {
		return pointerIcon;
	}

	public void setPointerIcon(ImageView pointerIcon) {
		this.pointerIcon = pointerIcon;
	}

	private float distanceInMeters = 0;
	private ImageView pointerIcon;


	public AugmentedPOI(String newName, String newDescription,
						double newLatitude, double newLongitude, ImageView pointerIcon) {
		this.mName = newName;
        this.mDescription = newDescription;
        this.mLatitude = newLatitude;
        this.mLongitude = newLongitude;
		this.pointerIcon = pointerIcon;

	}
	
	public int getPoiId() {
		return mId;
	}
	public void setPoiId(int poiId) {
		this.mId = poiId;
	}
	public String getPoiName() {
		return mName;
	}
	public void setPoiName(String poiName) {
		this.mName = poiName;
	}
	public String getPoiDescription() {
		return mDescription;
	}
	public void setPoiDescription(String poiDescription) {
		this.mDescription = poiDescription;
	}
	public double getPoiLatitude() {
		return mLatitude;
	}
	public void setPoiLatitude(double poiLatitude) {
		this.mLatitude = poiLatitude;
	}
	public double getAzimuthTeoretical() {
		return mAzimuthTheoretical;
	}
	public void setAzimuthTeoretical(double mAzimuthTeoretical) {
		this.mAzimuthTheoretical = mAzimuthTeoretical;
	}
	public double getDistanceInMeters() {
	return distanceInMeters;
}
	public void setDistanceInMeters(float distanceInMeters) {
		this.distanceInMeters = distanceInMeters;

		int wh;
		if ((int) distanceInMeters==0){
			wh = 850;
		}else{
			wh = (int) 850/(int) distanceInMeters;
		}
		if (wh<100)wh=100;
		pointerIcon.getLayoutParams().width=wh;
		pointerIcon.getLayoutParams().height  =wh;

	}
	public double getPoiLongitude() {
		return mLongitude;
	}
	public void setPoiLongitude(double poiLongitude) {
		this.mLongitude = poiLongitude;
	}

}
