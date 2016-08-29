package com.codepath.apps.Chirper.fragments;

import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.Network;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

public class MentionsTimelineFragment extends TweetsListFragment {

    @Override
    void populateTimeline() {
        TwitterClient client = getTwitterClient();
        final Activity activity = getActivity();
        if (Network.isNetworkAvailable(activity) && Network.isOnline()) {
            long maxId = getTweetsMaxId();
            TweetsListListener listener = (TweetsListListener) getActivity();
            listener.onPopulateStarted();
            client.getMentionsTimeline(maxId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    addTweets(Tweet.fromJSONArray(response));

                    swipeContainer.setRefreshing(false);
                    TweetsListListener listener = (TweetsListListener) getActivity();
                    listener.onPopulateFinished();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    String errorString = null;
                    if (errorResponse == null) {
                        errorString = "Unknown network error";
                    } else {
                        errorString = errorResponse.toString();
                    }
                    Toast.makeText(activity, errorString, Toast.LENGTH_LONG).show();

                    swipeContainer.setRefreshing(false);
                    TweetsListListener listener = (TweetsListListener) getActivity();
                    listener.onPopulateFinished();
                }
            });
        } else {
            Toast.makeText(activity, "No network detected. Loading tweets from cache", Toast.LENGTH_LONG).show();

            loadCachedTweets();

            swipeContainer.setRefreshing(false);
            TweetsListListener listener = (TweetsListListener) getActivity();
            listener.onPopulateFinished();
        }
    }
}
