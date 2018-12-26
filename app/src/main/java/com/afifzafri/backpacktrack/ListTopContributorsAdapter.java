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

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ListTopContributorsAdapter extends RecyclerView.Adapter<ListTopContributorsAdapter.MyViewHolder> {

    private List<UsersModel> usersList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView user_avatar;
        public TextView user_username;
        public ImageView user_badge;
        public LinearLayout popular_item;

        public MyViewHolder(View v) {
            super(v);
            user_avatar = (ImageView) v.findViewById(R.id.user_avatar);
            this.user_avatar = user_avatar;

            user_username = (TextView) v.findViewById(R.id.user_username);
            this.user_username = user_username;

            user_badge = (ImageView) v.findViewById(R.id.user_badge);
            this.user_badge = user_badge;

            popular_item = (LinearLayout) v.findViewById(R.id.popular_item);
            this.popular_item = popular_item;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListTopContributorsAdapter(List<UsersModel> usersList) {
        this.usersList = usersList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListTopContributorsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_top_contributors, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // set avatar image using Picasso library
        String avatar_url = usersList.get(position).getAvatar();
        if(avatar_url != null && !avatar_url.isEmpty() && avatar_url != "null") {
            // Set user avatar
            Picasso.get()
                    .load(avatar_url)
                    .transform(new CropCircleTransformation())
                    .into(holder.user_avatar);
            // set user id tag
            holder.user_username.setTag(usersList.get(position).getId());
        } else {
            Picasso.get()
                    .load(R.drawable.avatar)
                    .transform(new CropCircleTransformation())
                    .into(holder.user_avatar);
        }

        // Set user username
        holder.user_username.setText("@" + usersList.get(position).getUsername());

        // set user badge
        Picasso.get().load(usersList.get(position).getBadge()).into(holder.user_badge);

        // when card is clicked, get id
        holder.popular_item.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                // redirect to list itinerary page
                Intent intentPage = new Intent(v.getContext(), UserProfileActivity.class);
                intentPage.putExtra("user_id", usersList.get(position).getId());
                v.getContext().startActivity(intentPage);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return usersList.size();
    }
}