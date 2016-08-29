package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.adapters.UsersAdapter;
import com.codepath.apps.Chirper.models.User;
import com.codepath.apps.Chirper.utils.DividerItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class UsersActivity extends BaseActivity {

    private ArrayList<User> users;
    private UsersAdapter usersAdapter;
    private TwitterClient client;
    private long cursor = -1;
    private String type;
    private User user;
    
    @BindView(R.id.rvUsers) RecyclerView rvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        ButterKnife.bind(this);

        Activity activity = this;
        client = TwitterApplication.getRestClient();
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(activity, users);
        rvUsers.setAdapter(usersAdapter);
        rvUsers.setLayoutManager(new LinearLayoutManager(activity));
        rvUsers.addItemDecoration(new DividerItemDecoration(activity));

        Intent intent = getIntent();
        user = Parcels.unwrap(intent.getParcelableExtra("user"));
        type = intent.getStringExtra("type");

        populateUsers();

        setEventListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        getSupportActionBar().setTitle(String.format("@%s %s", user.getScreenName(), type));
        return true;
    }

    private void populateUsers() {
        if (type.equals("Followers")) {
            populateFollowers(user);
        } else if (type.equals("Following")) {
            populateFollowing(user);
        }
    }

    private void setEventListeners() {
        rvUsers.addOnScrollListener(new com.codepath.apps.Chirper.utils.EndlessRecyclerViewScrollListener((LinearLayoutManager) rvUsers.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount >= 25) {
                    populateUsers();
                }
            }
        });
    }


    private void populateFollowers(User user) {
        showProgressBar();
        client.getUserFollowers(user, cursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray usersJsonArray = response.getJSONArray("users");
                    ArrayList<User> newUsers = User.fromJSONArray(usersJsonArray);
                    users.addAll(newUsers);
                    usersAdapter.notifyItemRangeInserted(users.size() - newUsers.size(), newUsers.size());
                    cursor = response.getLong("next_cursor");
                    hideProgressBar();
                } catch (JSONException e) {
                    Toast.makeText(UsersActivity.this, "Failed to parse users", Toast.LENGTH_LONG).show();
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(UsersActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }

    private void populateFollowing(User user) {
        showProgressBar();
        client.getUserFollowing(user, cursor, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray usersJsonArray = response.getJSONArray("users");
                    ArrayList<User> newUsers = User.fromJSONArray(usersJsonArray);
                    users.addAll(newUsers);
                    usersAdapter.notifyItemRangeInserted(users.size() - newUsers.size(), newUsers.size());
                    cursor = response.getLong("next_cursor");
                    hideProgressBar();
                } catch (JSONException e) {
                    Toast.makeText(UsersActivity.this, "Failed to parse users", Toast.LENGTH_LONG).show();
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(UsersActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }
}
