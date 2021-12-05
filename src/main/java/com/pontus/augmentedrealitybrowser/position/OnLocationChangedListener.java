package com.pontus.augmentedrealitybrowser.position;

import android.location.Location;

/**
 * Created by gabriel yibirin
 */
public interface OnLocationChangedListener {

    final int REQUEST_CHECK_SETTINGS = 100;
    void onLocationChanged(Location currentLocation);
}
