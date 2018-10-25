package com.afifzafri.backpacktrack;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView textUsername;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // get UI elements
        final TextView textName = (TextView) view.findViewById(R.id.textName);
        textUsername = (TextView) view.findViewById(R.id.textUsername);
        final TextView textBio = (TextView) view.findViewById(R.id.textBio);
        final TextView textCountry = (TextView) view.findViewById(R.id.textCountry);
        final TextView textEmail = (TextView) view.findViewById(R.id.textEmail);
        final TextView textWebsite = (TextView) view.findViewById(R.id.textWebsite);
        final TextView textTotal = (TextView) view.findViewById(R.id.textTotal);
        final ImageView avatar_pic = (ImageView) view.findViewById(R.id.avatar_pic);
        final TextView textCountComments = (TextView) view.findViewById(R.id.textCountComments);
        final TextView textCountLikes = (TextView) view.findViewById(R.id.textCountLikes);
        final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);
        final FrameLayout loadNotiFrame = (FrameLayout) view.findViewById(R.id.loadNotiFrame);
        final CardView cardNotification = (CardView) view.findViewById(R.id.cardNotification);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");


        // ----- Fetch user data and display the profile -----
        // show loading spinner
        loadingFrame.setVisibility(View.VISIBLE);
        // Request a string response from the provided URL.
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/user", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            int id = response.getInt("id");
                            String name = response.getString("name");
                            String username = response.getString("username");
                            String phone = response.getString("phone");
                            String address = response.getString("address");
                            String bio = response.getString("bio");
                            String website = response.getString("website");
                            String email = response.getString("email");
                            String totalitineraries = response.getString("totalitineraries");
                            String avatar_url = response.getString("avatar_url");
                            JSONObject country = response.getJSONObject("country");
                            String country_name = country.getString("name");

                            // set values to the elements
                            textName.setText(name);
                            textUsername.setText("@"+username);
                            textBio.setText(bio);
                            textWebsite.setText(website.replaceFirst("^(http[s]?://www\\.|http[s]?://|www\\.)",""));
                            textEmail.setText(email);
                            textCountry.setText(country_name);
                            textTotal.setText(totalitineraries);

                            // check if bio and website not available (because optional), hide the widgets
                            if(bio == null || bio.isEmpty() || bio == "null") {
                                textBio.setVisibility(View.GONE);
                            }
                            if(website == null || website.isEmpty() || website == "null") {
                                textWebsite.setVisibility(View.GONE);
                            }

                            // set avatar image using Picasso library
                            if(avatar_url != null && !avatar_url.isEmpty() && avatar_url != "null") {
                                // check if activity have been attach to the fragment
                                if(isAdded()) {
                                    Picasso.get()
                                            .load(avatar_url)
                                            .transform(new BorderedCircleTransformation(getResources().getColor(R.color.colorPrimary),5))
                                            .into(avatar_pic);
                                }
                                avatar_pic.setTag(avatar_url); // store url into tag, used for retrieve later
                            } else {
                                Picasso.get()
                                        .load(R.drawable.avatar)
                                        .transform(new BorderedCircleTransformation(getResources().getColor(R.color.colorPrimary),5))
                                        .into(avatar_pic);
                            }

                            // check if activity have been attach to the fragment
                            if(isAdded()) {
                                Toast.makeText(getActivity().getApplicationContext(), "Profile data loaded!", Toast.LENGTH_SHORT).show();
                            }
                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // check if activity have been attach to the fragment
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Profile data not loaded! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(profileRequest);


        // ----- Fetch user comments notification -----
        loadNotiFrame.setVisibility(View.VISIBLE);
        // Request a string response from the provided URL.
        JsonObjectRequest notificationRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/getNotifications", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            // parse JSON response
                            // comments
                            JSONObject comments = response.getJSONObject("comments");
                            String total_comments = comments.getString("total_comments");
                            // likes
                            JSONObject likes = response.getJSONObject("likes");
                            String total_likes = likes.getString("total_likes");

                            // set values to the elements
                            textCountComments.setText(total_comments);
                            textCountLikes.setText(total_likes);

                            loadNotiFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadNotiFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // check if activity have been attach to the fragment
                if(isAdded()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Notification data not loaded! Please check your connection.", Toast.LENGTH_SHORT).show();
                }
                loadNotiFrame.setVisibility(View.GONE);
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
        VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(notificationRequest);

        // ----- Click notification, open activity to list all comments notification -----
        cardNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if activity have been attach to the fragment
                if(isAdded()) {
                    Intent intentPage = new Intent(getActivity(), NotificationsActivity.class);
                    startActivity(intentPage);
                }
            }
        });

        // ----- Click avatar, show full screen image -----
        avatar_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open fullscreen image activity
                // check if activity have been attach to the fragment
                if(isAdded()) {
                    Intent intentPage = new Intent(getActivity(), ImageFullscreenActivity.class);
                    intentPage.putExtra("image_url", avatar_pic.getTag().toString());
                    intentPage.putExtra("caption", textName.getText().toString());
                    startActivity(intentPage);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * For handling the menu items on click event for the Profile fragment
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // ----- Refresh Page -----
            case R.id.action_refresh:

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();

                return true;

            // share profile
            case R.id.action_share:

                String usrn = textUsername.getText().toString();
                if(!usrn.equals("") && usrn != null) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String url = AppHelper.baseurl + "/user/" + usrn.replace("@", "");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, url);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

                return true;

            // ----- Edit Profile -----
            case R.id.action_edit_profile:

                // redirect to edit profile page
                Intent intentPage = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intentPage);

                return true;


            // ----- Log Out of App -----
            case R.id.action_logout:

                View view = getView();

                if (view != null) {
                    final FrameLayout loadingFrame = (FrameLayout) view.findViewById(R.id.loadingFrame);

                    // read from SharedPreferences
                    final SharedPreferences sharedpreferences = getActivity().getSharedPreferences("logindata", Context.MODE_PRIVATE);
                    final String access_token = sharedpreferences.getString("access_token", "");

                    // Create dialog box, ask confirmation before proceed
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle("Log Out");
                    alert.setMessage("Are you sure you want to log out of the application?");
                    // set positive button, yes etc
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loadingFrame.setVisibility(View.VISIBLE);

                            // Request a string response from the provided URL.
                            JsonObjectRequest logoutRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/logout", null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {

                                                // parse JSON response
                                                String message = response.getString("message");
                                                // check if activity have been attach to the fragment
                                                if(isAdded()) {
                                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                                }

                                                // clear SharedPreferences
                                                sharedpreferences.edit().clear().commit();

                                                // redirect to log in page
                                                // check if activity have been attach to the fragment
                                                if(isAdded()) {
                                                    Intent intentPage = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intentPage);
                                                    getActivity().finish();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                loadingFrame.setVisibility(View.GONE);
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // check if activity have been attach to the fragment
                                    if(isAdded()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Log out failed! Please check your connection.", Toast.LENGTH_SHORT).show();
                                    }
                                    loadingFrame.setVisibility(View.GONE);
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Authorization", "Bearer " + access_token);

                                    return params;
                                }
                            };

                            // Add the request to the VolleySingleton.
                            VolleySingleton.getInstance(getActivity().getBaseContext()).addToRequestQueue(logoutRequest);

                            dialog.dismiss();
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

                return true;
        }
        return false;
    }

}
