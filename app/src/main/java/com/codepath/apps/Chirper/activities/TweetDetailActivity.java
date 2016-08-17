package com.codepath.apps.Chirper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivEntity) ImageView ivEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Tweet tweet = (Tweet) Parcels.unwrap(intent.getParcelableExtra("tweet"));

        tvUsername.setText(tweet.getUser().getName());
        String screenName = String.format("@%s", tweet.getUser().getScreenName());
        tvScreenName.setText(screenName);
        tvBody.setText(tweet.getText());
        tvRelativeTime.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt()));
        ivProfileImage.setImageResource(0);
        Picasso.with(this).load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(ivProfileImage);

        ivEntity.setImageResource(0);
        ArrayList<Entity> entities = tweet.getEntities();
        if (entities.size() > 0) {
            Entity entity = entities.get(0);
            Picasso.with(this).load(entity.getUrl())
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(ivEntity);
        }
    }
}
