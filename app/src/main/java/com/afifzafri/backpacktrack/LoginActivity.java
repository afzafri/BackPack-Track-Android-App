package com.afifzafri.backpacktrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressBar loading = (ProgressBar)findViewById(R.id.loading);
        final Button loginBut = (Button) findViewById(R.id.loginBut);

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                CheckBox remember_me = (CheckBox) findViewById(R.id.remember_me);

                if(!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())) {
                    loginBut.setEnabled(false); // disable button
                    loading.setVisibility(View.VISIBLE);// show loading progress bar

                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    JSONObject loginParams = new JSONObject(); // login parameters

                    try {
                        loginParams.put("login", username.getText().toString());
                        loginParams.put("password", password.getText().toString());
                        loginParams.put("remember_me", (remember_me.isChecked() ? 1 : 0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Request a string response from the provided URL.
                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, AppConstants.baseurl + "/api/login", loginParams,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                                    loginBut.setEnabled(true);
                                    loading.setVisibility(View.GONE);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();

                            loginBut.setEnabled(true);
                            loading.setVisibility(View.GONE);
                        }
                    });

                    // Add the request to the RequestQueue.
                    queue.add(loginRequest);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter both Username/Email and Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
