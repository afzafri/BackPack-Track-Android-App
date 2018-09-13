package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

        // Register button clicked
        registerBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setTitle("Register");
                alert.setMessage("Are you sure you want to register your account?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        
                        // get all input
                        EditText nameIn = (EditText) findViewById(R.id.name);
                        EditText usernameIn = (EditText) findViewById(R.id.username);
                        EditText phoneIn = (EditText) findViewById(R.id.phone);
                        EditText addressIn = (EditText) findViewById(R.id.address);
                        Spinner countrySpinner = (Spinner) findViewById(R.id.country);
                        EditText emailIn = (EditText) findViewById(R.id.email);
                        EditText passwordIn = (EditText) findViewById(R.id.password);
                        EditText password_confirmationIn = (EditText) findViewById(R.id.password_confirmation);

                        String name = nameIn.getText().toString();
                        String username = usernameIn.getText().toString();
                        String phone = phoneIn.getText().toString();
                        String address = addressIn.getText().toString();
                        StringWithTag country = (StringWithTag) countrySpinner.getSelectedItem();
                        String country_id = (String) country.key;
                        String email = emailIn.getText().toString();
                        String password = passwordIn.getText().toString();
                        String password_confirmation = password_confirmationIn.getText().toString();

                        if(name != null && username != null && phone != null && address != null
                                && country_id != null && email != null && password != null && password_confirmation != null)
                        {
                            Toast.makeText(getApplicationContext(), "Register Success!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please fill in all the input!", Toast.LENGTH_SHORT).show();
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
