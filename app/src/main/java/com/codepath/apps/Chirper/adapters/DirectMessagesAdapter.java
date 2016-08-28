package com.codepath.apps.Chirper.adapters;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.activities.ProfileActivity;
import com.codepath.apps.Chirper.fragments.ComposeDirectMessageDialogFragment;
import com.codepath.apps.Chirper.models.DirectMessage;
import com.codepath.apps.Chirper.utils.ParseRelativeDate;
import com.squareup.picasso.Picasso;

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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class DirectMessagesAdapter extends RecyclerView.Adapter<DirectMessagesAdapter.ViewHolder> {
    private ArrayList<DirectMessage> mDirectMessages;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindView(R.id.tvScreenName) TextView tvScreenName;
        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvText) TextView tvText;
        @BindView(R.id.tvRelativeTime) TextView tvRelativeTime;
        @BindView(R.id.ivReply) ImageView ivReply;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            DirectMessage directMessage = mDirectMessages.get(position);
            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
            intent.putExtra("directMessage", Parcels.wrap(directMessage));
            v.getContext().startActivity(intent);
        }

        @OnClick(R.id.ivReply)
        public void showComposeDirectMessageDialog(View v) {
            int position = getLayoutPosition();
            DirectMessage directMessage = mDirectMessages.get(position);

            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            ComposeDirectMessageDialogFragment composeDirectMessageDialogFragment = ComposeDirectMessageDialogFragment.newInstance(
                    directMessage.getSender().getScreenName()
            );
            composeDirectMessageDialogFragment.show(fm, "fragment_compose_direct_message");
        }
    }

    public DirectMessagesAdapter(Context context, ArrayList<DirectMessage> directMessages) {
        mContext = context;
        mDirectMessages = directMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View directMessageView = inflater.inflate(R.layout.item_direct_message, parent, false);
        ViewHolder viewHolder = new ViewHolder(directMessageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DirectMessage directMessage = mDirectMessages.get(position);

        holder.tvUsername.setText(directMessage.getSender().getName());
        String screenName = String.format("@%s", directMessage.getSender().getScreenName());
        holder.tvScreenName.setText(screenName);
        holder.tvText.setText(directMessage.getText());
        holder.tvRelativeTime.setText(ParseRelativeDate.getRelativeTimeAgo(directMessage.getCreatedAt()));

        holder.ivProfileImage.setImageResource(0);
        Picasso.with(mContext).load(directMessage.getSender().getProfileImageUrl())
                .transform(new RoundedCornersTransformation(2, 2))
                .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mDirectMessages.size();
    }
}
