package com.afifzafri.backpacktrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView forgotpass = (TextView) findViewById(R.id.forgotpass);
        forgotpass.setMovementMethod(LinkMovementMethod.getInstance()); // create forgot pass link

        final FrameLayout loadingFrame = (FrameLayout)findViewById(R.id.loadingFrame);
        final Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText username = (EditText) findViewById(R.id.username);
                final EditText password = (EditText) findViewById(R.id.password);
                CheckBox remember_me = (CheckBox) findViewById(R.id.remember_me);

                if(!TextUtils.isEmpty(username.getText()) && !TextUtils.isEmpty(password.getText())) {
                    loginBtn.setEnabled(false); // disable button
                    loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                    JSONObject loginParams = new JSONObject(); // login parameters

                    try {
                        loginParams.put("login", username.getText().toString());
                        loginParams.put("password", password.getText().toString());
                        loginParams.put("remember_me", (remember_me.isChecked() ? 1 : 0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Request a string response from the provided URL.
                    JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/login", loginParams,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        int code = Integer.parseInt(response.getString("code"));

                                        if(code == 200)
                                        {
                                            // parse JSON response
                                            String token = response.getString("access_token");
                                            JSONObject result = response.getJSONObject("result");
                                            String id = result.getString("id");
                                            String name = result.getString("name");
                                            String username = result.getString("username");

                                            // store data into Android SharedPreferences
                                            SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString("access_token", token);
                                            editor.putString("user_id", id);
                                            editor.putString("name", name);
                                            editor.putString("username", username);
                                            editor.commit();

                                            Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();

                                            Intent intentPage = new Intent(LoginActivity.this, MainActivity.class);
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
                                                if(errors.has("password"))
                                                {
                                                    String err = errors.getJSONArray("password").getString(0);
                                                    password.setError(err);
                                                }
                                                if(errors.has("email"))
                                                {
                                                    String err = errors.getJSONArray("email").getString(0);
                                                    username.setError(err);
                                                }
                                            }

                                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();

                                            loginBtn.setEnabled(true);
                                            loadingFrame.setVisibility(View.GONE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_SHORT).show();

                            loginBtn.setEnabled(true);
                            loadingFrame.setVisibility(View.GONE);
                        }
                    });

                    // Add the request to the VolleySingleton.
                    VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(loginRequest);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter both Username/Email and Password!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // user click register button, show register page
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentPage);
                finish();
            }
        });
    }

}
