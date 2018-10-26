package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchItineraryActivity extends AppCompatActivity {

    ViewPager simpleViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation
        getSupportActionBar().setElevation(0); // remove shadow

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String query = extras.getString("query");

        // set activity title
        setTitle("Search for '" + query + "'");

        // get the reference of ViewPager and TabLayout
        simpleViewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);

        // Create a new tabs
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("New"); // set the Text for the first Tab
        tabLayout.addTab(firstTab); // add  the tab at in the TabLayout

        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Top");
        tabLayout.addTab(secondTab);

        TabLayout.Tab thirdTab = tabLayout.newTab();
        thirdTab.setText("Trending");
        tabLayout.addTab(thirdTab);

        ItinerariesPagerAdapter adapter = new ItinerariesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), "search", query);
        simpleViewPager.setAdapter(adapter);
        // addOnPageChangeListener event change the tab on slide
        simpleViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                simpleViewPager.setCurrentItem(tab.getPosition());
                //Log.i("TAG", "onTabSelected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //Log.i("TAG", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //Log.i("TAG", "onTabReselected: " + tab.getPosition());
            }
        });
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
