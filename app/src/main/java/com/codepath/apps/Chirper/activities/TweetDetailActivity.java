package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivEntity) ImageView ivEntity;

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
    }

    @OnClick(R.id.ivReply)
    public void showComposeTweetDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(
                mTweet.getNetworkId(), mTweet.getUser().getScreenName()
        );
        composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
    }
}
