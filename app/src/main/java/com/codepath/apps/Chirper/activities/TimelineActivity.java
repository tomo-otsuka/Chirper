package com.codepath.apps.Chirper.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.adapters.TweetsAdapter;
import com.codepath.apps.Chirper.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.DividerItemDecoration;
import com.codepath.apps.Chirper.utils.Network;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetDialogFragment.TweetListener {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter tweetsAdapter;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fabComposeTweet) FloatingActionButton fabComposeTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(this, tweets);
        rvTweets.setAdapter(tweetsAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.addItemDecoration(new DividerItemDecoration(this));

        client = TwitterApplication.getRestClient();
        populateTimeline();

        setEventListeners();
    }

    private void setEventListeners() {
        rvTweets.addOnScrollListener(new com.codepath.apps.Chirper.utils.EndlessRecyclerViewScrollListener((LinearLayoutManager) rvTweets.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshTimeline();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void populateTimeline() {
        if (Network.isNetworkAvailable(this) && Network.isOnline()) {
            long maxId = -1;
            if (tweets.size() > 0) {
                maxId = tweets.get(tweets.size() - 1).getNetworkId();
            }
            client.getHomeTimeline(maxId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    tweets.addAll(Tweet.fromJSONArray(response));
                    tweetsAdapter.notifyItemRangeInserted(tweets.size() - 20, 20);

                    swipeContainer.setRefreshing(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(TimelineActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();

                    swipeContainer.setRefreshing(false);
                }
            });
        } else {
            Toast.makeText(this, "No network detected", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshTimeline() {
        int numTweets = tweets.size();
        tweets.clear();
        tweetsAdapter.notifyItemRangeRemoved(0, numTweets);

        populateTimeline();
    }

    @OnClick(R.id.fabComposeTweet)
    public void showComposeTweetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance();
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onTweet() {
        refreshTimeline();
    }
}
