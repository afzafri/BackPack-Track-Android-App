package com.afifzafri.backpacktrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class CountryItinerariesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_itineraries);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        String country_id = extras.getString("country_id");
        String country_name = extras.getString("country_name");

        // set activity title
        setTitle("Itineraries for " + country_name);
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
