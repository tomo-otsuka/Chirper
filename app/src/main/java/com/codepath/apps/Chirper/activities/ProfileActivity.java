package com.codepath.apps.Chirper.activities;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.fragments.LikedTimelineFragment;
import com.codepath.apps.Chirper.fragments.TweetsListFragment;
import com.codepath.apps.Chirper.fragments.UserMediaFragment;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileActivity extends BaseActivity implements TweetsListFragment.TweetsListListener {

    TwitterClient client;
    User user;
    String screenName;

    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivProfileBackgroundImage) ImageView ivProfileBackgroundImage;
    @BindView(R.id.tvBio) TextView tvBio;
    @BindView(R.id.tvFollowingCount) TextView tvFollowingCount;
    @BindView(R.id.tvFollowersCount) TextView tvFollowersCount;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabs) PagerSlidingTabStrip tabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        screenName = intent.getStringExtra("screenName");

        client = TwitterApplication.getRestClient();
        client.getUserInfo(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = new User(response);
                    populateProfileHeader(user);
                    user = user.getOrCreate();
                    getUserInfoCallback(savedInstanceState);
                } catch (JSONException e) {
                    Toast.makeText(ProfileActivity.this, "Could not parse user object", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void populateProfileHeader(User user) {
        tvUsername.setText(user.getName());
        tvScreenName.setText(String.format("@%s", user.getScreenName()));
        tvBio.setText(user.getBio());
        Picasso.with(this).load(user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(ivProfileImage);
        Picasso.with(this).load(user.getProfileBackgroundImageUrl())
                .fit()
                .centerCrop()
                .into(ivProfileBackgroundImage);

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

    @Override
    public void onPopulateStarted() {
        showProgressBar();
    }

    @Override
    public void onPopulateFinished() {
        hideProgressBar();
    }

    public void getUserInfoCallback(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            ProfilePagerAdapter pagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(pagerAdapter);
            tabStrip.setViewPager(viewPager);
        }
    }

    public class ProfilePagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Tweets", "Media", "Liked"};

        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return UserTimelineFragment.newInstance(screenName);
            } else if (position == 1) {
                return UserMediaFragment.newInstance(user);
            } else if (position == 2) {
                return LikedTimelineFragment.newInstance(user);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }
}
