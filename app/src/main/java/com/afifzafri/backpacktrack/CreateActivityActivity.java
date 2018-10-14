package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateActivityActivity extends AppCompatActivity implements IPickResult {

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
        final Spinner spinnerBudget = (Spinner) findViewById(R.id.spinnerBudget);
        final TextView labelChoose = (TextView) findViewById(R.id.labelChoose);
        final ImageButton chooseBtn = (ImageButton) findViewById(R.id.chooseBtn);
        final ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        final Button createBtn = (Button) findViewById(R.id.createBtn);

        // Populate budget type spinner
        loadingFrame.setVisibility(View.VISIBLE);
        JsonArrayRequest budgetListRequest = new JsonArrayRequest(Request.Method.GET, AppHelper.baseurl + "/api/listBudgetTypes", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        ArrayList<BudgetTypeModel> budgetTypeList = new ArrayList<BudgetTypeModel>();
                        budgetTypeList.add(new BudgetTypeModel(null,"Select type...")); // set default first element in the spinner

                        for(int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject budget = response.getJSONObject(i);
                                String id = budget.getString("id");
                                String type = budget.getString("type");

                                budgetTypeList.add(new BudgetTypeModel(id, type));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Populate the spinner with Array values
                        ArrayAdapter<BudgetTypeModel> budgetAdapter = new ArrayAdapter<BudgetTypeModel>(getApplicationContext(), android.R.layout.simple_spinner_item, budgetTypeList);
                        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                        spinnerBudget.setAdapter(budgetAdapter);

                        loadingFrame.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Load Budget type Failed! Check your connection", Toast.LENGTH_SHORT).show();
                loadingFrame.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+access_token);

                return params;
            }
        };

        // Add the request to the VolleySingleton.
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(budgetListRequest);

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

        // ----- Choose image button, open gallery -----
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.build(new PickSetup()).show(CreateActivityActivity.this);
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

                        final String actDate = editDate.getText().toString();
                        final String actTime = editTime.getText().toString();
                        final String actTitle = editActivity.getText().toString();
                        final String actDescription = editDescription.getText().toString();
                        final String actPlaceName = editPlaceName.getText().toString();
                        final String actLatLng = (String) editPlaceName.getTag();
                        final String actBudget = (editBudget.getText().toString() == null || editBudget.getText().toString().equals("")) ? "0.00" : editBudget.getText().toString();
                        BudgetTypeModel spinSel = (BudgetTypeModel) spinnerBudget.getSelectedItem();
                        final String actBudgetType = (spinSel.getId() == null) ? "7" : spinSel.getId(); // if not selected, set budget type to Other
                        final Drawable actPic = imgPreview.getDrawable();

                        Toast.makeText(getApplicationContext(), actBudget, Toast.LENGTH_SHORT).show();


                        if(actDate != null && actTime != null && actTitle != null && actDescription != null && actPlaceName != null && actLatLng != null)
                        {
                            createBtn.setEnabled(false); // disable button
                            loadingFrame.setVisibility(View.VISIBLE);// show loading progress bar

                            // parse latitude and longitude
                            final String actLat = actLatLng.split(",")[0];
                            final String actLng = actLatLng.split(",")[1];

                            // request to API
                            // VolleyMultipartRequest library by Angga Ari Wijaya https://gist.github.com/anggadarkprince/a7c536da091f4b26bb4abf2f92926594
                            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, AppHelper.baseurl + "/api/newActivity", new Response.Listener<NetworkResponse>() {
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

                                            setResult(CreateActivityActivity.RESULT_OK, new Intent());
                                            finish();
                                        }
                                        else if(code == 400)
                                        {
                                            String errormsg = result.getString("message");

                                            // check if response contain errors messages
                                            if(result.has("error"))
                                            {
                                                JSONObject errors = result.getJSONObject("error");
                                                if(errors.has("date"))
                                                {
                                                    String err = errors.getJSONArray("date").getString(0);
                                                    editDate.setError(err);
                                                }
                                                if(errors.has("time"))
                                                {
                                                    String err = errors.getJSONArray("time").getString(0);
                                                    editTime.setError(err);
                                                }
                                                if(errors.has("activity"))
                                                {
                                                    String err = errors.getJSONArray("activity").getString(0);
                                                    editActivity.setError(err);
                                                }
                                                if(errors.has("description"))
                                                {
                                                    String err = errors.getJSONArray("description").getString(0);
                                                    editDescription.setError(err);
                                                }
                                                if(errors.has("place_name"))
                                                {
                                                    String err = errors.getJSONArray("place_name").getString(0);
                                                    editPlaceName.setError(err);
                                                }
                                                if(errors.has("lat"))
                                                {
                                                    String err = errors.getJSONArray("lat").getString(0);
                                                    editPlaceName.setError(err);
                                                }
                                                if(errors.has("lng"))
                                                {
                                                    String err = errors.getJSONArray("lng").getString(0);
                                                    editPlaceName.setError(err);
                                                }
                                                if(errors.has("budget"))
                                                {
                                                    String err = errors.getJSONArray("budget").getString(0);
                                                    editBudget.setError(err);
                                                }
                                                if(errors.has("budgettype_id"))
                                                {
                                                    String err = errors.getJSONArray("budget_id").getString(0);
                                                    TextView errorText = (TextView)spinnerBudget.getSelectedView();
                                                    errorText.setError("");
                                                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                                                    errorText.setText(err);//changes the selected item text to this
                                                }
                                                if(errors.has("image"))
                                                {
                                                    String err = errors.getJSONArray("image").getString(0);
                                                    labelChoose.setError(err);
                                                }
                                            }

                                            Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            createBtn.setEnabled(true);
                                            loadingFrame.setVisibility(View.GONE);
                                        }

                                        loadingFrame.setVisibility(View.GONE);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), "Create new activity failed! Please check your connection." + error.toString(), Toast.LENGTH_SHORT).show();
                                    createBtn.setEnabled(true);
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
                                protected Map<String, String> getParams() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("date", actDate);
                                    params.put("time", actTime);
                                    params.put("activity", actTitle);
                                    params.put("description", actDescription);
                                    params.put("place_name", actPlaceName);
                                    params.put("lat", actLat);
                                    params.put("lng", actLng);
                                    params.put("itinerary_id", itinerary_id);
                                    params.put("budget", actBudget);
                                    params.put("budgettype_id", actBudgetType);
                                    return params;
                                }

                                @Override
                                protected Map<String, DataPart> getByteData() {
                                    Map<String, DataPart> params = new HashMap<>();
                                    // file name could found file base or direct access from real path
                                    // for now just get bitmap data from ImageView
                                    if(new AppHelper().hasImage(imgPreview)) {
                                        params.put("image", new DataPart("activity_image.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), actPic), "image/jpeg"));
                                    }

                                    return params;
                                }
                            };

                            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

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
