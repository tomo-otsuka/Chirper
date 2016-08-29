package com.codepath.apps.Chirper.fragments;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.adapters.TweetsAdapter;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.DividerItemDecoration;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class TweetsListFragment extends Fragment implements ComposeTweetDialogFragment.TweetListener {

    public interface TweetsListListener {
        public void onPopulateStarted();

        public void onPopulateFinished();
    }

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter tweetsAdapter;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvTweets) RecyclerView rvTweets;
    @BindView(R.id.fabComposeTweet) FloatingActionButton fabComposeTweet;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        ButterKnife.bind(this, v);

        Activity activity = getActivity();
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(activity, tweets);
        rvTweets.setAdapter(tweetsAdapter);
        rvTweets.setLayoutManager(new LinearLayoutManager(activity));
        rvTweets.addItemDecoration(new DividerItemDecoration(activity));

        populateTimeline();

        setEventListeners();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addAll(ArrayList<Tweet> newTweets) {
        tweets.addAll(newTweets);
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

    @OnClick(R.id.fabComposeTweet)
    public void showComposeTweetDialog() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(0, null);
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    abstract void populateTimeline();

    private void refreshTimeline() {
        int numTweets = tweets.size();
        tweets.clear();
        tweetsAdapter.notifyItemRangeRemoved(0, numTweets);

        populateTimeline();
    }

    public TwitterClient getTwitterClient() {
        return client;
    }

    public long getTweetsMaxId() {
        long maxId = -1;
        if (tweets.size() > 0) {
            maxId = tweets.get(tweets.size() - 1).getNetworkId();
        }
        return maxId;
    }

    public void addTweets(ArrayList<Tweet> newTweets) {
        tweets.addAll(newTweets);
        int numLoaded = newTweets.size();
        tweetsAdapter.notifyItemRangeInserted(tweets.size() - numLoaded, numLoaded);

        for (Tweet tweet : newTweets) {
            tweet.saveIfNew();
        }
    }

    public void loadCachedTweets() {
        tweets.clear();
        tweets.addAll(Tweet.recentItems());
        tweetsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTweet() {
        refreshTimeline();
    }
}
