package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final Spinner countryspinner = (Spinner)findViewById(R.id.country);
        Button registerBut = (Button) findViewById(R.id.registerBut);
        Button loginBut = (Button) findViewById(R.id.loginBut);

        // Populate Countries spinner
        // Instantiate the RequestQueue.
        RequestQueue queueCountries = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        JsonArrayRequest countriesListRequest = new JsonArrayRequest(Request.Method.GET, AppConstants.baseurl + "/api/listCountries", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // Countries Array
                        ArrayList<StringWithTag> countrieslist = new ArrayList<StringWithTag>();
                        countrieslist.add(new StringWithTag(null, "Select countries...")); // set default first element in the spinner

                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject country = response.getJSONObject(i);
                                String id = country.getString("id");
                                String name = country.getString("name");

                                countrieslist.add(new StringWithTag(id, name));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Populate the spinner with Array values
                        ArrayAdapter<StringWithTag> countriesAdapter = new ArrayAdapter<StringWithTag>(getApplicationContext(),   android.R.layout.simple_spinner_item, countrieslist);
                        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        countryspinner.setAdapter(countriesAdapter);

                        Toast.makeText(getApplicationContext(), "Load Countries Success!", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load Countries Failed!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queueCountries.add(countriesListRequest);

        // if click login, show login page
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentPage);
                finish();
            }
        });
    }
}
