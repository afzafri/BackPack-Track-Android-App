package com.afifzafri.backpacktrack;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TopCountryItinerariesFragment extends Fragment {

    // for swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // define last page, need for API to load next page. Will be increase by each request
    private int lastPage = 1;

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    // initialize adapter and data structure here
    private ListItinerariesAdapter mAdapter;
    // Countries list Array
    private List<ItinerariesModel> itinerariesList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private String apiEndpoint;

    public TopCountryItinerariesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_top_country_itineraries, container, false);

        // get country id
        final String country_id = getArguments().getString("country_id");
        // get sort type
        final int sort = getArguments().getInt("sort");

        // set API endpoint based on sort code received
        apiEndpoint = null;
        if(sort == 1) {
            apiEndpoint = "listItinerariesByCountry";
        } else if(sort == 2) {
            apiEndpoint = "listTopItinerariesByCountry";
        } else if(sort == 3) {
            apiEndpoint = "listTrendingItinerariesByCountry";
        } else {
            apiEndpoint = "listItinerariesByCountry";
        }

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // you must assign all objects to avoid nullPointerException
        itinerariesList = new ArrayList<>();
        mAdapter = new ListItinerariesAdapter(itinerariesList, access_token);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.itineraries_list);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);

        // create a function for the first load
        firstLoadData(view, country_id, access_token, apiEndpoint);

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
                            loadMore(view, country_id, access_token, apiEndpoint);
                        }
                    }

                }
            }
        });

        // refresh fragment when perform swipe to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(TopCountryItinerariesFragment.this).attach(TopCountryItinerariesFragment.this).commit();

                        mSwipeRefreshLayout.setRefreshing(false);

                        lastPage = 1; // reset back current page to first page
                    }
                }
        );

        return view;
    }

    private void firstLoadData(View view, String country_id, final String access_token, String apiEndpoint) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest countriesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/"+apiEndpoint+"/"+country_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // remember here we are in the main thread, that means,
                        //volley has finished processing request, and we have our response.
                        // What else are you waiting for? update itShouldLoadMore = true;
                        itShouldLoadMore = true;

                        try {
                            JSONArray itineraries = response.getJSONArray("data");

                            if (itineraries.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                if(isAdded()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "no data available", Toast.LENGTH_SHORT).show();
                                }
                                itShouldLoadMore = false;
                                loadingFrame.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<itineraries.length();i++)
                            {
                                JSONObject itinerary = itineraries.getJSONObject(i);
                                String id = itinerary.getString("id");
                                String title = itinerary.getString("title");
                                String duration = itinerary.getString("duration");
                                JSONObject user = itinerary.getJSONObject("user");
                                String user_id = user.getString("id");
                                String user_name = user.getString("name");
                                String date = itinerary.getString("created_at");
                                JSONObject country = itinerary.getJSONObject("country");
                                String country_name = country.getString("name");
                                String country_currency = country.getString("currency");
                                String totalbudget = country_currency + " " + itinerary.getString("totalbudget");
                                String totallikes = itinerary.getString("totallikes");
                                String totalcomments = itinerary.getString("totalcomments");
                                Boolean isLiked = itinerary.getBoolean("isLiked");

                                // insert data into array
                                itinerariesList.add(new ItinerariesModel(id, user_id, user_name, title, country_name, duration, date, totalbudget, totallikes, totalcomments, isLiked));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(isAdded()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Load itineraries Success!", Toast.LENGTH_SHORT).show();
                        }
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Load itineraries Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(countriesListRequest);

        lastPage++; // increment the page number
    }

    private void loadMore(View view, String country_id, final String access_token, String apiEndpoint) {
        // get UI elements
        final ProgressBar loadMoreSpin = (ProgressBar) view.findViewById(R.id.loadMoreSpin);

        // show loading spinner
        loadMoreSpin.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest countriesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/"+apiEndpoint+"/"+country_id+"?page="+lastPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // since volley has completed and it has our response, now let's update
                        // itShouldLoadMore
                        itShouldLoadMore = true;

                        try {
                            JSONArray itineraries = response.getJSONArray("data");

                            if (itineraries.length() <= 0) {
                                // we need to check this, to make sure, our dataStructure JSonArray contains
                                // something
                                if(isAdded()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "No more itineraries available", Toast.LENGTH_SHORT).show();
                                }
                                itShouldLoadMore = false;
                                loadMoreSpin.setVisibility(View.GONE);
                                return; // return will end the program at this point
                            }


                            for(int i=0;i<itineraries.length();i++)
                            {
                                JSONObject itinerary = itineraries.getJSONObject(i);
                                String id = itinerary.getString("id");
                                String title = itinerary.getString("title");
                                String duration = itinerary.getString("duration");
                                JSONObject user = itinerary.getJSONObject("user");
                                String user_id = user.getString("id");
                                String user_name = user.getString("name");
                                String date = itinerary.getString("created_at");
                                JSONObject country = itinerary.getJSONObject("country");
                                String country_name = country.getString("name");
                                String country_currency = country.getString("currency");
                                String totalbudget = country_currency + " " + itinerary.getString("totalbudget");
                                String totallikes = itinerary.getString("totallikes");
                                String totalcomments = itinerary.getString("totalcomments");
                                Boolean isLiked = itinerary.getBoolean("isLiked");

                                // insert data into array
                                itinerariesList.add(new ItinerariesModel(id, user_id, user_name, title, country_name, duration, date, totalbudget, totallikes, totalcomments, isLiked));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(isAdded()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Load more itineraries success!", Toast.LENGTH_SHORT).show();
                        }
                        loadMoreSpin.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Load more itineraries failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(countriesListRequest);

        lastPage++; // increment the page number
    }

}
