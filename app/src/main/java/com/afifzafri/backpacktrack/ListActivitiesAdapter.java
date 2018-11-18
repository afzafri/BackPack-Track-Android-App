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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivitiesAdapter extends RecyclerView.Adapter<ListActivitiesAdapter.MyViewHolder> {

    private List<ActivitiesModel> activitiesList;
    private String access_token;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView activity_title;
        public TextView activity_date;
        public TextView activity_time;
        public TextView activity_budget;
        public TextView activity_place;
        public TextView activity_description;
        public ImageView activity_pic;
        public CardView mCardView;
        public ImageButton editBtn;
        public ImageButton deleteBtn;
        public FrameLayout deleteFrame;
        public ActivitiesModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            activity_title = (TextView) v.findViewById(R.id.activity_title);
            this.activity_title = activity_title;

            activity_date = (TextView) v.findViewById(R.id.activity_date);
            this.activity_date = activity_date;

            activity_time = (TextView) v.findViewById(R.id.activity_time);
            this.activity_time = activity_time;

            activity_budget = (TextView) v.findViewById(R.id.activity_budget);
            this.activity_budget = activity_budget;

            activity_place = (TextView) v.findViewById(R.id.activity_place);
            this.activity_place = activity_place;

            activity_description = (TextView) v.findViewById(R.id.activity_description);
            this.activity_description = activity_description;

            activity_pic = (ImageView) v.findViewById(R.id.activity_pic);
            this.activity_pic = activity_pic;

            mCardView = (CardView) v.findViewById(R.id.activity_card);
            this.mCardView = mCardView;

            editBtn = (ImageButton) v.findViewById(R.id.editBtn);
            this.editBtn = editBtn;

            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
            this.deleteBtn = deleteBtn;

            deleteFrame = (FrameLayout) v.findViewById(R.id.deleteFrame);
            this.deleteFrame = deleteFrame;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListActivitiesAdapter(List<ActivitiesModel> activitiesList, String access_token) {
        this.activitiesList = activitiesList;
        this.access_token = access_token;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListActivitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_activities, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set activity title
        holder.activity_title.setText(StringEscapeUtils.unescapeJava(activitiesList.get(position).getActivityTitle()));

        // Set activity date
        holder.activity_date.setText(new AppHelper().convertDate(activitiesList.get(position).getDate()));

        // Set activity time
        holder.activity_time.setText(new AppHelper().convertTime(activitiesList.get(position).getTime().substring(0,5)));

        // Set activity budget
        holder.activity_budget.setText(activitiesList.get(position).getBudget() + " (" + activitiesList.get(position).getBudgetType() + ")");

        // Set activity place
        holder.activity_place.setText(activitiesList.get(position).getPlaceName());

        // Set activity description
        holder.activity_description.setText(StringEscapeUtils.unescapeJava(activitiesList.get(position).getDescription()));
        
        final String act_pic_url = activitiesList.get(position).getPicUrl();
        if(act_pic_url != null && !act_pic_url.isEmpty() && act_pic_url != "null") {
            // Set activity image
            Picasso.get().load(act_pic_url).into(holder.activity_pic);
        }

        // get current position item data
        holder.currentItem = activitiesList.get(position);

        // on click image, show full screen activity
        holder.activity_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(v.getContext(), ImageFullscreenActivity.class);
                intentPage.putExtra("image_url", act_pic_url);
                intentPage.putExtra("caption", activitiesList.get(position).getActivityTitle());
                v.getContext().startActivity(intentPage);
            }
        });

        // edit button clicked
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPage = new Intent(v.getContext(), EditActivityActivity.class);
                intentPage.putExtra("activity_id", activitiesList.get(position).getId());
                intentPage.putExtra("activity_title", activitiesList.get(position).getActivityTitle());
                v.getContext().startActivity(intentPage);
            }
        });

        // delete button clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {

                // Create dialog box, ask confirmation before proceed
                AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Delete this Activity");
                alert.setMessage("Are you sure you want to delete this activity?");
                // set positive button, yes etc
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        // show loading
                        holder.deleteFrame.setVisibility(View.VISIBLE);

                        JSONObject deleteParams = new JSONObject(); // login parameters

                        try {
                            deleteParams.put("activity_id", activitiesList.get(position).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Request a string response from the provided URL.
                        JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/deleteActivity", deleteParams,
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
                                                activitiesList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, activitiesList.size());
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
                                Toast.makeText(v.getContext(), "Delete activity failed! Please check your connection.", Toast.LENGTH_SHORT).show();

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
        return activitiesList.size();
    }
}