package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ListPopularCountriesAdapter extends RecyclerView.Adapter<ListPopularCountriesAdapter.MyViewHolder> {

    private List<CountriesModel> countriesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView country_flag;
        public TextView total_itineraries;
        public LinearLayout popular_item;
        public CountriesModel currentItem;

        public MyViewHolder(View v) {
            super(v);

            country_flag = (ImageView) v.findViewById(R.id.country_flag);
            this.country_flag = country_flag;

            total_itineraries = (TextView) v.findViewById(R.id.total_itineraries);
            this.total_itineraries = total_itineraries;

            popular_item = (LinearLayout) v.findViewById(R.id.popular_item);
            this.popular_item = popular_item;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListPopularCountriesAdapter(List<CountriesModel> countriesList) {
        this.countriesList = countriesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListPopularCountriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_popular_countries, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set country flag
        String flagurl = AppHelper.baseurl+"/images/flags/shiny/64/"+countriesList.get(position).getCode()+".png";
        Picasso.get()
                .load(flagurl)
                .into(holder.country_flag);

        // Hide country id into image
        holder.country_flag.setTag(countriesList.get(position).getId());

        // Set country total itineraries
        holder.total_itineraries.setText(countriesList.get(position).getTotalItineraries());

        // get current position item data
        holder.currentItem = countriesList.get(position);

        // when card is clicked, get id
        holder.popular_item.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                // redirect to list itinerary page
                Intent intentPage = new Intent(v.getContext(), CountryItinerariesActivity.class);
                intentPage.putExtra("country_id", countriesList.get(position).getId());
                intentPage.putExtra("country_name", countriesList.get(position).getName());
                v.getContext().startActivity(intentPage);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return countriesList.size();
    }
}