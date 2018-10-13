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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

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
public class BudgetFragment extends Fragment {

    // initialize adapter and data structure here
    private ListBudgetTypesAdapter mAdapter;
    private ListDailyBudgetAdapter mAdapterDaily;
    // List for all data array
    private List<TotalBudgetTypeModel> budgetList;
    private List<DailyBudgetModel> dailyList;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerViewDaily;
    private LinearLayoutManager mLayoutManagerDaily;


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
        // initialize map
        final PieChart pieChart = (PieChart) view.findViewById(R.id.pieChart);

        // ------------ Display the Pie chart --------------------
        // Setup recycler view for listing the budgets table
        // you must assign all objects to avoid nullPointerException
        budgetList = new ArrayList<>();
        mAdapter = new ListBudgetTypesAdapter(budgetList);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.listType);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setFocusable(false);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        JsonObjectRequest chartRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getTotalBudgetPerType/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // arraylist for piechart data
                        List<PieEntry> entries = new ArrayList<>();

                        try {

                            // parse JSON response
                            String currency = response.getString("currency");
                            String grandTotal = response.getString("grandTotal");
                            JSONArray budgets = response.getJSONArray("detail");

                            for(int i=0;i<budgets.length();i++) {
                                JSONObject budget = budgets.getJSONObject(i);

                                // insert budget data
                                float totalBudget = Float.parseFloat(budget.getString("totalBudget"));
                                String budget_type = budget.getString("budget_type");
                                entries.add(new PieEntry(totalBudget, budget_type));

                                budgetList.add(new TotalBudgetTypeModel(budget.getString("budget_type"), currency + " " + budget.getString("totalBudget")));
                            }

                            mAdapter.notifyDataSetChanged(); // alert adapter

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
                        pieChart.getDescription().setEnabled(false); // remove default desc
                        pieChart.setUsePercentValues(true); // output data in percentage
                        set.setColors(ColorTemplate.MATERIAL_COLORS); // set colors
                        pieChart.animateY(2000); // set animation
                        //pieChart.setDrawHoleEnabled(false); // no donut hole
                        pieChart.setCenterText("Percentage for each budget types");
                        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // set out values display
                        set.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // set out label display
                        // set label color
                        int colorBlack = Color.parseColor("#000000");
                        pieChart.setEntryLabelColor(colorBlack);

                        // set offset, avoid content clipping
                        pieChart.setExtraTopOffset(20f);
                        pieChart.setExtraBottomOffset(20f);
                        pieChart.setExtraLeftOffset(20f);
                        pieChart.setExtraRightOffset(20f);

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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(chartRequest);


        // ------------- Display daily budgets -----------------
        // Setup recycler view for listing the budgets table
        // you must assign all objects to avoid nullPointerException
        dailyList = new ArrayList<>();
        mAdapterDaily = new ListDailyBudgetAdapter(dailyList);

        mRecyclerViewDaily = (RecyclerView) view.findViewById(R.id.listDailyBudgets);
        // use a linear layout manager
        mLayoutManagerDaily = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerViewDaily.setLayoutManager(mLayoutManagerDaily);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewDaily.setHasFixedSize(true);

        // specify an adapter (see also next example)
        mRecyclerViewDaily.setAdapter(mAdapterDaily);
        mRecyclerViewDaily.setFocusable(false);

        final TextView textTotalBudget = (TextView) view.findViewById(R.id.textTotalBudget);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

        JsonObjectRequest dailyRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getTotalBudgetPerDay/"+itinerary_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            String currency = response.getString("currency");
                            String grandTotal = response.getString("grandTotal");
                            JSONArray budgets = response.getJSONArray("detail");

                            for(int i=0;i<budgets.length();i++) {
                                JSONObject budget = budgets.getJSONObject(i);

                                // insert budget data
                                String budgetDay = budget.getString("day");
                                String budgetDate = budget.getString("date");
                                String budgetBudget = budget.getString("totalBudget");

                                dailyList.add(new DailyBudgetModel(budgetDay, budgetDate, currency + " " + budgetBudget));
                            }

                            // set grand total
                            textTotalBudget.setText(currency + " " + grandTotal);

                            mAdapterDaily.notifyDataSetChanged(); // alert adapter

                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();

                            loadingFrame.setVisibility(View.GONE);
                        }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(dailyRequest);

        return view;
    }

}
