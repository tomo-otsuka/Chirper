package com.codepath.apps.Chirper;

import com.codepath.apps.Chirper.utils.Network;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.widget.Toast;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "FrByRiTB87aEtz910UvJAlxE9";       // Change this
	public static final String REST_CONSUMER_SECRET = "XhDCxmGo0DJkqlvDbWGVFcTxErynbreUqMXaj1KyKaMu5fQW4l"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cbchirper"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    private void get(String apiUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        if (Network.isNetworkAvailable(context) && Network.isOnline()) {
            client.get(apiUrl, params, handler);
        } else {
            Toast.makeText(context, "Network cannot be detected", Toast.LENGTH_LONG).show();
        }
    }

    private void post(String apiUrl, RequestParams params, AsyncHttpResponseHandler handler) {
        if (Network.isNetworkAvailable(context) && Network.isOnline()) {
            client.post(apiUrl, params, handler);
        } else {
            Toast.makeText(context, "Network cannot be detected", Toast.LENGTH_LONG).show();
        }
    }

	public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("since_id", 1);
        if (maxId >= 0) {
            params.put("max_id", maxId);
        }

		get(apiUrl, params, handler);
	}

    public void postTweet(String text, long replyToId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/update.json");
        RequestParams params = new RequestParams();
        params.put("status", text);
        if (replyToId > 0) {
            params.put("in_reply_to_status_id", replyToId);
        }

        post(apiUrl, params, handler);
    }

    public void postRetweet(long tweetId, AsyncHttpResponseHandler handler) {
        String url = String.format("statuses/retweet/%s.json", tweetId);
        String apiUrl = getApiUrl(url);
        RequestParams params = new RequestParams();
        params.put("id", tweetId);

        post(apiUrl, params, handler);
    }

    public void postUnretweet(long tweetId, AsyncHttpResponseHandler handler) {
        String url = String.format("statuses/unretweet/%s.json", tweetId);
        String apiUrl = getApiUrl(url);
        RequestParams params = new RequestParams();
        params.put("id", tweetId);

        post(apiUrl, params, handler);
    }

    public void postLike(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/create.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);

        post(apiUrl, params, handler);
    }

    public void postUnlike(long tweetId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/destroy.json");
        RequestParams params = new RequestParams();
        params.put("id", tweetId);

        post(apiUrl, params, handler);
    }

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}