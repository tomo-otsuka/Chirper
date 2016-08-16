package com.codepath.apps.Chirper.fragments;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ComposeTweetDialogFragment extends DialogFragment {

    public interface TweetListener {
        public void onTweet();
    }

    @BindView(R.id.etTweetText) EditText etTweetText;
    @BindView(R.id.btnSubmitTweet) Button btnSubmitTweet;

    public ComposeTweetDialogFragment() {}

    public static ComposeTweetDialogFragment newInstance() {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getDialog().setTitle("Compose new Tweet");
        etTweetText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnClick(R.id.btnSubmitTweet)
    public void submitTweet(View view) {
        TwitterClient client = new TwitterClient(getContext());
        client.postTweet(etTweetText.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                TweetListener listener = (TweetListener) getActivity();
                listener.onTweet();

                dismiss();
                Toast.makeText(getContext(), "Tweet successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
