package com.afifzafri.backpacktrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditActivityActivity extends AppCompatActivity implements IPickResult {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String activity_id = extras.getString("activity_id");
        final String activity_title = extras.getString("activity_title");
        setTitle("Edit Activity: " + activity_title);

        // read from SharedPreferences
        final SharedPreferences sharedpreferences = getSharedPreferences("logindata", Context.MODE_PRIVATE);
        final String access_token = sharedpreferences.getString("access_token", "");

        // get elements
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editTime = (EditText) findViewById(R.id.editTime);
        final EditText editActivity = (EditText) findViewById(R.id.editActivity);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        final EditText editPlaceName = (EditText) findViewById(R.id.editPlaceName);
        final EditText editBudget = (EditText) findViewById(R.id.editBudget);
        final TextView labelChoose = (TextView) findViewById(R.id.labelChoose);
        final ImageButton chooseBtn = (ImageButton) findViewById(R.id.chooseBtn);
        final ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        final Button createBtn = (Button) findViewById(R.id.createBtn);

        // Fetch current activity data
        // show progress bar
        loadingFrame.setVisibility(View.VISIBLE);
        JsonObjectRequest activityRequest = new JsonObjectRequest(Request.Method.GET, AppHelper.baseurl + "/api/viewActivity/"+activity_id, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            // parse JSON response
                            String id = response.getString("id");
                            String date = response.getString("date");
                            String time = response.getString("time");
                            String activityTitle = response.getString("activity");
                            String description = response.getString("description");
                            String place_name = response.getString("place_name");
                            String lat = response.getString("lat");
                            String lng = response.getString("lng");
                            String budget = response.getString("budget");
                            String pic_url = response.getString("pic_url");
                            String curitinerary_id = response.getString("itinerary_id");

                            // set values to the elements
                            editDate.setText(date);
                            editTime.setText(time);
                            editActivity.setText(activityTitle);
                            editDescription.setText(description);
                            editPlaceName.setText(place_name);
                            editPlaceName.setTag(lat+","+lng);
                            editBudget.setText(budget);

                            Toast.makeText(getApplicationContext(), "Activity data loaded!", Toast.LENGTH_SHORT).show();
                            loadingFrame.setVisibility(View.GONE); // hide loading spinner

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadingFrame.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Activity data not loaded!  Please check your connection.", Toast.LENGTH_SHORT).show();
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
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(activityRequest);

        // Date picker
        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy/MM/dd"; //set date format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                editDate.setText(sdf.format(myCalendar.getTime()));
            }

        };

        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(EditActivityActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Time picker
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditActivityActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTime.setText(String.format("%02d", selectedHour) + ":" + String.format("%02d", selectedMinute));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        // Select place, open Google Place Autocomplete API
        editPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .build(EditActivityActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        // ----- Choose image button, open gallery -----
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(EditActivityActivity.this);
            }
        });
    }

    // for select google place
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final EditText editPlaceName = (EditText) findViewById(R.id.editPlaceName);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                // get data
                String placeName = place.getName().toString();
                String placeLat = Double.toString(place.getLatLng().latitude);
                String placeLng = Double.toString(place.getLatLng().longitude);
                editPlaceName.setText(placeName);
                editPlaceName.setTag(placeLat+","+placeLng);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                editPlaceName.setText("");
                editPlaceName.setTag("");
                editPlaceName.setError("Select place error");
                //Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                editPlaceName.setText("");
                editPlaceName.setTag("");
            }
        }
        super.onActivityResult(requestCode, resultCode, data); // need to call super, if not onPickResult will not be called
    }

    // for pick image
    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Bitmap.
            final ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
            imgPreview.setImageBitmap(r.getBitmap());

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
