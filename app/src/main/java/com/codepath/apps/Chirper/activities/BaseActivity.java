package com.codepath.apps.Chirper.activities;

import com.codepath.apps.Chirper.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.twitter_bird);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
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
