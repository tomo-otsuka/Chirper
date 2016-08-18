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

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivEntity) ImageView ivEntity;
    @BindView(R.id.ivLike) ImageView ivLike;

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

        if (mTweet.getLiked()) {
            ivLike.setImageResource(R.drawable.liked);
        }
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
    public void postRetweet(View v) {
        TwitterClient client = new TwitterClient(this);
        client.postRetweet(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(TweetDetailActivity.this, "Retweet successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.ivLike)
    public void postLike(View v) {
        TwitterClient client = new TwitterClient(this);
        client.postLike(mTweet.getNetworkId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ivLike.setImageResource(R.drawable.liked);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TweetDetailActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
