package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ListPopularItinerariesAdapter extends RecyclerView.Adapter<ListPopularItinerariesAdapter.MyViewHolder> {

    private List<PopularItinerariesModel> itinerariesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout popular_item;
        public TextView itinerary_title;
        public TextView itinerary_user;
        public TextView itinerary_country;
        public TextView totallikes;
        public PopularItinerariesModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            popular_item = (LinearLayout) v.findViewById(R.id.popular_item);
            this.popular_item = popular_item;

            itinerary_title = (TextView) v.findViewById(R.id.itinerary_title);
            this.itinerary_title = itinerary_title;

            itinerary_user = (TextView) v.findViewById(R.id.itinerary_user);
            this.itinerary_user = itinerary_user;

            itinerary_country = (TextView) v.findViewById(R.id.itinerary_country);
            this.itinerary_country = itinerary_country;

            totallikes = (TextView) v.findViewById(R.id.totallikes);
            this.totallikes = totallikes;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListPopularItinerariesAdapter(List<PopularItinerariesModel> itinerariesList) {
        this.itinerariesList = itinerariesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListPopularItinerariesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_popular_itineraries, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set data to display
        holder.itinerary_title.setText(itinerariesList.get(position).getItineraryTitle());

        holder.itinerary_title.setTag(itinerariesList.get(position).getItineraryId());

        // if name too long, cut off the name, and add ellipsis
        String name = itinerariesList.get(position).getItineraryPosterName();
        if(name.length() > 15) {
            name = name.substring(0,12) + "...";
        }
        holder.itinerary_user.setText(name);

        holder.itinerary_user.setTag(itinerariesList.get(position).getItineraryPosterId());

        holder.itinerary_country.setText(itinerariesList.get(position).getItineraryCountry());

        holder.totallikes.setText(itinerariesList.get(position).getTotalLikes());

        // on click itinerary, open the itinerary
        holder.popular_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to view itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewItineraryActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getItineraryId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getItineraryTitle());
                intentPage.putExtra("itinerary_user_id", itinerariesList.get(position).getItineraryPosterId());
                v.getContext().startActivity(intentPage);
            }
        });

        // get current position item data
        holder.currentItem = itinerariesList.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}