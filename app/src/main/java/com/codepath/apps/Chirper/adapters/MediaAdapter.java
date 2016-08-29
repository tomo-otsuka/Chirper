package com.codepath.apps.Chirper.adapters;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.activities.TweetDetailActivity;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.Tweet;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private ArrayList<Entity> mMedia;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivMedia) ImageView ivMedia;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Entity media = mMedia.get(position);
            Tweet tweet = media.getTweet();

            Intent intent = new Intent(mContext, TweetDetailActivity.class);
            intent.putExtra("tweet", Parcels.wrap(tweet));
            mContext.startActivity(intent);
        }
    }

    public MediaAdapter(Context context, ArrayList<Entity> Media) {
        mContext = context;
        mMedia = Media;
    }

    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mediaView = inflater.inflate(R.layout.item_media, parent, false);
        ViewHolder viewHolder = new ViewHolder(mediaView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MediaAdapter.ViewHolder holder, int position) {
        Entity media = mMedia.get(position);

        holder.ivMedia.setImageResource(0);
        Picasso.with(mContext).load(media.getUrl())
                .into(holder.ivMedia);
    }

    @Override
    public int getItemCount() {
        return mMedia.size();
    }
}
