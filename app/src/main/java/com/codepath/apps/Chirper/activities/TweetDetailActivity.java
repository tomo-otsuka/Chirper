package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements ComposeTweetDialogFragment.TweetListener {

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivEntity) ImageView ivEntity;
    @BindView(R.id.ivRetweet) ImageView ivRetweet;
    @BindView(R.id.ivLike) ImageView ivLike;
    @BindView(R.id.tvRetweetCount) TextView tvRetweetCount;
    @BindView(R.id.tvLikeCount) TextView tvLikeCount;

    private Tweet mTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mTweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));

        tvUsername.setText(mTweet.getUser().getName());
        String screenName = String.format("@%s", mTweet.getUser().getScreenName());
        tvScreenName.setText(screenName);
        tvBody.setText(mTweet.getText());
        tvRelativeTime.setText(ParseRelativeDate.getRelativeTimeAgo(mTweet.getCreatedAt()));
        ivProfileImage.setImageResource(0);
        Picasso.with(this).load(mTweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(ivProfileImage);

        ivEntity.setImageResource(0);
        ArrayList<Entity> entities = mTweet.getEntities();
        if (entities.size() > 0) {
            Entity entity = entities.get(0);
            Picasso.with(this).load(entity.getUrl())
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(ivEntity);
        }

        if (mTweet.getRetweeted()) {
            ivRetweet.setImageResource(R.drawable.retweeted);
            tvRetweetCount.setTextColor(ContextCompat.getColor(this, R.color.retweeted));
        } else {
            ivRetweet.setImageResource(R.drawable.retweet);
            tvRetweetCount.setTextColor(ContextCompat.getColor(this, R.color.twitter_grey));
        }
        if (mTweet.getLiked()) {
            ivLike.setImageResource(R.drawable.liked);
            tvLikeCount.setTextColor(ContextCompat.getColor(this, R.color.liked));
        } else {
            ivLike.setImageResource(R.drawable.like);
            tvLikeCount.setTextColor(ContextCompat.getColor(this, R.color.twitter_grey));
        }

        tvRetweetCount.setText(String.format("%s", mTweet.getRetweetCount()));
        tvLikeCount.setText(String.format("%s", mTweet.getLikeCount()));
    }

    @OnClick({R.id.ivProfileImage, R.id.tvUsername, R.id.tvScreenName})
    public void openProfileActivity(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(mTweet.getUser()));
        v.getContext().startActivity(intent);
    }

    @OnClick(R.id.ivReply)
    public void showComposeTweetDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(
                mTweet.getNetworkId(), mTweet.getUser().getScreenName()
        );
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }

    @OnClick(R.id.ivRetweet)
    public void toggleRetweet(View v) {
        TwitterClient client = new TwitterClient(this);
        if (mTweet.getRetweeted()) {
            client.postUnretweet(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mTweet.setRetweeted(false);
                    mTweet.setRetweetCount(mTweet.getRetweetCount() - 1);
                    ivRetweet.setImageResource(R.drawable.retweet);
                    tvRetweetCount.setText(String.format("%s", mTweet.getRetweetCount()));
                    tvRetweetCount.setTextColor(ContextCompat.getColor(TweetDetailActivity.this, R.color.twitter_grey));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            client.postRetweet(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mTweet.setRetweeted(true);
                    mTweet.setRetweetCount(mTweet.getRetweetCount() + 1);
                    ivRetweet.setImageResource(R.drawable.retweeted);
                    tvRetweetCount.setText(String.format("%s", mTweet.getRetweetCount()));
                    tvRetweetCount.setTextColor(ContextCompat.getColor(TweetDetailActivity.this, R.color.retweeted));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @OnClick(R.id.ivLike)
    public void toggleLike(View v) {
        TwitterClient client = new TwitterClient(this);
        if (mTweet.getLiked()) {
            client.postUnlike(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mTweet.setLiked(false);
                    mTweet.setLikeCount(mTweet.getLikeCount() - 1);
                    ivLike.setImageResource(R.drawable.like);
                    tvLikeCount.setText(String.format("%s", mTweet.getLikeCount()));
                    tvLikeCount.setTextColor(ContextCompat.getColor(TweetDetailActivity.this, R.color.twitter_grey));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            client.postLike(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    mTweet.setLiked(true);
                    mTweet.setLikeCount(mTweet.getLikeCount() + 1);
                    ivLike.setImageResource(R.drawable.liked);
                    tvLikeCount.setText(String.format("%s", mTweet.getLikeCount()));
                    tvLikeCount.setTextColor(ContextCompat.getColor(TweetDetailActivity.this, R.color.liked));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onTweet() {}
}
