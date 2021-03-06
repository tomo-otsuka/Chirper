package com.codepath.apps.Chirper;

import com.codepath.apps.Chirper.models.User;
import com.codepath.apps.Chirper.utils.Network;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
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

    public void getUserInfo(String screenName, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("account/verify_credentials.json");
        RequestParams params = new RequestParams();
        if (screenName != null) {
            apiUrl = getApiUrl("users/show.json");
            params.put("screen_name", screenName);
        }

        get(apiUrl, params, handler);
    }

    public void getHomeTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/home_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);
        if (maxId >= 0) {
            params.put("max_id", maxId - 1);
        }

        get(apiUrl, params, handler);
    }

    public void getMentionsTimeline(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/mentions_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId >= 0) {
            params.put("max_id", maxId - 1);
        }

        get(apiUrl, params, handler);
    }

    public void getUserTimeline(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("statuses/user_timeline.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId >= 0) {
            params.put("max_id", maxId - 1);
        }
        params.put("screen_name", screenName);

        get(apiUrl, params, handler);
    }

    public void searchTweets(String q, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("search/tweets.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId >= 0) {
            params.put("max_id", maxId - 1);
        }
        params.put("q", q);

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

    public void getUserFollowers(User user, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("followers/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", user.getNetworkId());
        params.put("count", 25);
        if (cursor > 0) {
            params.put("cursor", cursor);
        }
        params.put("skip_status", true);

        get(apiUrl, params, handler);
    }

    public void getUserFollowing(User user, long cursor, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("friends/list.json");
        RequestParams params = new RequestParams();
        params.put("user_id", user.getNetworkId());
        params.put("count", 25);
        if (cursor > 0) {
            params.put("cursor", cursor);
        }
        params.put("skip_status", true);

        get(apiUrl, params, handler);
    }

    public void getDirectMessages(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        params.put("skip_status", true);

        get(apiUrl, params, handler);
    }

    public void getDirectMessagesSent(long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages/sent.json");
        RequestParams params = new RequestParams();
        params.put("count", 25);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }
        params.put("skip_status", true);

        get(apiUrl, params, handler);
    }

    public void postDirectMessage(String screenName, String text, JsonHttpResponseHandler handler) {
        String apiUrl = getApiUrl("direct_messages/new.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("text", text);

        post(apiUrl, params, handler);
    }

    public void getTweetsUserLiked(String screenName, long maxId, AsyncHttpResponseHandler handler) {
        String apiUrl = getApiUrl("favorites/list.json");
        RequestParams params = new RequestParams();
        params.put("screen_name", screenName);
        params.put("count", 25);
        if (maxId > 0) {
            params.put("max_id", maxId);
        }

        get(apiUrl, params, handler);
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