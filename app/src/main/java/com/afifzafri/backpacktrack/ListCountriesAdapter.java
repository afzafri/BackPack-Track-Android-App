package com.afifzafri.backpacktrack;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListCountriesAdapter extends RecyclerView.Adapter<ListCountriesAdapter.MyViewHolder> {

    private List<VisitedCountriesModel> countriesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public CardView mCardView;
        public VisitedCountriesModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.country_name);
            this.mTextView = mTextView;

            mImageView = (ImageView) v.findViewById(R.id.country_flag);
            this.mImageView = mImageView;

            mCardView = (CardView) v.findViewById(R.id.country_card);
            this.mCardView = mCardView;

            // when card is clicked, get id
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    // item clicked
                    // redirect to list itinerary page
                    Intent intentPage = new Intent(v.getContext(), CountryItinerariesActivity.class);
                    intentPage.putExtra("country_id", currentItem.getId());
                    intentPage.putExtra("country_name", currentItem.getName());
                    v.getContext().startActivity(intentPage);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListCountriesAdapter(List<VisitedCountriesModel> countriesList) {
        this.countriesList = countriesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListCountriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_countries, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set country flag
        String flagurl = "https://www.countryflags.io/"+countriesList.get(position).getCode()+"/flat/64.png";
        Picasso.get().load(flagurl).into(holder.mImageView);

        // Set country name
        holder.mTextView.setText(countriesList.get(position).getName());

        // Hide country id into image
        holder.mImageView.setTag(countriesList.get(position).getId());

        // get current position item data
        holder.currentItem = countriesList.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return countriesList.size();
    }
}