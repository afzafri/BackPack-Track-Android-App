package com.afifzafri.backpacktrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MyActivitiesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String itinerary_id = extras.getString("itinerary_id");
        String itinerary_title = extras.getString("itinerary_title");
        setTitle("Add activities for " + itinerary_title);
    }
}
