package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class CommentsNotificationActivity extends AppCompatActivity {

    // for swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // refresh fragment when perform swipe to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        finish();
                        startActivity(getIntent());

                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    // override default back navigation action
    // need finish(), to destroy the current activity so that it go back to last activity with last fragment
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
