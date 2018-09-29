package com.afifzafri.backpacktrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateItineraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // Get all elements
        final EditText title = (EditText) findViewById(R.id.title);
        final AutoCompleteTextView countryselect = (AutoCompleteTextView) findViewById(R.id.countries_list);
        final Button createBtn = (Button) findViewById(R.id.createBtn);
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);

        // Countries Array
        final List<String> countrieslist = new ArrayList<String>();

        // show progress bar
        loadingFrame.setVisibility(View.VISIBLE);

        // Populate Countries spinner

        // Request a string response from the provided URL.
        JsonArrayRequest countriesListRequest = new JsonArrayRequest(Request.Method.GET, AppHelper.baseurl + "/api/listCountries", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        countrieslist.add("Select countries..."); // set default first element in the spinner

                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject country = response.getJSONObject(i);
                                String name = country.getString("name");

                                countrieslist.add(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Populate the AutoCompleteTextView with Array values
                        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, countrieslist);
                        countryselect.setAdapter(countriesAdapter);

                        Toast.makeText(getApplicationContext(), "Load Countries Success!", Toast.LENGTH_SHORT).show();
                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load Countries Failed!", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }
        });

        // Add the request to the VolleySingleton.
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(countriesListRequest);
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
