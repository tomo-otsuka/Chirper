package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterApplication;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.adapters.DirectMessagesAdapter;
import com.codepath.apps.Chirper.fragments.ComposeDirectMessageDialogFragment;
import com.codepath.apps.Chirper.models.DirectMessage;
import com.codepath.apps.Chirper.utils.DividerItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class DirectMessagesActivity extends BaseActivity implements ComposeDirectMessageDialogFragment.DirectMessageListener {

    private ArrayList<DirectMessage> directMessages;
    private DirectMessagesAdapter directMessagesAdapter;
    private TwitterClient client;

    @BindView(R.id.rvDirectMessages) RecyclerView rvDirectMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messages);
        ButterKnife.bind(this);

        Activity activity = this;
        client = TwitterApplication.getRestClient();
        directMessages = new ArrayList<>();
        directMessagesAdapter = new DirectMessagesAdapter(activity, directMessages);
        rvDirectMessages.setAdapter(directMessagesAdapter);
        rvDirectMessages.setLayoutManager(new LinearLayoutManager(activity));
        rvDirectMessages.addItemDecoration(new DividerItemDecoration(activity));

        Intent intent = getIntent();

        populateDirectMessages();

        setEventListeners();
    }

    @OnClick(R.id.ivComposeDirectMessage)
    public void showComposeDirectMessageDialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDirectMessageDialogFragment composeDirectMessageDialogFragment = ComposeDirectMessageDialogFragment.newInstance(
                null
        );
        composeDirectMessageDialogFragment.show(fm, "fragment_compose_direct_message");
    }

    private void populateDirectMessages() {
        long maxId = -1;
        if (directMessages.size() > 0) {
            maxId = directMessages.get(directMessages.size() - 1).getNetworkId();
        }
        final long finalMaxId = maxId;
        client.getDirectMessages(maxId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<DirectMessage> newDirectMessages = DirectMessage.fromJSONArray(response);
                directMessages.addAll(newDirectMessages);

                client.getDirectMessagesSent(finalMaxId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        ArrayList<DirectMessage> newDirectMessages = DirectMessage.fromJSONArray(response);
                        directMessages.addAll(newDirectMessages);

                        Collections.sort(directMessages, (o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

                        directMessagesAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(DirectMessagesActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(DirectMessagesActivity.this, errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setEventListeners() {
        rvDirectMessages.addOnScrollListener(new com.codepath.apps.Chirper.utils.EndlessRecyclerViewScrollListener((LinearLayoutManager) rvDirectMessages.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateDirectMessages();
            }
        });
    }

    @Override
    public void onDirectMessage() {
        directMessages.clear();
        directMessagesAdapter.notifyDataSetChanged();
        populateDirectMessages();
    }
}
