package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateActivityActivity extends AppCompatActivity {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back navigation

        // get data pass through intent
        Bundle extras = getIntent().getExtras();
        final String itinerary_id = extras.getString("itinerary_id");
        final String itinerary_title = extras.getString("itinerary_title");
        setTitle("Create activity for " + itinerary_title);

        // get elements
        final FrameLayout loadingFrame = (FrameLayout) findViewById(R.id.loadingFrame);
        final EditText editDate = (EditText) findViewById(R.id.editDate);
        final EditText editTime = (EditText) findViewById(R.id.editTime);
        final EditText editActivity = (EditText) findViewById(R.id.editActivity);
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        final EditText editPlaceName = (EditText) findViewById(R.id.editPlaceName);
        final EditText editBudget = (EditText) findViewById(R.id.editBudget);
        final ImageButton chooseBtn = (ImageButton) findViewById(R.id.chooseBtn);
        final ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        final Button createBtn = (Button) findViewById(R.id.createBtn);

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
                new DatePickerDialog(CreateActivityActivity.this, date, myCalendar
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
                mTimePicker = new TimePickerDialog(CreateActivityActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                                    .build(CreateActivityActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        // Create button clicked, insert data send to API
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(CreateActivityActivity.this);
                alert.setTitle("Add activity?");
                alert.setMessage("Are you sure you want to create this activity?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        String actDate = editDate.getText().toString();
                        String actTime = editTime.getText().toString();
                        String actTitle = editActivity.getText().toString();
                        String actDescription = editDescription.getText().toString();
                        String actPlaceName = editPlaceName.getText().toString();
                        String actLatLng = (String) editPlaceName.getTag();
                        Drawable actPic = imgPreview.getDrawable();

                        if(actDate != null && actTime != null && actTitle != null && actDescription != null && actPlaceName != null && actLatLng != null)
                        {
                            createBtn.setEnabled(false); // disable button
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            // parse latitude and longitude
                            String lat = actLatLng.split(",")[0];
                            String lng = actLatLng.split(",")[1];

                            // request to API

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
    }

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
