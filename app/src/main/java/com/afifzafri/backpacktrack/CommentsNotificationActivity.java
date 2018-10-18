package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class CommentsNotificationActivity extends AppCompatActivity {

    // for swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // initialize adapter and data structure here
    private ListCommentsNotificationAdapter mAdapter;
    // Countries list Array
    private List<CommentsNotificationModel> commentsList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // you must assign all objects to avoid nullPointerException
        commentsList = new ArrayList<>();
        mAdapter = new ListCommentsNotificationAdapter(commentsList);

        mRecyclerView = (RecyclerView) findViewById(R.id.comments_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        // create a function for the first load
        firstLoadData(access_token);

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

    private void firstLoadData(final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        JsonObjectRequest notificationsRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getCommentsNotification", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // parse JSON response
                            String total_comments = response.getString("total_comments");
                            JSONArray comments = response.getJSONArray("comments");

                            if (comments.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                Toast.makeText(getApplicationContext(), "no notification available", Toast.LENGTH_SHORT).show();
                                loadingFrame.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<comments.length();i++)
                            {
                                JSONObject comment = comments.getJSONObject(i);
                                String id = comment.getString("id");
                                String date_time = comment.getString("created_at");
                                JSONObject user = comment.getJSONObject("user");
                                String user_id = user.getString("id");
                                String user_name = user.getString("name");
                                String user_username = user.getString("username");
                                String user_avatar = user.getString("avatar_url");
                                JSONObject itinerary = comment.getJSONObject("itinerary");
                                String itinerary_id = itinerary.getString("id");
                                String itinerary_title = itinerary.getString("title");
                                String itinerary_user_id = itinerary.getString("user_id");

                                // insert data into array
                                commentsList.add(new CommentsNotificationModel(id, date_time, user_id, user_name, user_username, user_avatar, itinerary_id, itinerary_title, itinerary_user_id));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(getApplicationContext(), "Load notifications Success!", Toast.LENGTH_SHORT).show();
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load notifications Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(notificationsRequest);
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
