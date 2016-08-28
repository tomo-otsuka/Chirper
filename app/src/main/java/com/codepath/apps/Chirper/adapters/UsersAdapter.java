package com.codepath.apps.Chirper.adapters;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.activities.ProfileActivity;
import com.codepath.apps.Chirper.models.User;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> mUsers;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            User user = mUsers.get(position);
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("user", Parcels.wrap(user));
            v.getContext().startActivity(intent);
        }
    }

    public UsersAdapter(Context context, ArrayList<User> users) {
        mContext = context;
        mUsers = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View userView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.tvUsername.setText(user.getName());
        String screenName = String.format("@%s", user.getScreenName());
        holder.tvScreenName.setText(screenName);
        holder.ivProfileImage.setImageResource(0);
        Picasso.with(mContext).load(user.getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
