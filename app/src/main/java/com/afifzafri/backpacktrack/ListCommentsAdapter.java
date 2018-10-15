package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ListCommentsAdapter extends RecyclerView.Adapter<ListCommentsAdapter.MyViewHolder> {

    private List<CommentsModel> commentsList;
    private String authUserId;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView comment_name;
        public TextView comment_username;
        public TextView comment_datetime;
        public TextView comment_message;
        public ImageView comment_user_avatar;
        public ImageButton deleteBtn;
        public CommentsModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            comment_name = (TextView) v.findViewById(R.id.comment_name);
            this.comment_name = comment_name;

            comment_username = (TextView) v.findViewById(R.id.comment_username);
            this.comment_username = comment_username;

            comment_datetime = (TextView) v.findViewById(R.id.comment_datetime);
            this.comment_datetime = comment_datetime;

            comment_message = (TextView) v.findViewById(R.id.comment_message);
            this.comment_message = comment_message;

            comment_user_avatar = (ImageView) v.findViewById(R.id.comment_user_avatar);
            this.comment_user_avatar = comment_user_avatar;

            deleteBtn = (ImageButton) v.findViewById(R.id.deleteBtn);
            this.deleteBtn = deleteBtn;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListCommentsAdapter(List<CommentsModel> commentsList, String authUserId) {
        this.commentsList = commentsList;
        this.authUserId = authUserId;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListCommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_comments, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set data to display
        holder.comment_name.setText(commentsList.get(position).getUserFName());

        holder.comment_name.setTag(commentsList.get(position).getUserId());

        holder.comment_username.setText("@" + commentsList.get(position).getUsername());

        String dateTime[] = commentsList.get(position).getDateTime().split(" ");
        String date = dateTime[0];
        String time = dateTime[1].substring(0,5);
        // convert utc to default timezone, then convert 24h to 12h
        String convertedTime = new AppHelper().convertTime(new AppHelper().convertUTCTime(time));
        holder.comment_datetime.setText(new AppHelper().convertDate(date) + "  " + convertedTime);

        holder.comment_message.setText(commentsList.get(position).getMessage());

        holder.comment_message.setTag(commentsList.get(position).getId());

        String avatar_url = commentsList.get(position).getUserAvatar();
        if(avatar_url != null && !avatar_url.isEmpty() && avatar_url != "null") {
            Picasso.get()
                    .load(avatar_url)
                    .transform(new CropCircleTransformation())
                    .into(holder.comment_user_avatar);
        } else {
            Picasso.get()
                    .load(R.drawable.avatar)
                    .transform(new CropCircleTransformation())
                    .into(holder.comment_user_avatar);
        }

        // if current logged in user id equals to comment user id, show delete button
        if(authUserId.equals(commentsList.get(position).getUserId())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }

        // get current position item data
        holder.currentItem = commentsList.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}