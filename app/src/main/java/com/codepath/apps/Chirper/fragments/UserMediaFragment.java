package com.codepath.apps.Chirper.fragments;

import com.codepath.apps.Chirper.R;
import com.codepath.apps.Chirper.adapters.MediaAdapter;
import com.codepath.apps.Chirper.models.Entity;
import com.codepath.apps.Chirper.models.User;
import com.codepath.apps.Chirper.utils.SpacesItemDecoration;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserMediaFragment extends Fragment {

    private ArrayList<Entity> media;
    private MediaAdapter mediaAdapter;

    @BindView(R.id.rvMedia) RecyclerView rvMedia;

    public static UserMediaFragment newInstance(User user) {
        UserMediaFragment userMediaFragment = new UserMediaFragment();
        Bundle args = new Bundle();
        args.putLong("userId", user.getId());
        userMediaFragment.setArguments(args);
        return userMediaFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_media_list, parent, false);
        ButterKnife.bind(this, v);

        Activity activity = getActivity();
        media = new ArrayList<>();
        mediaAdapter = new MediaAdapter(activity, media);
        rvMedia.setAdapter(mediaAdapter);

        int numCols = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 3;
        }
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(numCols, StaggeredGridLayoutManager.VERTICAL);
        rvMedia.setLayoutManager(gridLayoutManager);

        SpacesItemDecoration decoration = new SpacesItemDecoration(numCols, 16);
        rvMedia.addItemDecoration(decoration);

        populateMedia();

        setEventListeners();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void addAll(ArrayList<Entity> newMedia) {
        media.addAll(newMedia);
    }

    private void setEventListeners() {
        rvMedia.addOnScrollListener(new com.codepath.apps.Chirper.utils.EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvMedia.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateMedia();
            }
        });
    }

    public void populateMedia() {
        long userId = getArguments().getLong("userId");
        List<Entity> newMedia;
        if (getMediaMaxId() > 0) {
            newMedia = Entity.getByUserId(userId, getMediaMaxId());
        } else {
            newMedia = Entity.getByUserId(userId);
        }
        media.addAll(newMedia);
        mediaAdapter.notifyDataSetChanged();
    }

    public long getMediaMaxId() {
        long maxId = -1;
        if (media.size() > 0) {
            maxId = media.get(media.size() - 1).getNetworkId();
        }
        return maxId;
    }
}
