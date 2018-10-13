package com.afifzafri.backpacktrack;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.mikephil.charting.components.Legend.LegendPosition.RIGHT_OF_CHART;


/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetFragment extends Fragment {


    public BudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // get itinerary id
        final String itinerary_id = getArguments().getString("itinerary_id");

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // get UI elements
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);
        final TextView chartDescription = (TextView) view.findViewById(R.id.chartDescription);
        // initialize map
        final PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        JsonObjectRequest activityRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getTotalBudgetPerType/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String currency = null;
                        // arraylist for piechart data
                        List<PieEntry> entries = new ArrayList<>();

                        try {

                            // parse JSON response
                            currency = response.getString("currency");
                            String grandTotal = response.getString("grandTotal");
                            JSONArray budgets = response.getJSONArray("detail");

                            for(int i=0;i<budgets.length();i++) {
                                JSONObject budget = budgets.getJSONObject(i);

                                // insert budget data
                                float totalBudget = Float.parseFloat(budget.getString("totalBudget"));
                                String budget_type = budget.getString("budget_type");
                                entries.add(new PieEntry(totalBudget, budget_type));
                            }

                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();

                            loadingFrame.setVisibility(View.GONE);
                        }

                        // set data
                        PieDataSet set = new PieDataSet(entries, "Budget Types");
                        PieData data = new PieData(set);
                        data.setValueTextSize(20f);
                        data.setValueFormatter(new PercentFormatter()); // show percentage symbol
                        pieChart.setData(data);

                        // styling
                        chartDescription.setText("Total Budget Expense"); // set description
                        pieChart.getDescription().setEnabled(false); // remove default desc
                        pieChart.setUsePercentValues(true); // output data in percentage
                        set.setColors(ColorTemplate.MATERIAL_COLORS); // set colors
                        pieChart.animateY(2000); // set animation
                        pieChart.setDrawHoleEnabled(false); // no donut hole

                        // disable legends
                        pieChart.getLegend().setEnabled(false);

                        // refresh chart
                        pieChart.invalidate();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), "Budgets data not loaded!  Please check your connection.", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(activityRequest);

        return view;
    }

}
