package com.codepath.apps.Chirper.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.activities.TweetDetailActivity;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }
}
