package com.pontus.augmentedrealitybrowser.query;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel yibirin on 8/29/2016.
 */
public interface OnTwitterResultReadyListener {

    static final String TAG_NAME="NAME";
    static final String TAG_TEXT="TEXT";
    static final String TAG_SOURCE="SOURCE";

    void onTwitterResultReady(ArrayList<HashMap<String, String>> string_result_list);
}
