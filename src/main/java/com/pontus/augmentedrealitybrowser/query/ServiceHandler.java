package com.pontus.augmentedrealitybrowser.query;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by usuario on 8/17/2016.
 */
public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     *
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     */
    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if(method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if(params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            } else if(method == GET) {
                // appending params to url
                if(params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    public ArrayList<HashMap<String, String>> parseWikiJsonToHashMap(String jsonStr) {

        ArrayList<HashMap<String, String>> listToSaveTo = new ArrayList<HashMap<String, String>>();
        JSONObject searchJson = null;
        JSONObject queryObject = null;
        JSONArray mJsonArray= null;
        if(jsonStr != null) {
            try {
              //  Log.d("Cast:", "JSONObject(jsonStr)");
                searchJson = new JSONObject(jsonStr);
           //     Log.d("Cast:", "OK!!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
               // Log.d("Cast:", "JSONObject(query)");
                queryObject = searchJson.getJSONObject("query");
              //  Log.d("Cast:", "OK!!");

             //   Log.d("Cast:", "JSONObject(pages)");
             //   Log.d("String value:", queryObject.toString());

                mJsonArray = queryObject.getJSONArray("geosearch");

                for(int pos=0; pos<mJsonArray.length(); pos++) {


                  //  Log.d("Cast:", "JsonObject("+pos+")");
                    JSONObject json = mJsonArray.getJSONObject(pos);
                  //  Log.d("Cast:", "OK!!");

                    String name = json.getString("title");
                    String scope = json.getString("pageid");
                    String id = json.getString("dist");

                    // tmp hashmap for single contact
                    HashMap<String, String> contact = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    contact.put(OnWikiResultReadyListener.TAG_NAME, name);
                    contact.put(OnWikiResultReadyListener.TAG_SCOPE, scope);
                    contact.put(OnWikiResultReadyListener.TAG_ID, id);

                 //   Log.d("title",name);
                //    Log.d("pageid",scope);
               //     Log.d("distance",id);


                    // adding contact to contact list
                    listToSaveTo.add(contact);
                }

                return listToSaveTo;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }

    public ArrayList<HashMap<String, String>> parseGooglePlacesJsonToHashMap(String jsonStr) {

        ArrayList<HashMap<String, String>> listToSaveTo = new ArrayList<HashMap<String, String>>();
        JSONObject searchJson = null;
        JSONArray queryObject = null;

        if(jsonStr != null) {
            try {
             //   Log.d("Cast:", "JSONObject(jsonStr)");
                searchJson = new JSONObject(jsonStr);
           //     Log.d("Cast:", "OK!!");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
          //      Log.d("Cast:", "JSONArray(query)");
                queryObject = searchJson.getJSONArray("results");
         //       Log.d("Cast:", "OK!!");


                for(int pos=0; pos<queryObject.length(); pos++) {


                 //   Log.d("Cast:", "JsonObject("+pos+")");
                    JSONObject json = queryObject.getJSONObject(pos);
                 //   Log.d("Cast:", "OK!!");

                    String name = json.getString("name");
                    String scope = json.getString("place_id");
                    String id = json.getString("id");

                    // tmp hashmap for single contact
                    HashMap<String, String> contact = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    contact.put(OnGoogleResultReadyListener.TAG_NAME, name);
                    contact.put(OnGoogleResultReadyListener.TAG_SCOPE, scope);
                    contact.put(OnGoogleResultReadyListener.TAG_ID, id);

                   // adding contact to contact list
                    listToSaveTo.add(contact);
                }

                return listToSaveTo;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
        return null;
    }
}
