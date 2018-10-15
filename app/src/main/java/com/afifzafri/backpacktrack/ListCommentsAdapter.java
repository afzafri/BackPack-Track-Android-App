package com.afifzafri.backpacktrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ListCommentsAdapter extends RecyclerView.Adapter<ListCommentsAdapter.MyViewHolder> {

    private List<CommentsModel> commentsList;
    private String authUserId;
    private String itinerary_user_id;
    private String access_token;

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
        public FrameLayout deleteFrame;
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

            deleteFrame = (FrameLayout) v.findViewById(R.id.deleteFrame);
            this.deleteFrame = deleteFrame;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListCommentsAdapter(List<CommentsModel> commentsList, String authUserId, String itinerary_user_id, String access_token) {
        this.commentsList = commentsList;
        this.authUserId = authUserId;
        this.itinerary_user_id = itinerary_user_id;
        this.access_token = access_token;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
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

        // if current logged in user id equals to comment user id,
        // or current logged in user id equals itinerary owner id,
        // show delete button
        // and allow delete
        if(authUserId.equals(commentsList.get(position).getUserId()) || authUserId.equals(itinerary_user_id)) {
            holder.deleteBtn.setVisibility(View.VISIBLE);

            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Create dialog box, ask confirmation before proceed
                    AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                    alert.setTitle("Delete user's comment");
                    alert.setMessage("Are you sure you want to delete this comment?");
                    // set positive button, yes etc
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            // show loading
                            holder.deleteFrame.setVisibility(View.VISIBLE);

                            JSONObject deleteParams = new JSONObject(); // login parameters

                            try {
                                deleteParams.put("comment_id", commentsList.get(position).getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Request a string response from the provided URL.
                            JsonObjectRequest deleteRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/deleteComment", deleteParams,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {

                                            try {

                                                int code = Integer.parseInt(response.getString("code"));

                                                if(code == 200)
                                                {
                                                    // parse JSON response
                                                    String message = response.getString("message");
                                                    Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();

                                                    // remove item from array and recyclerview
                                                    commentsList.remove(position);
                                                    notifyItemRemoved(position);
                                                }
                                                else if(code == 400)
                                                {
                                                    String errormsg = response.getString("message");
                                                    Toast.makeText(v.getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                                }

                                                holder.deleteFrame.setVisibility(View.GONE);

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(v.getContext(), "Unable to delete your comment! Please check your connection.", Toast.LENGTH_SHORT).show();

                                    holder.deleteFrame.setVisibility(View.GONE);
                                }
                            })
                            {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("Authorization", "Bearer "+access_token);

                                    return params;
                                }
                            };

                            // Add the request to the VolleySingleton.
                            VolleySingleton.getInstance(v.getContext()).addToRequestQueue(deleteRequest);

                        }
                    });
                    // set negative button, no etc
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    alert.show(); // show alert message
                }
            });
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