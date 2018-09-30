package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itinerary_title;
        public TextView itinerary_country;
        public TextView itinerary_duration;
        public TextView itinerary_totalbudget;
        public TextView itinerary_user;
        public CardView mCardView;
        public ImageButton activityBtn;
        public ImageButton editBtn;
        public ImageButton deleteBtn;
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

            itinerary_user = (TextView) v.findViewById(R.id.itinerary_user);
            this.itinerary_user = itinerary_user;

            mCardView = (CardView) v.findViewById(R.id.itinerary_card);
            this.mCardView = mCardView;

            activityBtn = (ImageButton) v.findViewById(R.id.activityBtn);
            this.activityBtn = activityBtn;

            editBtn = (ImageButton) v.findViewById(R.id.editBtn);
            this.editBtn = editBtn;

            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
            this.deleteBtn = deleteBtn;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListMyItinerariesAdapter(List<ItinerariesModel> itinerariesList) {
        this.itinerariesList = itinerariesList;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set itinerary title
        holder.itinerary_title.setText(itinerariesList.get(position).getTitle());

        // Set itinerary title
        holder.itinerary_country.setText(itinerariesList.get(position).getCountry());

        // Set itinerary duration
        holder.itinerary_duration.setText(itinerariesList.get(position).getDuration());

        // Set itinerary total budget
        holder.itinerary_totalbudget.setText(itinerariesList.get(position).getTotalBudget());

        // Set itinerary user name
        holder.itinerary_user.setText(itinerariesList.get(position).getUser());

        // get current position item data
        holder.currentItem = itinerariesList.get(position);

        // add activity button clicked, open add activity
        holder.activityBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                Toast.makeText(v.getContext(), "ACTIVITY ID: "+ itinerariesList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });

        // when edit button clicked, open edit activity
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                Toast.makeText(v.getContext(), "EDIT ID: "+ itinerariesList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });

        // delete button clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(final View v) {
                Toast.makeText(v.getContext(), "DELETE ID: "+ itinerariesList.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}