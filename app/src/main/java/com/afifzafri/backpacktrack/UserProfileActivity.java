package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textUsername;

    // initialize adapter and data structure here
    private ListPopularItinerariesAdapter mAdapter;
    // Countries list Array
    private List<PopularItinerariesModel> popularList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // declare variables
        String user_id = null;

        // If activity start from other intent get data pass through intent
        Bundle extras = getIntent().getExtras();
        user_id = extras.getString("user_id");

        // If activity launched from web, get data from deeplink
        Uri deepLink = this.getIntent().getData();
        if (deepLink != null && deepLink.isHierarchical()) {
            user_id = deepLink.getQueryParameter("user_id");
        }

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // get UI elements
        final TextView textName = (TextView) findViewById(R.id.textName);
        textUsername = (TextView) findViewById(R.id.textUsername);
        final TextView textBio = (TextView) findViewById(R.id.textBio);
        final TextView textCountry = (TextView) findViewById(R.id.textCountry);
        final TextView textEmail = (TextView) findViewById(R.id.textEmail);
        final TextView textWebsite = (TextView) findViewById(R.id.textWebsite);
        final TextView textTotal = (TextView) findViewById(R.id.textTotal);
        final ImageView avatar_pic = (ImageView) findViewById(R.id.avatar_pic);
        final Button itineraryBtn = (Button) findViewById(R.id.itineraryBtn);
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // ----- Fetch article data and display -----
        // Request a string response from the provided URL.
        JsonObjectRequest userRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getUserData/" + user_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            int id = response.getInt("id");
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String phone = response.getString("phone");
                            String address = response.getString("address");
                            String bio = response.getString("bio");
                            String website = response.getString("website");
                            String email = response.getString("email");
                            String totalitineraries = response.getString("totalitineraries");
                            String avatar_url = response.getString("avatar_url");
                            String country_name = response.getString("country_name");

                            // set values to the elements
                            textName.setText(name);
                            textUsername.setText("@"+username);
                            textBio.setText(bio);
                            textWebsite.setText(website.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)",""));
                            textEmail.setText(email);
                            textCountry.setText(country_name);
                            textTotal.setText(totalitineraries);

                            // check if bio and website not available (because optional), hide the widgets
                            if(bio == null || bio.isEmpty() || bio == "null") {
                                textBio.setVisibility(View.GONE);
                            }
                            if(website == null || website.isEmpty() || website == "null") {
                                textWebsite.setVisibility(View.GONE);
                            }

                            // set avatar image using Picasso library
                            if(avatar_url != null && !avatar_url.isEmpty() && avatar_url != "null") {
                                // check if activity have been attach to the fragment
                                Picasso.get()
                                        .load(avatar_url)
                                        .transform(new BorderedCircleTransformation(getResources().getColor(R.color.colorPrimary),5))
                                        .into(avatar_pic);
                                avatar_pic.setTag(avatar_url); // store url into tag, used for retrieve later
                            } else {
                                Picasso.get()
                                        .load(R.drawable.avatar)
                                        .transform(new BorderedCircleTransformation(getResources().getColor(R.color.colorPrimary),5))
                                        .into(avatar_pic);
                            }

                            Toast.makeText(getApplicationContext(), "Profile data loaded!", Toast.LENGTH_SHORT).show();
                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();

                            loadingFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Profile not loaded! Please check your connection.", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(userRequest);

        // you must assign all objects to avoid nullPointerException
        popularList = new ArrayList<>();
        mAdapter = new ListPopularItinerariesAdapter(popularList);

        mRecyclerView = (RecyclerView) findViewById(R.id.listPopularItineraries);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        // create a function for the load user's popular itineraries list
        loadPopularList(user_id, access_token);
    }

    private void loadPopularList(String user_id, final String access_token) {
        // get UI elements
        final FrameLayout loadPopularFrame = (FrameLayout) findViewById(R.id.loadPopularFrame);

        // show loading spinner
        loadPopularFrame.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        JsonArrayRequest popularListRequest = new JsonArrayRequest(Request.Method.GET, AppHelper.baseurl + "/api/listUserPopularItineraries/"+user_id, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {

                            for(int i=0;i<response.length();i++)
                            {
                                JSONObject itinerary = response.getJSONObject(i);
                                String itinerary_id = itinerary.getString("id");
                                String itinerary_title = itinerary.getString("itinerary_title");
                                String itinerary_country = itinerary.getString("itinerary_country");
                                String itinerary_poster_id = itinerary.getString("itinerary_poster_id");
                                String itinerary_poster_name = itinerary.getString("itinerary_poster_name");
                                String totallikes = itinerary.getString("total");

                                // insert data into array
                                popularList.add(new PopularItinerariesModel(itinerary_id, itinerary_title, itinerary_country, itinerary_poster_id, itinerary_poster_name, totallikes));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadPopularFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load popular itineraries Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                loadPopularFrame.setVisibility(View.GONE);
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
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(popularListRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
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

            // share profile
            case R.id.action_share:

                String usrn = textUsername.getText().toString();
                if(!usrn.equals("") && usrn != null) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String url = AppHelper.baseurl + "/user/" + usrn.replace("@", "");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
