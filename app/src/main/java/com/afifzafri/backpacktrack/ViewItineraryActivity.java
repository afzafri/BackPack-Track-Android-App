package com.afifzafri.backpacktrack;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class ViewItineraryActivity extends AppCompatActivity {

    ViewPager simpleViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation
        getSupportActionBar().setElevation(0); // remove shadow

        // declare variables
        String itinerary_id = null;
        String itinerary_title = null;
        String itinerary_user_id = null;
        Boolean viewComment = false;

        // If activity start from other intent get data pass through intent
        Bundle extras = getIntent().getExtras();
        itinerary_id = extras.getString("itinerary_id");
        itinerary_title = extras.getString("itinerary_title");
        itinerary_user_id = extras.getString("itinerary_user_id");
        viewComment = extras.getBoolean("viewComment");

        // If activity launched from web, get data from deeplink
        Uri deepLink = this.getIntent().getData();
        if (deepLink != null && deepLink.isHierarchical()) {
            itinerary_id = deepLink.getQueryParameter("itinerary_id");
            itinerary_title = deepLink.getQueryParameter("itinerary_title");
            itinerary_user_id = deepLink.getQueryParameter("itinerary_user_id");
        }

        // set activity action bar title
        setTitle(itinerary_title);

        // get the reference of ViewPager and TabLayout
        simpleViewPager = (ViewPager) findViewById(R.id.simpleViewPager);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);

        // Create a new tabs
        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("Activities"); // set the Text for the first Tab
        tabLayout.addTab(firstTab); // add  the tab at in the TabLayout

        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("Map");
        tabLayout.addTab(secondTab);

        TabLayout.Tab thirdTab = tabLayout.newTab();
        thirdTab.setText("Budget");
        tabLayout.addTab(thirdTab);

        TabLayout.Tab fourthTab = tabLayout.newTab();
        fourthTab.setText("Comments");
        tabLayout.addTab(fourthTab);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), itinerary_id, itinerary_user_id);
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

        // check if the intent send include option to view comment tab
        // if yes, open the comment tab
        if(viewComment) {
            fourthTab.select();
        }

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
