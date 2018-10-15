package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivitiesActivity extends AppCompatActivity {

    // for swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // define last page, need for API to load next page. Will be increase by each request
    private int lastPage = 1;

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    // initialize adapter and data structure here
    private ListActivitiesAdapter mAdapter;
    // Countries list Array
    private List<ActivitiesModel> activitiesList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_activities);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String itinerary_id = extras.getString("itinerary_id");
        final String itinerary_title = extras.getString("itinerary_title");
        setTitle("Manage activities for " + itinerary_title);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // you must assign all objects to avoid nullPointerException
        activitiesList = new ArrayList<>();
        mAdapter = new ListActivitiesAdapter(activitiesList, access_token);

        mRecyclerView = (RecyclerView) findViewById(R.id.activities_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        // create a function for the first load
        firstLoadData(itinerary_id, access_token);

        // here add a recyclerView listener, to listen to scrolling,
        // we don't care when user scrolls upwards, will only be careful when user scrolls downwards
        // this listener is freely provided for by android, no external library
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            // for this tutorial, this is the ONLY method that we need, ignore the rest
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {
                            loadMore(itinerary_id, access_token);
                        }
                    }

                }
            }
        });

        // refresh fragment when perform swipe to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        finish();
                        startActivity(getIntent());

                        mSwipeRefreshLayout.setRefreshing(false);

                        lastPage = 1; // reset back current page to first page
                    }
                }
        );

        // handle fab button
        FloatingActionButton createFab = (FloatingActionButton) findViewById(R.id.createFab);
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(getApplicationContext(), CreateActivityActivity.class);
                intentPage.putExtra("itinerary_id", itinerary_id);
                intentPage.putExtra("itinerary_title", itinerary_title);
                startActivityForResult(intentPage, 1);
            }
        });
    }

    private void firstLoadData(String itinerary_id, final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest activitiesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewActivitiesPaginated/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // remember here we are in the main thread, that means,
                        //volley has finished processing request, and we have our response.
                        // What else are you waiting for? update itShouldLoadMore = true;
                        itShouldLoadMore = true;

                        try {
                            // get country info and currency
                            JSONObject country = response.getJSONObject("country");
                            String currency = country.getString("currency");

                            JSONArray activities = response.getJSONArray("data");

                            if (activities.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                Toast.makeText(getApplicationContext(), "no data available", Toast.LENGTH_SHORT).show();
                                itShouldLoadMore = false;
                                loadingFrame.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<activities.length();i++)
                            {
                                JSONObject activity = activities.getJSONObject(i);
                                String id = activity.getString("id");
                                String date = activity.getString("date");
                                String time = activity.getString("time");
                                String activity_title = activity.getString("activity");
                                String description = activity.getString("description");
                                String place_name = activity.getString("place_name");
                                String lat = activity.getString("lat");
                                String lng = activity.getString("lng");
                                String budget = currency + " " + activity.getString("budget");
                                JSONObject budgettype = activity.getJSONObject("budgettype");
                                String budgettype_type = budgettype.getString("type");
                                String pic_url = activity.getString("pic_url");
                                String itinerary_id = activity.getString("itinerary_id");

                                // insert data into array
                                activitiesList.add(new ActivitiesModel(id, date, time, activity_title, description, place_name, lat, lng, budget, budgettype_type, pic_url, itinerary_id));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "Load activities Success!", Toast.LENGTH_SHORT).show();
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load activities Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
                itShouldLoadMore = true; // even if volley failed, set true so we can retry again
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

        lastPage++; // increment the page number
    }

    private void loadMore(String itinerary_id, final String access_token) {
        // get UI elements
        final ProgressBar loadMoreSpin = (ProgressBar) findViewById(R.id.loadMoreSpin);

        // show loading spinner
        loadMoreSpin.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest activitiesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewActivitiesPaginated/"+itinerary_id+"?page="+lastPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // since volley has completed and it has our response, now let's update
                        // itShouldLoadMore
                        itShouldLoadMore = true;

                        try {
                            JSONArray activities = response.getJSONArray("data");

                            if (activities.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                Toast.makeText(getApplicationContext(), "No more activitites available", Toast.LENGTH_SHORT).show();
                                itShouldLoadMore = false;
                                loadMoreSpin.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<activities.length();i++)
                            {
                                JSONObject activity = activities.getJSONObject(i);
                                String id = activity.getString("id");
                                String date = activity.getString("date");
                                String time = activity.getString("time");
                                String activity_title = activity.getString("activity");
                                String description = activity.getString("description");
                                String place_name = activity.getString("place_name");
                                String lat = activity.getString("lat");
                                String lng = activity.getString("lng");
                                String budget = activity.getString("budget");
                                JSONObject budgettype = activity.getJSONObject("budgettype");
                                String budgettype_type = budgettype.getString("type");
                                String pic_url = activity.getString("pic_url");
                                String itinerary_id = activity.getString("itinerary_id");

                                // insert data into array
                                activitiesList.add(new ActivitiesModel(id, date, time, activity_title, description, place_name, lat, lng, budget, budgettype_type, pic_url, itinerary_id));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "Load more activitites success!", Toast.LENGTH_SHORT).show();
                        loadMoreSpin.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load more activities failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                loadMoreSpin.setVisibility(View.GONE);

                itShouldLoadMore = true; // even if volley failed, set true so we can retry again
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

        lastPage++; // increment the page number
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
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
