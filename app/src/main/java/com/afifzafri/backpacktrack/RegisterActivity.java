package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final AutoCompleteTextView countryselect = (AutoCompleteTextView) findViewById(R.id.countries_list);
        final Button registerBtn = (Button) findViewById(R.id.registerBtn);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
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

        // Register button clicked
        registerBtn.setOnClickListener(new View.OnClickListener() {
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
                        final EditText nameIn = (EditText) findViewById(R.id.name);
                        final EditText usernameIn = (EditText) findViewById(R.id.username);
                        final EditText phoneIn = (EditText) findViewById(R.id.phone);
                        final EditText addressIn = (EditText) findViewById(R.id.address);
                        final EditText emailIn = (EditText) findViewById(R.id.email);
                        final EditText passwordIn = (EditText) findViewById(R.id.password);
                        final EditText password_confirmationIn = (EditText) findViewById(R.id.password_confirmation);

                        String name = nameIn.getText().toString();
                        final String username = usernameIn.getText().toString();
                        String phone = phoneIn.getText().toString();
                        String address = addressIn.getText().toString();
                        final String country_name = countryselect.getText().toString();
                        String country_id = Integer.toString(countrieslist.indexOf(country_name));
                        String email = emailIn.getText().toString();
                        String password = passwordIn.getText().toString();
                        String password_confirmation = password_confirmationIn.getText().toString();
                        String role = "backpacker";

                        if(name != null && username != null && phone != null && address != null && !country_id.equals("-1") && email != null && password != null && password_confirmation != null)
                        {
                            registerBtn.setEnabled(false); // disable button
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            JSONObject registerParams = new JSONObject(); // login parameters

                            try {
                                registerParams.put("name", name);
                                registerParams.put("username", username);
                                registerParams.put("phone", phone);
                                registerParams.put("address", address);
                                registerParams.put("country", country_id);
                                registerParams.put("email", email);
                                registerParams.put("password", password);
                                registerParams.put("password_confirmation", password_confirmation);
                                registerParams.put("role", role);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/register", registerParams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {

                                                int code = Integer.parseInt(response.getString("code"));

                                                if(code == 200)
                                                {
                                                    // parse JSON response
                                                    String message = response.getString("message");
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                                    // empty all input
                                                    nameIn.setText("");
                                                    usernameIn.setText("");
                                                    phoneIn.setText("");
                                                    addressIn.setText("");
                                                    countryselect.setText("");
                                                    emailIn.setText("");
                                                    passwordIn.setText("");
                                                    password_confirmationIn.setText("");
                                                }
                                                else if(code == 400)
                                                {
                                                    String errormsg = response.getString("message");

                                                    // check if response contain errors messages
                                                    if(response.has("error"))
                                                    {
                                                        JSONObject errors = response.getJSONObject("error");
                                                        if(errors.has("name"))
                                                        {
                                                            String err = errors.getJSONArray("name").getString(0);
                                                            nameIn.setError(err);
                                                        }
                                                        if(errors.has("username"))
                                                        {
                                                            String err = errors.getJSONArray("username").getString(0);
                                                            usernameIn.setError(err);
                                                        }
                                                        if(errors.has("phone"))
                                                        {
                                                            String err = errors.getJSONArray("phone").getString(0);
                                                            phoneIn.setError(err);
                                                        }
                                                        if(errors.has("address"))
                                                        {
                                                            String err = errors.getJSONArray("address").getString(0);
                                                            addressIn.setError(err);
                                                        }
                                                        if(errors.has("country"))
                                                        {
                                                            String err = errors.getJSONArray("country").getString(0);
                                                            countryselect.setError(err);
                                                        }
                                                        if(errors.has("email"))
                                                        {
                                                            String err = errors.getJSONArray("email").getString(0);
                                                            emailIn.setError(err);
                                                        }
                                                        if(errors.has("password"))
                                                        {
                                                            String err = errors.getJSONArray("password").getString(0);
                                                            passwordIn.setError(err);
                                                        }
                                                    }

                                                    Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                }

                                                registerBtn.setEnabled(true);
                                                loadingFrame.setVisibility(View.GONE);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Registration failed! Please check your connection.", Toast.LENGTH_SHORT).show();

                                    registerBtn.setEnabled(true);
                                    loadingFrame.setVisibility(View.GONE);
                                }
                            });

                            // Add the request to the VolleySingleton.
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(registerRequest);
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

        // if click login, show login page
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentPage);
                finish();
            }
        });
    }
}
