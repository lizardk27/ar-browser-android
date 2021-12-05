package com.pontus.augmentedrealitybrowser.query;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pontus.augmentedrealitybrowser.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class TwitterQueryManager {

    private final OnTwitterResultReadyListener twitter_listener;

    //TWITTER
    private static final String TWITTER_KEY = "lYtPhg7lDgYqhODHhzBIZzWLZ";
    private static final String TWITTER_SECRET = "XhrKa6Bb6TYHIwXd5DM6hMi0EHQKlCzfAFilQrgG0rd6sGsRsh";

    private final double latitude;
    private final double longitude;
    private final Context mContext;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public TwitterQueryManager(OnTwitterResultReadyListener twitter_listener, Context context, double latitude, double longitude) {
        this.twitter_listener = twitter_listener;
        this.mContext = context;
        this.latitude = latitude;
        this.longitude = longitude;
        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Fabric.with(mContext, new Twitter(authConfig));

        TwitterCore.getInstance().logInGuest(new Callback() {
            @Override
            public void success(Result result) {

                Toast.makeText(mContext, result.toString(), Toast.LENGTH_SHORT).show();
                AppSession session = (AppSession) result.data;
                getTweets();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });


        client = new GoogleApiClient.Builder(mContext).addApi(AppIndex.API).build();
    }

    public void getTweets() {

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        final SearchService service = Twitter.getApiClient().getSearchService();


        service.tweets("", new Geocode(latitude, longitude, 1, Geocode.Distance.KILOMETERS), null, null, null, null, null, null,
                null, true, new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {

                        ArrayList<HashMap<String, String>> listToSaveTo = new ArrayList<HashMap<String, String>>();
                        final List<Tweet> tweets = searchResult.data.tweets;
                    //    Toast.makeText(mContext, "Result size=" + tweets.size(), Toast.LENGTH_SHORT).show();

                        for (Tweet tweet : tweets) {
                          //  Log.v("tweet", tweet.toString());

                            HashMap<String, String> contact = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            contact.put(OnTwitterResultReadyListener.TAG_NAME, tweet.user.name);
                            contact.put(OnTwitterResultReadyListener.TAG_TEXT, tweet.text);
                            contact.put(OnTwitterResultReadyListener.TAG_SOURCE, tweet.source);


                            // adding contact to contact list
                            listToSaveTo.add(contact);
                        }

                        twitter_listener.onTwitterResultReady(listToSaveTo);

                    }

                    @Override
                    public void failure(TwitterException error) {
                        System.out.println("Exception: " + error.getMessage());
                        System.out.println("Why: " + error.getCause());
                        error.printStackTrace();
                        System.out.println("----------------------------------------------");
                    }
                });

    }

}



