package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

                        // parse JSON response
                        //String token = response.getString("access_token");

                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "Load Countries Success!", Toast.LENGTH_SHORT).show();

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
