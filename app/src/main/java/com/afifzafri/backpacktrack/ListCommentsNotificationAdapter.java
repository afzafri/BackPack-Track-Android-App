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

public class ListCommentsNotificationAdapter extends RecyclerView.Adapter<ListCommentsNotificationAdapter.MyViewHolder> {

    private List<NotificationsModel> commentsList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView notification_name;
        public TextView notification_username;
        public ImageView notification_user_avatar;
        public TextView itinerary_title;
        public TextView notification_datetime;
        public TextView textNotiType;
        public LinearLayout notification_item;
        public NotificationsModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            notification_name = (TextView) v.findViewById(R.id.notification_name);
            this.notification_name = notification_name;

            notification_username = (TextView) v.findViewById(R.id.notification_username);
            this.notification_username = notification_username;

            notification_user_avatar = (ImageView) v.findViewById(R.id.notification_user_avatar);
            this.notification_user_avatar = notification_user_avatar;

            itinerary_title = (TextView) v.findViewById(R.id.itinerary_title);
            this.itinerary_title = itinerary_title;

            notification_datetime = (TextView) v.findViewById(R.id.notification_datetime);
            this.notification_datetime = notification_datetime;

            textNotiType = (TextView) v.findViewById(R.id.textNotiType);
            this.textNotiType = textNotiType;

            notification_item = (LinearLayout) v.findViewById(R.id.notification_item);
            this.notification_item = notification_item;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListCommentsNotificationAdapter(List<NotificationsModel> commentsList) {
        this.commentsList = commentsList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListCommentsNotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_notification, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set data to display
        if(commentsList.get(position).getNotificationType().equals("comment")) {
            holder.textNotiType.setText("commented on");
        } else if(commentsList.get(position).getNotificationType().equals("like")) {
            holder.textNotiType.setText("likes");
        }

        holder.notification_name.setText(commentsList.get(position).getUserFName());

        holder.notification_name.setTag(commentsList.get(position).getUserId());

        holder.notification_username.setText("@" + commentsList.get(position).getUsername());

        holder.itinerary_title.setText(commentsList.get(position).getItineraryTitle());

        holder.itinerary_title.setTag(commentsList.get(position).getItineraryId());

        String dateTime[] = commentsList.get(position).getDateTime().split(" ");
        String date = dateTime[0];
        String time = dateTime[1].substring(0,5);
        // convert utc to default timezone, then convert 24h to 12h
        String convertedTime = new AppHelper().convertTime(new AppHelper().convertUTCTime(time));
        holder.notification_datetime.setText(new AppHelper().convertDate(date) + "  " + convertedTime);

        String avatar_url = commentsList.get(position).getUserAvatar();
        if(avatar_url != null && !avatar_url.isEmpty() && avatar_url != "null") {
            Picasso.get()
                    .load(avatar_url)
                    .transform(new CropCircleTransformation())
                    .into(holder.notification_user_avatar);
        } else {
            Picasso.get()
                    .load(R.drawable.avatar)
                    .transform(new CropCircleTransformation())
                    .into(holder.notification_user_avatar);
        }

        // get current position item data
        holder.currentItem = commentsList.get(position);

        // when clicked on comment notification, redirect to the itinerary page
        holder.notification_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to view itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewItineraryActivity.class);
                intentPage.putExtra("itinerary_id", commentsList.get(position).getItineraryId());
                intentPage.putExtra("itinerary_title", commentsList.get(position).getItineraryTitle());
                intentPage.putExtra("itinerary_user_id", commentsList.get(position).getItineraryUserId());
                intentPage.putExtra("viewComment", true);
                v.getContext().startActivity(intentPage);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}