package com.pontus.augmentedrealitybrowser.query;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.pontus.augmentedrealitybrowser.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by usuario on 8/29/2016.
 */
public class GetGooglePlacesResultsAsync extends AsyncTask<Void, Void, Void> {

    ProgressDialog pDialog;
    String _url;
    private final OnGoogleResultReadyListener google_listener;
    private ArrayList<HashMap<String, String>> string_result_list;

    public GetGooglePlacesResultsAsync(OnGoogleResultReadyListener google_listener, String url) {
        this._url=url;
        this.google_listener = google_listener;
    }

    @Override

    protected void onPreExecute() {
        super.onPreExecute();
        // Showing progress dialog
        try {
            pDialog = new ProgressDialog(null);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        } catch (Exception e) {
            //  e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to wiki_url and getting response
        String jsonStr = sh.makeServiceCall(_url, ServiceHandler.GET);

        //Log.d("JSON RESULT", jsonStr);
        // Process json response as String and save into wikiResultsList
        string_result_list = sh.parseGooglePlacesJsonToHashMap(jsonStr);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        super.onPostExecute(result);
        // Dismiss the progress dialog
        try {
            if(pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        if (string_result_list!=null && string_result_list.size()>0)
        google_listener.onGoogleResultReady(string_result_list);

    }
}