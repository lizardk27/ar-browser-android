package com.pontus.augmentedrealitybrowser.position;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by gabriel yibirin.
 */
public class MyCurrentLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private OnLocationChangedListener onLocationChangedListener;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private Location mCurrentLocation ;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates=true;
    public final String REQUESTING_LOCATION_UPDATES_KEY= "REQUESTING_LOCATION_UPDATES_KEY";
    public final String LOCATION_KEY= "LOCATION_KEY";
    public final String LAST_UPDATED_TIME_STRING_KEY= "LAST_UPDATED_TIME_STRING_KEY";



    public MyCurrentLocation(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    public synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage((FragmentActivity) context,this)
                .build();


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)          // 10 sec. in milliseconds
                .setFastestInterval(5 * 1000);        // 5 sec. milliseconds
    }

    public void start(){
        mRequestingLocationUpdates=true;
        mGoogleApiClient.connect();
    }

    public void stop(){
        mRequestingLocationUpdates=false;
        mGoogleApiClient.disconnect();
    }

    public void pause(){
        stopLocationUpdates();
    }

    public void resume(){
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }else if (!mGoogleApiClient.isConnected()){
            mGoogleApiClient.connect();
        }
    }


    protected void stopLocationUpdates() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
        mRequestingLocationUpdates=false;
    }



    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        try {
            onLocationChangedListener.onLocationChanged(mLastLocation);
        }catch (NullPointerException npe){
            Log.d("NULL PONTER",npe.getLocalizedMessage());
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    (Activity) onLocationChangedListener,
                                    onLocationChangedListener.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {

        mRequestingLocationUpdates=true;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("MyApp", "Location services connection failed with code " + connectionResult.getErrorCode());

        mRequestingLocationUpdates=false;
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed");
        boolean better = isBetterLocation(location,mLastLocation);
        System.out.println("location is better than last? = "  + better);

        if (better) {
           mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            try {
                onLocationChangedListener.onLocationChanged(mLastLocation);
            }catch (NullPointerException npe){
                Log.d("NULL PONTER",npe.getLocalizedMessage());
            }

            Log.d("NULL PONTER","AFTER catch");
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            onLocationChangedListener.onLocationChanged(mCurrentLocation);

            Log.d("NULL PONTER","all asigned");
        }

    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public String getLastUpdateTime() {
        return mLastUpdateTime;
    }


    public boolean getRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public void setRequestingLocationUpdates(boolean state) {
        mRequestingLocationUpdates=state;
    }

    public void setCurrentLocation(Location loc) {
        mCurrentLocation=loc;
    }

    public void setLastUpdateTime(String time ) {
        mLastUpdateTime = time;
    }

}
