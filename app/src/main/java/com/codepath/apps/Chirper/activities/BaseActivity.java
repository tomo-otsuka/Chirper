package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_bird);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(BaseActivity.this, SearchTimelineActivity.class);
                intent.putExtra("q", query);
                startActivity(intent);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    public void goHome(MenuItem mi) {
        Intent intent = new Intent(this, TimelineActivity.class);
        startActivity(intent);
    }

    public void goDirectMessage(MenuItem mi) {
        Intent intent = new Intent(this, DirectMessagesActivity.class);
        startActivity(intent);
    }

    public void onProfileView(MenuItem mi) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

}
