package com.afifzafri.backpacktrack;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
public class MyItineraryFragment extends Fragment {

    // for swipe to refresh widget
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // define last page, need for API to load next page. Will be increase by each request
    private int lastPage = 1;

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    // initialize adapter and data structure here
    private ListMyItinerariesAdapter mAdapter;
    // Countries list Array
    private List<ItinerariesModel> itinerariesList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public MyItineraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_itinerary, container, false);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // you must assign all objects to avoid nullPointerException
        itinerariesList = new ArrayList<>();
        mAdapter = new ListMyItinerariesAdapter(itinerariesList);

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
        firstLoadData(view, access_token);

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
                            loadMore(view, access_token);
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
                        Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(currentFragment).attach(currentFragment).commit();
                        mSwipeRefreshLayout.setRefreshing(false);

                        lastPage = 1; // reset back current page to first page
                    }
                }
        );

        // handle FAB button
        FloatingActionButton createFab = (FloatingActionButton) view.findViewById(R.id.createFab);
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(getActivity(), CreateItineraryActivity.class);
                startActivity(intentPage);
            }
        });


        return view;
    }

    private void firstLoadData(View view, final String access_token) {
        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest countriesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/listItinerariesByAuthUser", null,
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
                                // check if activity have been attach to the fragment
                                if(isAdded()) {
                                    Toast.makeText(getActivity().getApplicationContext(), "no data available", Toast.LENGTH_SHORT).show();
                                }
                                itShouldLoadMore = false;
                                return; // return will end the program at this point
                            }

                            for(int i=0;i<itineraries.length();i++)
                            {
                                JSONObject itinerary = itineraries.getJSONObject(i);
                                String id = itinerary.getString("id");
                                String title = itinerary.getString("title");
                                String duration = itinerary.getString("duration");
                                JSONObject user = itinerary.getJSONObject("user");
                                String user_name = user.getString("name");
                                JSONObject country = itinerary.getJSONObject("country");
                                String country_name = country.getString("name");
                                String country_currency = country.getString("currency");
                                String totalbudget = country_currency + " " + itinerary.getString("totalbudget");

                                // insert data into array
                                itinerariesList.add(new ItinerariesModel(id, user_name, title, country_name, duration, totalbudget));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // check if activity have been attach to the fragment
                        if(isAdded()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Load itineraries Success!", Toast.LENGTH_SHORT).show();
                        }
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // check if activity have been attach to the fragment
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

    private void loadMore(View view, final String access_token) {
        // get UI elements
        final ProgressBar loadMoreSpin = (ProgressBar) view.findViewById(R.id.loadMoreSpin);

        // show loading spinner
        loadMoreSpin.setVisibility(View.VISIBLE);

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        // Request a string response from the provided URL.
        JsonObjectRequest countriesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/listItinerariesByAuthUser?page="+lastPage, null,
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
                                // check if activity have been attach to the fragment
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
                                String user_name = user.getString("name");
                                JSONObject country = itinerary.getJSONObject("country");
                                String country_name = country.getString("name");
                                String country_currency = country.getString("currency");
                                String totalbudget = country_currency + " " + itinerary.getString("totalbudget");

                                // insert data into array
                                itinerariesList.add(new ItinerariesModel(id, user_name, title, country_name, duration, totalbudget));

                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // check if activity have been attach to the fragment
                        if(isAdded()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Load more itineraries success!", Toast.LENGTH_SHORT).show();
                        }
                        loadMoreSpin.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // check if activity have been attach to the fragment
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
