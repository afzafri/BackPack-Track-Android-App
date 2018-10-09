package com.afifzafri.backpacktrack;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class MapFragment extends Fragment {

    // initialize variables for google map
    MapView mMapView;
    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        // get itinerary id
        final String itinerary_id = getArguments().getString("itinerary_id");

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // Initialize Google Map and display Google Map
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                //googleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                // get list of coordinates and details
                // get UI elements
                final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

                // show loading spinner
                loadingFrame.setVisibility(View.VISIBLE);

                // Request a string response from the provided URL.
                JsonArrayRequest coordinatesListRequest = new JsonArrayRequest(Request.Method.GET, AppHelper.baseurl + "/api/getLatLng/"+itinerary_id, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                for(int i=0;i<response.length();i++)
                                {
                                    try {
                                        JSONObject data = response.getJSONObject(i);
                                        String place_name = data.getString("place_name");
                                        String activity = data.getString("activity");
                                        Double lat = Double.parseDouble(data.getString("lat"));
                                        Double lng = Double.parseDouble(data.getString("lng"));

                                        googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(place_name).snippet(activity));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                loadingFrame.setVisibility(View.GONE);

                                if(response.length() > 0) {
                                    try {
                                        JSONObject initialData = response.getJSONObject(0);
                                        // initial lat lng for zoom
                                        LatLng firstCoor = new LatLng(Double.parseDouble(initialData.getString("lat")), Double.parseDouble(initialData.getString("lng")));
                                        // For zooming automatically to the location of the marker
                                        CameraPosition cameraPosition = new CameraPosition.Builder().target(firstCoor).zoom(12).build();
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(isAdded()) {
                            Toast.makeText(getActivity().getApplicationContext(), "Load coordinates Failed! Please check your connection.", Toast.LENGTH_SHORT).show();
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
                VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(coordinatesListRequest);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
