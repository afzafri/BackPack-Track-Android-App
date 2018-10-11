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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.vipulasri.timelineview.TimelineView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivitiesAdapter extends RecyclerView.Adapter<ListViewActivitiesAdapter.MyViewHolder> {

    private Context mContext;
    private List<ActivitiesModel> activitiesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView activity_title;
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
        public TimelineView mTimelineView;

        public MyViewHolder(View v, int viewType) {
            super(v);
            activity_title = (TextView) v.findViewById(R.id.activity_title);
            this.activity_title = activity_title;

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

            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            mTimelineView.initLine(viewType);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListViewActivitiesAdapter(Context mContext, List<ActivitiesModel> activitiesList) {
        this.mContext = mContext;
        this.activitiesList = activitiesList;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListViewActivitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_activities, parent, false);
        MyViewHolder vh= new MyViewHolder(v, viewType);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set activity title
        holder.activity_title.setText(activitiesList.get(position).getActivityTitle());

        // Set activity time
        holder.activity_time.setText(new AppHelper().convertTime(activitiesList.get(position).getTime().substring(0,5)));

        // Set activity budget
        holder.activity_budget.setText(activitiesList.get(position).getBudget() + " (" + activitiesList.get(position).getBudgetType() + ")");

        // Set activity place
        holder.activity_place.setText(activitiesList.get(position).getPlaceName());

        // Set activity description
        holder.activity_description.setText(activitiesList.get(position).getDescription());
        
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

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return activitiesList.size();
    }
}