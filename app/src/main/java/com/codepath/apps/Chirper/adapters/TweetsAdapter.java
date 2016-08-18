package com.codepath.apps.Chirper.adapters;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.TwitterClient;
import com.codepath.apps.Chirper.activities.TweetDetailActivity;
import com.codepath.apps.Chirper.fragments.ComposeTweetDialogFragment;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private ArrayList<Tweet> mTweets;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.ivEntity) ImageView ivEntity;
        @BindView(R.id.ivRetweet) ImageView ivRetweet;
        @BindView(R.id.ivLike) ImageView ivLike;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Tweet tweet = mTweets.get(position);
            Intent intent = new Intent(v.getContext(), TweetDetailActivity.class);
            intent.putExtra("tweet", Parcels.wrap(tweet));
            v.getContext().startActivity(intent);
        }

        @OnClick(R.id.ivReply)
        public void showComposeTweetDialog(View v) {
            int position = getLayoutPosition();
            Tweet tweet = mTweets.get(position);
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            ComposeTweetDialogFragment composeTweetDialogFragment = ComposeTweetDialogFragment.newInstance(
                    tweet.getNetworkId(), tweet.getUser().getScreenName()
            );
            composeTweetDialogFragment.show(fm, "fragment_compose_tweet");
        }

        @OnClick(R.id.ivRetweet)
        public void postRetweet(View v) {
            int position = getLayoutPosition();
            Tweet tweet = mTweets.get(position);
            TwitterClient client = new TwitterClient(mContext);
            client.postRetweet(tweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    ivRetweet.setImageResource(R.drawable.retweeted);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(mContext, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

        @OnClick(R.id.ivLike)
        public void postLike(View v) {
            int position = getLayoutPosition();
            Tweet tweet = mTweets.get(position);
            TwitterClient client = new TwitterClient(mContext);
            client.postLike(tweet.getNetworkId(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    ivLike.setImageResource(R.drawable.liked);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Toast.makeText(mContext, errorResponse.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public TweetsAdapter(Context context, ArrayList<Tweet> tweets) {
        mContext = context;
        mTweets = tweets;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);

        holder.tvUsername.setText(tweet.getUser().getName());
        String screenName = String.format("@%s", tweet.getUser().getScreenName());
        holder.tvScreenName.setText(screenName);
        holder.tvBody.setText(tweet.getText());
        holder.tvRelativeTime.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.ivProfileImage.setImageResource(0);
        Picasso.with(mContext).load(tweet.getUser().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(holder.ivProfileImage);

        holder.ivEntity.setImageResource(0);
        ArrayList<Entity> entities = tweet.getEntities();
        if (entities.size() > 0) {
            Entity entity = entities.get(0);
            Picasso.with(mContext).load(entity.getUrl())
                    .transform(new RoundedCornersTransformation(10, 10))
                    .into(holder.ivEntity);
        }

        if (tweet.getRetweeted()) {
            holder.ivRetweet.setImageResource(R.drawable.retweeted);
        }
        if (tweet.getLiked()) {
            holder.ivLike.setImageResource(R.drawable.liked);
        }
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
