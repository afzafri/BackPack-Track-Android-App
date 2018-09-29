package com.afifzafri.backpacktrack;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

            // activity button clicked
            activityBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // item clicked
                    Toast.makeText(v.getContext(), "ACTIVITY ID: "+ currentItem.getId(), Toast.LENGTH_SHORT).show();

                }
            });

            // edit button clicked
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // item clicked
                    Toast.makeText(v.getContext(), "EDIT ID: "+ currentItem.getId(), Toast.LENGTH_SHORT).show();

                }
            });

            // delete button clicked
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // item clicked
                    Toast.makeText(v.getContext(), "DELETE ID: "+ currentItem.getId(), Toast.LENGTH_SHORT).show();

                }
            });
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}