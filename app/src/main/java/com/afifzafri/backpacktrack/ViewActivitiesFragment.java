package com.afifzafri.backpacktrack;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewActivitiesFragment extends Fragment {

    // initialize adapter and data structure here
    private ListDatesAdapter mAdapter;
    // List for all data array
    private List<ItineraryDatesModel> allDataList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private TextView itinerary_user;
    private TextView itinerary_country;
    private ImageButton likeBtn;
    private TextView itinerary_likes;

    public ViewActivitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_activities, container, false);

        // get itinerary id
        String itinerary_id = getArguments().getString("itinerary_id");

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // elements
        itinerary_user = (TextView) view.findViewById(R.id.itinerary_user);
        itinerary_country = (TextView) view.findViewById(R.id.itinerary_country);
        likeBtn = (ImageButton) view.findViewById(R.id.likeBtn);
        itinerary_likes = (TextView) view.findViewById(R.id.itinerary_likes);

        // fetch and set the itinerary data info
        setItineraryData(view, itinerary_id, access_token);

        // you must assign all objects to avoid nullPointerException
        allDataList = new ArrayList<>();

        mAdapter = new ListDatesAdapter(getContext(), allDataList);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.dates_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setFocusable(false);

        // create a function for load all data
        firstLoadData(view, itinerary_id, access_token);

        return view;
    }

    private void setItineraryData(final View view, String itinerary_id, final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        JsonObjectRequest itineraryRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewItineraryDetails/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject user = response.getJSONObject("user");
                            String user_id = user.getString("id");
                            String user_name = user.getString("name");
                            JSONObject country = response.getJSONObject("country");
                            String country_id = country.getString("id");
                            String country_name = country.getString("name");
                            String totallikes = response.getString("totallikes");
                            Boolean isLiked = response.getBoolean("isLiked");

                            itinerary_user.setText(user_name);
                            itinerary_user.setTag(user_id);
                            itinerary_country.setText(country_name);
                            itinerary_country.setTag(country_id);
                            itinerary_likes.setText("(" + totallikes + ")");

                            if(isLiked) {
                                likeBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
                                itinerary_likes.setTextColor(Color.RED);
                                itinerary_likes.setTag(true);
                            } else {
                                likeBtn.setImageResource(R.drawable.ic_favorite_border_grey_24dp);
                                itinerary_likes.setTextColor(Color.GRAY);
                                itinerary_likes.setTag(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Load itinerary data Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(itineraryRequest);
    }

    private void firstLoadData(final View view, String itinerary_id, final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // Request a string response from the provided URL.
        JsonObjectRequest activitiesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewActivitiesByDay/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // get country info and currency
                            JSONObject country = response.getJSONObject("country");
                            String currency = country.getString("currency");

                            // get activities
                            JSONObject activities = response.getJSONObject("activities");

                            if (activities.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                if(isAdded()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "no data available", Toast.LENGTH_SHORT).show();
                                }
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
                                    String budget = currency + " " + activity.getString("budget");
                                    JSONObject budgettype = activity.getJSONObject("budgettype");
                                    String budgettype_type = budgettype.getString("type");
                                    String pic_url = activity.getString("pic_url");
                                    String act_itinerary_id = activity.getString("itinerary_id");

                                    // insert data into array
                                    activitiesList.add(new ActivitiesModel(id, act_date, time, activity_title, description, place_name, lat, lng, budget, budgettype_type, pic_url, act_itinerary_id));
                                }

                                // insert data into array
                                allDataList.add(new ItineraryDatesModel(date,activitiesList));

                                mAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Load activities Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(activitiesListRequest);
    }

}
