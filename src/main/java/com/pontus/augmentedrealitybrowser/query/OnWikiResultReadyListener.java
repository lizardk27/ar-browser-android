package com.pontus.augmentedrealitybrowser.query;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gabriel yibirin on 8/29/2016.
 */
public interface OnWikiResultReadyListener {

    static final String TAG_NAME="NAME";
    static final String TAG_SCOPE="SCOPE";
    static final String TAG_ID="ID";

    void onWikiResultReady(ArrayList<HashMap<String, String>> string_result_list);
}
