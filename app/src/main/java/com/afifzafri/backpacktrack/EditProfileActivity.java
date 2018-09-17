package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

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
        final EditText editEmail = (EditText) findViewById(R.id.editEmail);
        final Spinner countryspinner = (Spinner) findViewById(R.id.countryspinner);
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        final List<String> countrieslist2 = new ArrayList<String>(); // need 2nd array, for getting position of country
        final Button updAccountBtn = (Button) findViewById(R.id.updAccountBtn);

        final EditText editOldPassword = (EditText) findViewById(R.id.editOldPasword);
        final EditText editPassword = (EditText) findViewById(R.id.editPassword);
        final EditText editPasswordConfirm = (EditText) findViewById(R.id.editPasswordConfirm);
        final Button chgPasswordBtn = (Button) findViewById(R.id.chgPasswordBtn);

        final ImageButton chooseBtn = (ImageButton) findViewById(R.id.chooseBtn);
        final Button upAvatarBtn = (Button) findViewById(R.id.upAvatarBtn);

        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);

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
                        countrieslist2.add("Select countries..."); // also insert into 2nd list

                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject country = response.getJSONObject(i);
                                String id = country.getString("id");
                                String name = country.getString("name");

                                countrieslist.add(new StringWithTag(id, name));
                                countrieslist2.add(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Populate the spinner with Array values
                        ArrayAdapter<StringWithTag> countriesAdapter = new ArrayAdapter<StringWithTag>(getApplicationContext(),   android.R.layout.simple_spinner_item, countrieslist);
                        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        countryspinner.setAdapter(countriesAdapter);

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

        // Add the request to the RequestQueue.
        queueCountries.add(countriesListRequest);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // ----- Fetch user data and display the profile -----
        // Instantiate the RequestQueue.
        RequestQueue profileQueue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, AppConstants.baseurl + "/api/user", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String phone = response.getString("phone");
                            String address = response.getString("address");
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
                            editEmail.setText(email);

                            // set spinner selection to match current country
                            if (country_name != null && !country_name.isEmpty() && country_name != "null") {
                                int pos = countrieslist2.indexOf(country_name);
                                countryspinner.setSelection(pos);
                            }

                            Toast.makeText(getApplicationContext(), "Profile data loaded!", Toast.LENGTH_SHORT).show();
                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Profile data not loaded!", Toast.LENGTH_SHORT).show();
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

        // Add the request to the RequestQueue.
        profileQueue.add(profileRequest);

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
                        final StringWithTag country = (StringWithTag) countryspinner.getSelectedItem();
                        final String country_id = (String) country.key;
                        String email = editEmail.getText().toString();

                        if(name != null && username != null && phone != null && address != null && country_id != null && email != null)
                        {
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            // Instantiate the RequestQueue.
                            RequestQueue updateQueue = Volley.newRequestQueue(getApplicationContext());

                            JSONObject updateParams = new JSONObject(); // login parameters

                            try {
                                updateParams.put("name", name);
                                updateParams.put("username", username);
                                updateParams.put("phone", phone);
                                updateParams.put("address", address);
                                updateParams.put("country_id", country_id);
                                updateParams.put("email", email);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.POST, AppConstants.baseurl + "/api/updateProfile", updateParams,
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
                                                            ((TextView)countryspinner.getSelectedView()).setError(err);
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
                                    Toast.makeText(getApplicationContext(), "Update failed!", Toast.LENGTH_SHORT).show();

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

                            // Add the request to the RequestQueue.
                            updateQueue.add(updateRequest);
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

                            // Instantiate the RequestQueue.
                            RequestQueue passwordQueue = Volley.newRequestQueue(getApplicationContext());

                            JSONObject passwordParams = new JSONObject(); // login parameters

                            try {
                                passwordParams.put("old_password", oldpassword);
                                passwordParams.put("password", newpassword);
                                passwordParams.put("password_confirmation", newpasswordconfirm);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest passwordRequest = new JsonObjectRequest(Request.Method.POST, AppConstants.baseurl + "/api/updatePassword", passwordParams,
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
                                    Toast.makeText(getApplicationContext(), "Password change failed!", Toast.LENGTH_SHORT).show();

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

                            // Add the request to the RequestQueue.
                            passwordQueue.add(passwordRequest);
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
                // open media chooser
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageIntent.setType("image/*");
                startActivityForResult(pickImageIntent, 1);
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

                        if(hasImage(avatarPreview))
                        {
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            // VolleyMultipartRequest library by Angga Ari Wijaya https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
                            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppConstants.baseurl + "/api/uploadAvatar", new Response.Listener<NetworkResponse>() {
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
                                    Toast.makeText(getApplicationContext(), "Upload picture failed! Please check you internet connection" + error.toString(), Toast.LENGTH_SHORT).show();
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
                                    params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), avatarPreview.getDrawable()), "image/jpeg"));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);

                //displaying selected image to imageview
                ImageView avatarPreview = (ImageView) findViewById(R.id.avatarPreview);
                avatarPreview.setImageBitmap(bitmap);

                avatarPreview.getLayoutParams().height = 400;
                avatarPreview.requestLayout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // method for checking if and ImageView have a drawable attached to it
    // credit: https://stackoverflow.com/a/32066539/5784900
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
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
