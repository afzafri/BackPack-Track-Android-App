package com.afifzafri.backpacktrack;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class CountryItinerariesActivity extends AppCompatActivity {

    ViewPager simpleViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_itineraries);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation
        getSupportActionBar().setElevation(0); // remove shadow

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String country_id = extras.getString("country_id");
        String country_name = extras.getString("country_name");

        // set activity title
        setTitle("Itineraries for " + country_name);

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

        ItinerariesPagerAdapter adapter = new ItinerariesPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), "country", country_id);
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
