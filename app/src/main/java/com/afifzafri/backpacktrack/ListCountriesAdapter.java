package com.afifzafri.backpacktrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListCountriesAdapter extends RecyclerView.Adapter<ListCountriesAdapter.MyViewHolder> {
    private List<String> cname = new ArrayList<String>();
    private List<String> ccode = new ArrayList<String>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public MyViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.country_name);
            this.mTextView = mTextView;

            mImageView = (ImageView) v.findViewById(R.id.country_flag);
            this.mImageView = mImageView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListCountriesAdapter(List<String> countrylist, List<String> countriescode) {
        cname = countrylist;
        ccode = countriescode;
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
        String flagurl = "https://www.countryflags.io/"+ccode.get(position)+"/flat/64.png";
        Picasso.get().load(flagurl).into(holder.mImageView);

        // Set country name
        holder.mTextView.setText(cname.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cname.size();
    }
}