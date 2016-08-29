package com.codepath.apps.Chirper.fragments;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

public class ComposeTweetDialogFragment extends DialogFragment {

    private int MAX_CHARS_PER_TWEET = 140;

    public interface TweetListener {
        public void onTweet();
    }

    @BindView(R.id.etTweetText) EditText etTweetText;
    @BindView(R.id.btnSubmitTweet) Button btnSubmitTweet;
    @BindView(R.id.tvRemainingCharCount) TextView tvRemainingCharCount;

    public ComposeTweetDialogFragment() {}

    public static ComposeTweetDialogFragment newInstance(long replyToId, String replyToScreenName) {
        ComposeTweetDialogFragment frag = new ComposeTweetDialogFragment();
        Bundle args = new Bundle();
        args.putLong("replyToId", replyToId);
        args.putString("replyToScreenName", replyToScreenName);
        frag.setArguments(args);
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

        String replyToScreenName = getArguments().getString("replyToScreenName");
        String tweetText = "";
        if (replyToScreenName != null && replyToScreenName.length() != 0) {
            tweetText = String.format("@%s ", replyToScreenName);
            etTweetText.setText(tweetText);
        }
        tvRemainingCharCount.setText(String.format("%s", MAX_CHARS_PER_TWEET - tweetText.length()));
        etTweetText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnTextChanged(R.id.etTweetText)
    public void updateRemainingCharCount(CharSequence tweetText) {
        int remainingLength = MAX_CHARS_PER_TWEET - tweetText.length();
        tvRemainingCharCount.setText(String.format("%s", remainingLength));

        if (remainingLength < 0) {
            btnSubmitTweet.setEnabled(false);
            int grey = ContextCompat.getColor(getContext(), R.color.twitter_grey);
            btnSubmitTweet.setBackgroundColor(grey);
            tvRemainingCharCount.setTextColor(Color.RED);
        }

        if (!btnSubmitTweet.isEnabled() && remainingLength >= 0) {
            btnSubmitTweet.setEnabled(true);

            final TypedValue value = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
            int primary = value.data;

            btnSubmitTweet.setBackgroundColor(primary);
            tvRemainingCharCount.setTextColor(Color.LTGRAY);
        }
    }

    @OnClick(R.id.btnSubmitTweet)
    public void submitTweet() {
        long replyToId = getArguments().getLong("replyToId");
        TwitterClient client = new TwitterClient(getContext());
        client.postTweet(etTweetText.getText().toString(), replyToId, new JsonHttpResponseHandler() {
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
