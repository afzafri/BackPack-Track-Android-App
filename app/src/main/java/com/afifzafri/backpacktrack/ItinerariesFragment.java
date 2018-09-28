package com.afifzafri.backpacktrack;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
public class ItinerariesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public ItinerariesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_itineraries, container, false);

        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.countries_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Countries names Array
        final List<String> countrieslist = new ArrayList<String>();
        // Countries code Array
        final List<String> countriescode = new ArrayList<String>();
        // Countries id Array
        final List<String> countriesid = new ArrayList<String>();

        // Request a string response from the provided URL.
        JsonObjectRequest countriesListRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/listVisitedCountries", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray countries = response.getJSONArray("data");

                            for(int i=0;i<countries.length();i++)
                            {
                                JSONObject country = countries.getJSONObject(i);
                                String name = country.getString("name");
                                String code = country.getString("code");
                                String id = country.getString("id");

                                countrieslist.add(name);
                                countriescode.add(code);
                                countriesid.add(id);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // specify an adapter (see also next example)
                        mAdapter = new ListCountriesAdapter(countrieslist, countriescode, countriesid);
                        mRecyclerView.setAdapter(mAdapter);

                        Toast.makeText(getActivity().getApplicationContext(), "Load Countries Success!", Toast.LENGTH_SHORT).show();
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Load Countries Failed!"+error, Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(countriesListRequest);

        return view;
    }

}
