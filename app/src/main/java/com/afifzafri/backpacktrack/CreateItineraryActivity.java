package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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

public class CreateItineraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // Get all elements
        final EditText itinerary_title = (EditText) findViewById(R.id.title);
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

        // Create new itinerary when button create is clicked
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(CreateItineraryActivity.this);
                alert.setTitle("Create new Itinerary");
                alert.setMessage("Are you sure you want to create this itinerary?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        String title = itinerary_title.getText().toString();
                        String country_name = countryselect.getText().toString();
                        String country_id = Integer.toString(countrieslist.indexOf(country_name));

                        if(title != null && country_name != null && !country_id.equals("-1"))
                        {
                            createBtn.setEnabled(false); // disable button
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            JSONObject createParams = new JSONObject(); // login parameters

                            try {
                                createParams.put("title", title);
                                createParams.put("country_id", country_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest createRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/newItinerary", createParams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {

                                                int code = Integer.parseInt(response.getString("code"));

                                                if(code == 200)
                                                {
                                                    // parse JSON response
                                                    String message = response.getString("message");
                                                    JSONObject result = response.getJSONObject("result");
                                                    String resitinerary_id = result.getString("id");
                                                    String resitinerary_title = result.getString("title");
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                                    // empty all input
                                                    itinerary_title.setText("");
                                                    countryselect.setText("");

                                                    // redirect to create activity page
                                                    Intent intentPage = new Intent(getApplicationContext(), MyActivitiesActivity.class);
                                                    intentPage.putExtra("itinerary_id", resitinerary_id);
                                                    intentPage.putExtra("itinerary_title", resitinerary_title);
                                                    startActivity(intentPage);

                                                    finish();
                                                }
                                                else if(code == 400)
                                                {
                                                    String errormsg = response.getString("message");

                                                    // check if response contain errors messages
                                                    if(response.has("error"))
                                                    {
                                                        JSONObject errors = response.getJSONObject("error");
                                                        if(errors.has("title"))
                                                        {
                                                            String err = errors.getJSONArray("title").getString(0);
                                                            itinerary_title.setError(err);
                                                        }
                                                        if(errors.has("country"))
                                                        {
                                                            String err = errors.getJSONArray("country").getString(0);
                                                            countryselect.setError(err);
                                                        }
                                                    }

                                                    Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                    createBtn.setEnabled(true);
                                                    loadingFrame.setVisibility(View.GONE);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Create new itinerary failed! Please check your connection.", Toast.LENGTH_SHORT).show();

                                    createBtn.setEnabled(true);
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

                            // set new timeout, fix double request bug if network connection is slow
                            createRequest.setRetryPolicy(new DefaultRetryPolicy(
                                    0,
                                    -1,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            // Add the request to the VolleySingleton.
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(createRequest);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please fill in all the input!", Toast.LENGTH_SHORT).show();

                            // if the country id is -1, which means not found in the countries list, then show error
                            String err = "Please only choose the available country in the list";
                            countryselect.setError(err);
                        }

                    }
                });
                // set negative button, no etc
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show(); // show alert message
            }
        });

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
