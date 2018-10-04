package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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

public class ViewItineraryActivity extends AppCompatActivity {

    // initialize adapter and data structure here
    private ListDatesAdapter mAdapter;
    // List for all data array
    private List<ItineraryDatesModel> allDataList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String itinerary_id = extras.getString("itinerary_id");
        String itinerary_title = extras.getString("itinerary_title");
        setTitle(itinerary_title);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // you must assign all objects to avoid nullPointerException
        allDataList = new ArrayList<>();

        mAdapter = new ListDatesAdapter(allDataList);

        mRecyclerView = (RecyclerView) findViewById(R.id.dates_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        // create a function for load all data
        firstLoadData(itinerary_id, access_token);

    }

    private void firstLoadData(String itinerary_id, final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        JsonObjectRequest activitiesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewActivitiesByDay/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject activities = response.getJSONObject("activities");

                            if (activities.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                Toast.makeText(getApplicationContext(), "no data available", Toast.LENGTH_SHORT).show();
                                loadingFrame.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<activities.length();i++)
                            {
                                // get the date
                                String date = activities.names().getString(i);

                                // get the activities
                                List<ActivitiesModel> activitiesList = new ArrayList<>();
                                JSONArray actArr = activities.getJSONArray(date);
                                for(int j=0;j<actArr.length();j++)
                                {
                                    JSONObject activity = actArr.getJSONObject(j);
                                    String id = activity.getString("id");
                                    String act_date = activity.getString("date");
                                    String time = activity.getString("time");
                                    String activity_title = activity.getString("activity");
                                    String description = activity.getString("description");
                                    String place_name = activity.getString("place_name");
                                    String lat = activity.getString("lat");
                                    String lng = activity.getString("lng");
                                    String budget = activity.getString("budget");
                                    String pic_url = activity.getString("pic_url");
                                    String act_itinerary_id = activity.getString("itinerary_id");

                                    // insert data into array
                                    activitiesList.add(new ActivitiesModel(id, act_date, time, activity_title, description, place_name, lat, lng, budget, pic_url, act_itinerary_id));
                                }

                                // insert data into array
                                allDataList.add(new ItineraryDatesModel(date,activitiesList));

                                mAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(getApplicationContext(), "Load activities Success!", Toast.LENGTH_SHORT).show();
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load activities Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+access_token);

                return params;
            }
        };

        // Add the request to the VolleySingleton.
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(activitiesListRequest);
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
