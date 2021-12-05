package com.pontus.augmentedrealitybrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pontus.augmentedrealitybrowser.position.MyCurrentAzimuth;
import com.pontus.augmentedrealitybrowser.position.MyCurrentLocation;
import com.pontus.augmentedrealitybrowser.position.OnAzimuthChangedListener;
import com.pontus.augmentedrealitybrowser.position.OnLocationChangedListener;
import com.pontus.augmentedrealitybrowser.query.GetGooglePlacesResultsAsync;
import com.pontus.augmentedrealitybrowser.query.GetWikiResultsAsync;
import com.pontus.augmentedrealitybrowser.query.OnGoogleResultReadyListener;
import com.pontus.augmentedrealitybrowser.query.OnTwitterResultReadyListener;
import com.pontus.augmentedrealitybrowser.query.OnWikiResultReadyListener;
import com.pontus.augmentedrealitybrowser.query.TwitterQueryManager;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gabriel yibirin.
 */
public class CameraViewActivity extends FragmentActivity implements
        SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener,
        OnGoogleResultReadyListener, OnWikiResultReadyListener, OnTwitterResultReadyListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "dtiYbZtv3tMlTVzsp8lKkaqR7";
    private static final String TWITTER_SECRET = "JiBJhCl19LOrgHFzA3wHA46vSAh1rm1GVjbDyTlDvmIAWagMtd";

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private boolean isCameraviewOn = false;
    private AugmentedPOI[] mPoi;

    private double mAzimuthReal = 0;
    private static double AZIMUTH_ACCURACY = 5;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    private Bundle savedInstanceState;

    private MyCurrentAzimuth myCurrentAzimuth;
    private MyCurrentLocation myCurrentLocation;

    TextView descriptionTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //TwitterL
        setContentView(R.layout.activity_camera_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setupListeners();
        setupLayout();
        setAugmentedRealityPoints();

    }


    private void setAugmentedRealityPoints() {


        ImageView pointerN = (ImageView) findViewById(R.id.north);
        pointerN.setImageResource(this.getResources().getIdentifier("drawable/blue", null, this.getPackageName()));

        ImageView pointerE = (ImageView) findViewById(R.id.east);
        pointerE.setImageResource(this.getResources().getIdentifier("drawable/red", null, this.getPackageName()));

        ImageView pointerW = (ImageView) findViewById(R.id.west);
        pointerW.setImageResource(this.getResources().getIdentifier("drawable/green", null, this.getPackageName()));

        ImageView pointerS = (ImageView) findViewById(R.id.south);
        pointerS.setImageResource(this.getResources().getIdentifier("drawable/test", null, this.getPackageName()));

        mPoi = new AugmentedPOI[4];
        mPoi[0] = new AugmentedPOI("NORTH",
                "NORTH",
                4.729697,
                -74.039150,
                pointerN);

		/*mPoi[0]= new AugmentedPOI("NORTH",
                "NORTH",
				9.142406,
				-74.21632,
				pointerN);
*/
        mPoi[1] = new AugmentedPOI("SOUTH",
                "SOUTH",
                4.7297572,
                -74.039017,
                pointerS);

        mPoi[2] = new AugmentedPOI("EAST",
                "EAST",
                4.700164,
                -66.10207,
                pointerE);

        mPoi[3] = new AugmentedPOI("WEST",
                "WEST",
                4.700164,
                -83.42468,
                pointerW);
    }

    public double calculateTheoreticalAzimuth(AugmentedPOI mPoi) {

        double dX = mPoi.getPoiLatitude() - mMyLatitude;
        double dY = mPoi.getPoiLongitude() - mMyLongitude;

        double phiAngle;
        double tanPhi;
        double azimuth = 0;

        tanPhi = Math.abs(dY / dX);
        phiAngle = Math.atan(tanPhi);
        phiAngle = Math.toDegrees(phiAngle);

        if(dX > 0 && dY > 0) { // I quater
            return azimuth = phiAngle;
        } else if(dX < 0 && dY > 0) { // II
            return azimuth = 180 - phiAngle;
        } else if(dX < 0 && dY < 0) { // III
            return azimuth = 180 + phiAngle;
        } else if(dX > 0 && dY < 0) { // IV
            return azimuth = 360 - phiAngle;
        }

        return phiAngle;
    }

    private List<Double> calculateAzimuthAccuracy(double azimuth) {
        double minAngle = azimuth - AZIMUTH_ACCURACY;
        double maxAngle = azimuth + AZIMUTH_ACCURACY;
        List<Double> minMax = new ArrayList<Double>();

        if(minAngle < 0)
            minAngle += 360;

        if(maxAngle >= 360)
            maxAngle -= 360;

        minMax.clear();
        minMax.add(minAngle);
        minMax.add(maxAngle);

        return minMax;
    }

    private boolean isBetween(double minAngle, double maxAngle, double azimuth) {

        if(minAngle > maxAngle) {
            if(isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
                return true;
        } else {
            if(azimuth > minAngle && azimuth < maxAngle)
                return true;
        }
        return false;
    }

    private void updateDescription() {

        String tmp = "";
        for (AugmentedPOI poi : mPoi) {
            tmp += (poi.getPoiName() + ": Azimuth(Theor) " + poi.getAzimuthTeoretical()
                    + " Azimuth(Real) " + mAzimuthReal
                    + " Distance: " + poi.getDistanceInMeters() + "m. ");
        }
        tmp += "\n" + "My latitude " + mMyLatitude + "My longitude " + mMyLongitude;
        descriptionTextView.setText(tmp);
    }

    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        for (AugmentedPOI poi : mPoi) {
            poi.setAzimuthTeoretical(calculateTheoreticalAzimuth(poi));
        }
        Toast.makeText(this, "latitude: " + location.getLatitude() + " longitude: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        updateDescription();
        String wiki_url = "NOT SET";
        String googleplaces_url = "NOT SET";
        try {
            wiki_url = "https://en.wikipedia.org/w/api.php?"
                    + "action=query&list=geosearch&gscoord="
                    + mMyLatitude + URLEncoder.encode("|", "UTF-8") + mMyLongitude
                    + "&gsradius=10000&gslimit=10"
                    + "&format=json";

            googleplaces_url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                    "location=" + mMyLatitude + "," + mMyLongitude + "&radius=2000&type=all&key=AIzaSyB6-7tcUICnnlR1zbTBDZgx-0qb_ta8V5M";

        } catch (UnsupportedEncodingException use) {
            use.printStackTrace();
            Log.d("WIKI", wiki_url);
        }
        Log.d("WIKI", wiki_url);
        GetWikiResultsAsync gwra = new GetWikiResultsAsync(this,wiki_url);
        gwra.execute();
        GetGooglePlacesResultsAsync gpra = new GetGooglePlacesResultsAsync(this, googleplaces_url);
        gpra.execute();
        TwitterQueryManager tqm = new TwitterQueryManager(this, this, mMyLatitude, mMyLongitude);

    }

    @Override
    public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {

        mAzimuthReal = azimuthChangedTo;
        Location locOrig = new Location("");
        locOrig.setLatitude(mMyLatitude);
        locOrig.setLatitude(mMyLongitude);

        for (AugmentedPOI poi : mPoi) {
            poi.setAzimuthTeoretical(calculateTheoreticalAzimuth(poi));

            double minAngle = calculateAzimuthAccuracy(poi.getAzimuthTeoretical()).get(0);
            double maxAngle = calculateAzimuthAccuracy(poi.getAzimuthTeoretical()).get(1);
            Location locDest = new Location("");
            locDest.setLatitude(poi.getPoiLatitude());
            locDest.setLatitude(poi.getPoiLongitude());
            poi.setDistanceInMeters(getDistanceInMeters(locOrig, locDest));

            if(isBetween(minAngle, maxAngle, mAzimuthReal)) {
                //System.out.println("Showing: " + poi.getPoiName());
                poi.getPointerIcon().setVisibility(View.VISIBLE);
                return;
            } else {
                poi.getPointerIcon().setVisibility(View.INVISIBLE);
            }
        }
        updateDescription();
    }

    @Override
    protected void onStop() {
        myCurrentAzimuth.stop();
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentAzimuth.start();
        myCurrentLocation.start();
        myCurrentLocation.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myCurrentLocation.pause();
    }

    private float getDistanceInMeters(Location from, Location to) {

        float distanceInMeters = from.distanceTo(to);

        return distanceInMeters;
    }

    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();

        myCurrentAzimuth = new MyCurrentAzimuth(this, this);
        myCurrentAzimuth.start();
    }

    private void setupLayout() {
        descriptionTextView = (TextView) findViewById(R.id.cameraTextView);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if(isCameraviewOn) {
            mCamera.stopPreview();
            isCameraviewOn = false;
        }

        if(mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                isCameraviewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(myCurrentLocation.REQUESTING_LOCATION_UPDATES_KEY,
                myCurrentLocation.getRequestingLocationUpdates());
        savedInstanceState.putParcelable(myCurrentLocation.LOCATION_KEY, myCurrentLocation.getCurrentLocation());
        savedInstanceState.putString(myCurrentLocation.LAST_UPDATED_TIME_STRING_KEY, myCurrentLocation.getLastUpdateTime());
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if(savedInstanceState.keySet().contains(myCurrentLocation.REQUESTING_LOCATION_UPDATES_KEY)) {
                myCurrentLocation.setRequestingLocationUpdates(savedInstanceState.getBoolean(
                        myCurrentLocation.REQUESTING_LOCATION_UPDATES_KEY));
                //setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if(savedInstanceState.keySet().contains(myCurrentLocation.LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                myCurrentLocation.setCurrentLocation((Location) savedInstanceState.getParcelable(myCurrentLocation.LOCATION_KEY));
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if(savedInstanceState.keySet().contains(myCurrentLocation.LAST_UPDATED_TIME_STRING_KEY)) {
                myCurrentLocation.setLastUpdateTime(savedInstanceState.getString(
                        myCurrentLocation.LAST_UPDATED_TIME_STRING_KEY));
            }
            updateDescription();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        isCameraviewOn = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to

                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }


    @Override
    public void onGoogleResultReady(ArrayList<HashMap<String, String>> string_result_list) {

        System.out.println("GOOGLE RESULTS");
        for (HashMap<String, String> tmp : string_result_list) {
            System.out.println(tmp.values());
        }
    }

    @Override
    public void onTwitterResultReady(ArrayList<HashMap<String, String>> string_result_list) {

        System.out.println("TWITTER RESULTS");
        for (HashMap<String, String> tmp : string_result_list) {
            System.out.println(tmp.values());
        }
    }

    @Override
    public void onWikiResultReady(ArrayList<HashMap<String, String>> string_result_list) {

        System.out.println("WIKI RESULTS");
        for (HashMap<String, String> tmp : string_result_list) {
            System.out.println(tmp.values());
        }
    }
}
