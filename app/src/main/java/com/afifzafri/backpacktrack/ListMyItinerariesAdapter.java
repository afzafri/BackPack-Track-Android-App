package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMyItinerariesAdapter extends RecyclerView.Adapter<ListMyItinerariesAdapter.MyViewHolder> {

    private List<ItinerariesModel> itinerariesList;
    private String access_token;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itinerary_title;
        public TextView itinerary_country;
        public TextView itinerary_duration;
        public TextView itinerary_totalbudget;
        public TextView itinerary_date;
        public CardView mCardView;
        public ImageButton activityBtn;
        public ImageButton editBtn;
        public ImageButton deleteBtn;
        public FrameLayout deleteFrame;
        public ItinerariesModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            itinerary_title = (TextView) v.findViewById(R.id.itinerary_title);
            this.itinerary_title = itinerary_title;

            itinerary_country = (TextView) v.findViewById(R.id.itinerary_country);
            this.itinerary_country = itinerary_country;

            itinerary_duration = (TextView) v.findViewById(R.id.itinerary_duration);
            this.itinerary_duration = itinerary_duration;

            itinerary_totalbudget = (TextView) v.findViewById(R.id.itinerary_totalbudget);
            this.itinerary_totalbudget = itinerary_totalbudget;

            itinerary_date = (TextView) v.findViewById(R.id.itinerary_date);
            this.itinerary_date = itinerary_date;

            mCardView = (CardView) v.findViewById(R.id.itinerary_card);
            this.mCardView = mCardView;

            activityBtn = (ImageButton) v.findViewById(R.id.activityBtn);
            this.activityBtn = activityBtn;

            editBtn = (ImageButton) v.findViewById(R.id.editBtn);
            this.editBtn = editBtn;

            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
            this.deleteBtn = deleteBtn;

            deleteFrame = (FrameLayout) v.findViewById(R.id.deleteFrame);
            this.deleteFrame = deleteFrame;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListMyItinerariesAdapter(List<ItinerariesModel> itinerariesList, String access_token) {
        this.itinerariesList = itinerariesList;
        this.access_token = access_token;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListMyItinerariesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_itineraries, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set itinerary title
        holder.itinerary_title.setText(itinerariesList.get(position).getTitle());

        // Set itinerary country
        holder.itinerary_country.setText(itinerariesList.get(position).getCountry());

        // Set itinerary duration
        holder.itinerary_duration.setText(itinerariesList.get(position).getDuration());

        // Set itinerary total budget
        holder.itinerary_totalbudget.setText(itinerariesList.get(position).getTotalBudget());

        // Set itinerary date
        String crdate = itinerariesList.get(position).getDate();
        holder.itinerary_date.setText(new AppHelper().convertDate(crdate.split(" ")[0]));

        // get current position item data
        holder.currentItem = itinerariesList.get(position);

        // add activity button clicked, open add activity
        holder.activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // redirect to create activity page
                Intent intentPage = new Intent(v.getContext(), MyActivitiesActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getTitle());
                v.getContext().startActivity(intentPage);
            }
        });

        // when edit button clicked, open edit activity
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // redirect to create activity page
                Intent intentPage = new Intent(v.getContext(), EditItineraryActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getTitle());
                v.getContext().startActivity(intentPage);
            }
        });

        // delete button clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {

                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Delete this Itinerary");
                alert.setMessage("Are you sure you want to delete this itinerary?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        // show loading
                        holder.deleteFrame.setVisibility(View.VISIBLE);

                        JSONObject deleteParams = new JSONObject(); // login parameters

                        try {
                            deleteParams.put("itinerary_id", itinerariesList.get(position).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Request a string response from the provided URL.
                        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/deleteItinerary", deleteParams,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            int code = Integer.parseInt(response.getString("code"));

                                            if(code == 200)
                                            {
                                                // parse JSON response
                                                String message = response.getString("message");
                                                Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();

                                                // remove item from array and recyclerview
                                                itinerariesList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, itinerariesList.size());
                                            }
                                            else if(code == 400)
                                            {
                                                String errormsg = response.getString("message");
                                                Toast.makeText(v.getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            }

                                            holder.deleteFrame.setVisibility(View.GONE);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(v.getContext(), "Delete itinerary failed! Please check your connection.", Toast.LENGTH_SHORT).show();

                                holder.deleteFrame.setVisibility(View.GONE);
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
                        VolleySingleton.getInstance(v.getContext()).addToRequestQueue(deleteRequest);

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

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}