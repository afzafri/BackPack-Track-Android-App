package com.afifzafri.backpacktrack;

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

import com.squareup.picasso.Picasso;

import java.util.List;

public class ListActivitiesAdapter extends RecyclerView.Adapter<ListActivitiesAdapter.MyViewHolder> {

    private List<ActivitiesModel> activitiesList;

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
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListActivitiesAdapter(List<ActivitiesModel> activitiesList) {
        this.activitiesList = activitiesList;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set activity title
        holder.activity_title.setText(activitiesList.get(position).getActivityTitle());

        // Set activity date
        holder.activity_date.setText(activitiesList.get(position).getDate());

        // Set activity time
        holder.activity_time.setText(activitiesList.get(position).getTime());

        // Set activity budget
        holder.activity_budget.setText(activitiesList.get(position).getBudget());

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