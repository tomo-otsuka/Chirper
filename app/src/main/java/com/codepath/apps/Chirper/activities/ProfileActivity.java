package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.fragments.UserTimelineFragment;
import com.codepath.apps.Chirper.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.tvBio) TextView tvBio;
    @BindView(R.id.tvFollowingCount) TextView tvFollowingCount;
    @BindView(R.id.tvFollowersCount) TextView tvFollowersCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        User userIntent = Parcels.unwrap(intent.getParcelableExtra("user"));

        client = TwitterApplication.getRestClient();
        client.getUserInfo(userIntent, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = new User(response);
                    populateProfileHeader(user);
                } catch (JSONException e) {
                    Toast.makeText(ProfileActivity.this, "Could not parse user object", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        if (savedInstanceState == null) {
            String screenName = null;
            if (userIntent != null) {
                screenName = userIntent.getScreenName();
            }
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(screenName);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }
    }

    private void populateProfileHeader(User user) {
        tvUsername.setText(user.getName());
        tvScreenName.setText(String.format("@%s", user.getScreenName()));
        tvBio.setText(user.getBio());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivProfileImage);

        String strFollowersCount = String.format("%s", user.getFollowersCount());
        String strFollowingCount = String.format("%s", user.getFollowingCount());

        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        ForegroundColorSpan greyForegroundColorSpan = new ForegroundColorSpan(
                ContextCompat.getColor(this, android.R.color.darker_gray));
        SpannableStringBuilder ssbFollowers = new SpannableStringBuilder(strFollowersCount);
        SpannableStringBuilder ssbFollowing = new SpannableStringBuilder(strFollowingCount);

        ssbFollowers.append(" FOLLOWERS");
        ssbFollowing.append(" FOLLOWING");

        ssbFollowers.setSpan(boldStyle, 0, strFollowersCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbFollowing.setSpan(boldStyle, 0, strFollowingCount.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ssbFollowers.setSpan(greyForegroundColorSpan, strFollowersCount.length(), ssbFollowers.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssbFollowing.setSpan(greyForegroundColorSpan, strFollowingCount.length(), ssbFollowing.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvFollowersCount.setText(ssbFollowers, TextView.BufferType.EDITABLE);
        tvFollowingCount.setText(ssbFollowing, TextView.BufferType.EDITABLE);
    }

    @OnClick(R.id.tvFollowersCount)
    public void showFollowers(View v) {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        intent.putExtra("type", "Followers");
        startActivity(intent);
    }

    @OnClick(R.id.tvFollowingCount)
    public void showFollowing(View v) {
        Intent intent = new Intent(this, UsersActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        intent.putExtra("type", "Following");
        startActivity(intent);
    }
}
