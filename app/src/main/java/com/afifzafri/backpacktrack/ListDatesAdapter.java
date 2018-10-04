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

public class ListDatesAdapter extends RecyclerView.Adapter<ListDatesAdapter.MyViewHolder> {

    private List<ItineraryDatesModel> allDataList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView activity_date;


        public MyViewHolder(View v) {
            super(v);
            activity_date = (TextView) v.findViewById(R.id.activity_date);
            this.activity_date = activity_date;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListDatesAdapter(List<ItineraryDatesModel> allDataList) {
        this.allDataList = allDataList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListDatesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_itinerary_dates, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set itinerary title
        holder.activity_date.setText(allDataList.get(position).getDate());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return allDataList.size();
    }
}