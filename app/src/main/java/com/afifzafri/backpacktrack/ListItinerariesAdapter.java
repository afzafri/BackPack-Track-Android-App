package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class ListItinerariesAdapter extends RecyclerView.Adapter<ListItinerariesAdapter.MyViewHolder> {

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
        public ImageButton likeBtn;
        public TextView itinerary_likes;
        public TextView itinerary_comments;
        public TextView itinerary_user;
        public TextView itinerary_date;
        public CardView mCardView;
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

            likeBtn = (ImageButton) v.findViewById(R.id.likeBtn);
            this.likeBtn = likeBtn;

            itinerary_likes = (TextView) v.findViewById(R.id.itinerary_likes);
            this.itinerary_likes = itinerary_likes;

            itinerary_comments = (TextView) v.findViewById(R.id.itinerary_comments);
            this.itinerary_comments = itinerary_comments;

            itinerary_user = (TextView) v.findViewById(R.id.itinerary_user);
            this.itinerary_user = itinerary_user;

            itinerary_date = (TextView) v.findViewById(R.id.itinerary_date);
            this.itinerary_date = itinerary_date;

            mCardView = (CardView) v.findViewById(R.id.itinerary_card);
            this.mCardView = mCardView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListItinerariesAdapter(List<ItinerariesModel> itinerariesList) {
        this.itinerariesList = itinerariesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListItinerariesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_itineraries, parent, false);
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

        //setting liked status initially, if user liked it previously
        if (itinerariesList.get(position).getIsLiked()) {
            holder.likeBtn.setImageResource(R.drawable.ic_favorite_red_24dp);
            holder.itinerary_likes.setTextColor(Color.RED);
        } else {
            holder.likeBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            holder.itinerary_likes.setTextColor(Color.GRAY);
        }

        // Set itinerary total likes
        holder.itinerary_likes.setText("(" + itinerariesList.get(position).getTotalLikes() + ")");

        // Set itinerary total comments
        holder.itinerary_comments.setText("(" + itinerariesList.get(position).getTotalComments() + ")");

        // Set itinerary user name
        holder.itinerary_user.setText(itinerariesList.get(position).getUser());

        // Set itinerary date
        String crdate = itinerariesList.get(position).getDate();
        holder.itinerary_date.setText(new AppHelper().convertDate(crdate.split(" ")[0]));

        // get current position item data
        holder.currentItem = itinerariesList.get(position);

        // when card is clicked, get id
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // redirect to view itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewItineraryActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getTitle());
                intentPage.putExtra("itinerary_user_id", itinerariesList.get(position).getUserId());
                v.getContext().startActivity(intentPage);

            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}