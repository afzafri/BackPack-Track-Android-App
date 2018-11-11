package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements IPickResult {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get UI elements
        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editUsername = (EditText) findViewById(R.id.editUsername);
        final EditText editPhone = (EditText) findViewById(R.id.editPhone);
        final EditText editAddress = (EditText) findViewById(R.id.editAddress);
        final EditText editBio = (EditText) findViewById(R.id.editBio);
        final EditText editWebsite = (EditText) findViewById(R.id.editWebsite);
        final EditText editEmail = (EditText) findViewById(R.id.editEmail);
        final AutoCompleteTextView countryselect = (AutoCompleteTextView) findViewById(R.id.countries_list);
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        final Button updAccountBtn = (Button) findViewById(R.id.updAccountBtn);

        final EditText editOldPassword = (EditText) findViewById(R.id.editOldPasword);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final EditText editPasswordConfirm = (EditText) findViewById(R.id.editPasswordConfirm);
        final Button chgPasswordBtn = (Button) findViewById(R.id.chgPasswordBtn);

        final ImageButton chooseBtn = (ImageButton) findViewById(R.id.chooseBtn);
        final Button upAvatarBtn = (Button) findViewById(R.id.upAvatarBtn);

        // Countries Array
        final List<String> countrieslist = new ArrayList<String>();

        // show loading spinner
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
                                String id = country.getString("id");
                                String name = country.getString("name");

                                countrieslist.add(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Populate the spinner with Array values
                        ArrayAdapter<String> countriesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, countrieslist);
                        countryselect.setAdapter(countriesAdapter);

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

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // ----- Fetch user data and display the profile -----
        // Request a string response from the provided URL.
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/user", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String phone = response.getString("phone");
                            String address = response.getString("address");
                            String bio = response.getString("bio");
                            String website = response.getString("website");
                            String email = response.getString("email");
                            String avatar_url = response.getString("avatar_url");
                            JSONObject country = response.getJSONObject("country");
                            String country_name = country.getString("name");
                            String country_id = country.getString("id");

                            // set values to the elements
                            editName.setText(name);
                            editUsername.setText(username);
                            editPhone.setText(phone);
                            editAddress.setText(address);
                            countryselect.setText(country_name);
                            editBio.setText(bio);
                            editWebsite.setText(website);
                            editEmail.setText(email);

                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Profile data not loaded!  Please check your connection.", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(profileRequest);

        // ----- Clicked save button, update into server -----
        updAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileActivity.this);
                alert.setTitle("Update");
                alert.setMessage("Are you sure you want to update your account details?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        // get all input
                        String name = editName.getText().toString();
                        final String username = editUsername.getText().toString();
                        String phone = editPhone.getText().toString();
                        String address = editAddress.getText().toString();
                        final String country_name = countryselect.getText().toString();
                        String country_id = Integer.toString(countrieslist.indexOf(country_name));
                        String bio = editBio.getText().toString();
                        String website = editWebsite.getText().toString();
                        String email = editEmail.getText().toString();

                        if(name != null && username != null && phone != null && address != null && !country_id.equals("-1") && email != null)
                        {
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            JSONObject updateParams = new JSONObject(); // login parameters

                            try {
                                updateParams.put("name", name);
                                updateParams.put("username", username);
                                updateParams.put("phone", phone);
                                updateParams.put("address", address);
                                updateParams.put("country_id", country_id);
                                updateParams.put("bio", bio);
                                updateParams.put("website", website);
                                updateParams.put("email", email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/updateProfile", updateParams,
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
                                                            editName.setError(err);
                                                        }
                                                        if(errors.has("username"))
                                                        {
                                                            String err = errors.getJSONArray("username").getString(0);
                                                            editUsername.setError(err);
                                                        }
                                                        if(errors.has("phone"))
                                                        {
                                                            String err = errors.getJSONArray("phone").getString(0);
                                                            editPhone.setError(err);
                                                        }
                                                        if(errors.has("address"))
                                                        {
                                                            String err = errors.getJSONArray("address").getString(0);
                                                            editAddress.setError(err);
                                                        }
                                                        if(errors.has("country"))
                                                        {
                                                            String err = errors.getJSONArray("country").getString(0);
                                                            countryselect.setError(err);
                                                        }
                                                        if(errors.has("bio"))
                                                        {
                                                            String err = errors.getJSONArray("bio").getString(0);
                                                            editBio.setError(err);
                                                        }
                                                        if(errors.has("website"))
                                                        {
                                                            String err = errors.getJSONArray("website").getString(0);
                                                            editWebsite.setError(err);
                                                        }
                                                        if(errors.has("email"))
                                                        {
                                                            String err = errors.getJSONArray("email").getString(0);
                                                            editEmail.setError(err);
                                                        }
                                                    }

                                                    Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                }

                                                loadingFrame.setVisibility(View.GONE);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Update failed! Please check your connection.", Toast.LENGTH_SHORT).show();

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
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(updateRequest);
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

        // ----- Change Password -----
        chgPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileActivity.this);
                alert.setTitle("Change Password");
                alert.setMessage("Are you sure you want to change your password?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        // get all input
                        String oldpassword = editOldPassword.getText().toString();
                        String newpassword = editPassword.getText().toString();
                        String newpasswordconfirm = editPasswordConfirm.getText().toString();

                        if(oldpassword != null && newpassword != null && newpasswordconfirm != null)
                        {
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            JSONObject passwordParams = new JSONObject(); // login parameters

                            try {
                                passwordParams.put("old_password", oldpassword);
                                passwordParams.put("password", newpassword);
                                passwordParams.put("password_confirmation", newpasswordconfirm);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest passwordRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/updatePassword", passwordParams,
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

                                                    editOldPassword.setText("");
                                                    editPassword.setText("");
                                                    editPasswordConfirm.setText("");
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
                                                            editPassword.setError(err);
                                                        }
                                                        if(errors.has("old_password"))
                                                        {
                                                            String err = errors.getJSONArray("old_password").getString(0);
                                                            editOldPassword.setError(err);
                                                        }
                                                    }

                                                    Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                }

                                                loadingFrame.setVisibility(View.GONE);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Password change failed! Please check your connection.", Toast.LENGTH_SHORT).show();

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
                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(passwordRequest);
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

        // ----- Choose image button, open gallery -----
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(EditProfileActivity.this);
            }
        });

        // ----- Click upload button, upload image to server -----
        upAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(EditProfileActivity.this);
                alert.setTitle("Upload new profile picture");
                alert.setMessage("Are you sure you want to upload new picture? This will replace the current one.");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        // get all input
                        final ImageView avatarPreview = (ImageView) findViewById(R.id.avatarPreview);

                        if(new AppHelper().hasImage(avatarPreview))
                        {
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            // VolleyMultipartRequest library by Angga Ari Wijaya https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
                            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppHelper.baseurl + "/api/uploadAvatar", new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    String resultResponse = new String(response.data);
                                    try {
                                        JSONObject result = new JSONObject(resultResponse);
                                        int code = Integer.parseInt(result.getString("code"));

                                        if(code == 200)
                                        {
                                            // parse JSON response
                                            String message = result.getString("message");
                                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                        }
                                        else if(code == 400)
                                        {
                                            String errormsg = result.getString("message");

                                            // check if response contain errors messages
                                            if(result.has("error"))
                                            {
                                                JSONObject errors = result.getJSONObject("error");
                                                if(errors.has("avatar"))
                                                {
                                                    String err = errors.getJSONArray("avatar").getString(0);
                                                    Toast.makeText(getApplicationContext(), errormsg + err, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        loadingFrame.setVisibility(View.GONE);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Upload picture failed! Please check your connection." + error.toString(), Toast.LENGTH_SHORT).show();
                                    loadingFrame.setVisibility(View.GONE);
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("Authorization", "Bearer "+access_token);

                                    return params;
                                }

                                @Override
                                protected Map<String, DataPart> getByteData() {
                                    Map<String, DataPart> params = new HashMap<>();
                                    // file name could found file base or direct access from real path
                                    // for now just get bitmap data from ImageView
                                    params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawableAvatar(getBaseContext(), avatarPreview.getDrawable(), 500, 0), "image/jpeg"));

                                    return params;
                                }
                            };

                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Please choose a picture first!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Bitmap.
            ImageView avatarPreview = (ImageView) findViewById(R.id.avatarPreview);
            avatarPreview.setImageBitmap(r.getBitmap());

            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            Toast.makeText(this, r.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
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
