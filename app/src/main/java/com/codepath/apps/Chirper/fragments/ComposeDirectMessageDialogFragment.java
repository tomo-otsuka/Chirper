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

public class ComposeDirectMessageDialogFragment extends DialogFragment {

    private long mReplyToId;

    public interface DirectMessageListener {
        public void onDirectMessage();
    }

    @BindView(R.id.etSendTo) EditText etSendTo;
    @BindView(R.id.etDirectMessageText) EditText etDirectMessageText;
    @BindView(R.id.btnSend) Button btnSend;

    public ComposeDirectMessageDialogFragment() {
    }

    public static ComposeDirectMessageDialogFragment newInstance(String replyToScreenName) {
        ComposeDirectMessageDialogFragment frag = new ComposeDirectMessageDialogFragment();
        Bundle args = new Bundle();
        args.putString("replyToScreenName", replyToScreenName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_direct_message, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getDialog().setTitle("Compose new Direct Message");

        String screenName = getArguments().getString("replyToScreenName");
        if (screenName != null) {
            etSendTo.setText(String.format("@%s", screenName));
        }
        etDirectMessageText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @OnClick(R.id.btnSend)
    public void send() {
        String screenName = getArguments().getString("replyToScreenName");
        TwitterClient client = new TwitterClient(getContext());
        client.postDirectMessage(screenName, etDirectMessageText.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                DirectMessageListener listener = (DirectMessageListener) getActivity();
                listener.onDirectMessage();

                dismiss();
                Toast.makeText(getContext(), "Direct Message successful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), errorResponse.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
