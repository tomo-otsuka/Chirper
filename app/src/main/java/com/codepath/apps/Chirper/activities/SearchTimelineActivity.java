package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.adapters.TweetsAdapter;
import com.codepath.apps.Chirper.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.DividerItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchTimelineActivity extends BaseActivity implements ComposeTweetDialogFragment.TweetListener {

    private ArrayList<Tweet> tweets;
    private TweetsAdapter tweetsAdapter;
    private TwitterClient client;
    private String q;

    @BindView(R.id.rvTweets) RecyclerView rvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tweets_list);
        ButterKnife.bind(this);

        Activity activity = this;
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(activity, tweets);
        rvTweets.setAdapter(tweetsAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(activity));
        rvTweets.addItemDecoration(new DividerItemDecoration(activity));

        Intent intent = getIntent();
        q = intent.getStringExtra("q");

        populateTweets();

        setEventListeners();
    }

    private void populateTweets() {
        long maxId = -1;
        if (tweets.size() > 0) {
            maxId = tweets.get(tweets.size() - 1).getNetworkId();
        }
        showProgressBar();
        client.searchTweets(q, maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ArrayList<Tweet> newTweets = null;
                try {
                    newTweets = Tweet.fromJSONArray(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tweets.addAll(newTweets);
                tweetsAdapter.notifyDataSetChanged();
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(SearchTimelineActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }

    private void setEventListeners() {
        rvTweets.addOnScrollListener(new com.codepath.apps.Chirper.utils.EndlessRecyclerViewScrollListener((LinearLayoutManager) rvTweets.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount >= 25) {
                    populateTweets();
                }
            }
        });
    }

    @Override
    public void onTweet() {
        tweets.clear();
        tweetsAdapter.notifyDataSetChanged();
        populateTweets();
    }
}
