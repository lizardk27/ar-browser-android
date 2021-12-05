package com.pontus.augmentedrealitybrowser.query;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by usuario on 8/29/2016.
 */
public class GetWikiResultsAsync extends AsyncTask<Void, Void, Void> {

    ProgressDialog pDialog;
    private final String wiki_url;
    private final  OnWikiResultReadyListener wiki_listener;

    private ArrayList<HashMap<String, String>> string_result_list;

    public GetWikiResultsAsync(OnWikiResultReadyListener wiki_listener, String url) {
        this.wiki_listener=wiki_listener;
        wiki_url = url;
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
        String jsonStr = sh.makeServiceCall(wiki_url, ServiceHandler.GET);

        // Process json response as String and save into wikiResultsList
        string_result_list = sh.parseWikiJsonToHashMap(jsonStr);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        try {
            if(pDialog.isShowing())
                pDialog.dismiss();
        } catch (Exception e) {
            //  e.printStackTrace();
        }
        wiki_listener.onWikiResultReady(string_result_list);

    }
}