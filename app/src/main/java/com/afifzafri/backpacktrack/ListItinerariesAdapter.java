package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ListItinerariesAdapter extends RecyclerView.Adapter<ListItinerariesAdapter.MyViewHolder> {

    private List<ItinerariesModel> itinerariesList;
    private String access_token;

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
        public ImageButton shareBtn;
        public TextView itinerary_likes;
        public TextView itinerary_comments;
        public TextView itinerary_user;
        public ImageView itinerary_user_badge;
        public TextView itinerary_date;
        public LinearLayout contentsArea;
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

            shareBtn = (ImageButton) v.findViewById(R.id.shareBtn);
            this.shareBtn = shareBtn;

            itinerary_likes = (TextView) v.findViewById(R.id.itinerary_likes);
            this.itinerary_likes = itinerary_likes;

            itinerary_comments = (TextView) v.findViewById(R.id.itinerary_comments);
            this.itinerary_comments = itinerary_comments;

            itinerary_user = (TextView) v.findViewById(R.id.itinerary_user);
            this.itinerary_user = itinerary_user;

            itinerary_user_badge = (ImageView) v.findViewById(R.id.itinerary_user_badge);
            this.itinerary_user_badge = itinerary_user_badge;

            itinerary_date = (TextView) v.findViewById(R.id.itinerary_date);
            this.itinerary_date = itinerary_date;

            contentsArea = (LinearLayout) v.findViewById(R.id.contentsArea);
            this.contentsArea = contentsArea;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListItinerariesAdapter(List<ItinerariesModel> itinerariesList, String access_token) {
        this.itinerariesList = itinerariesList;
        this.access_token = access_token;
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set itinerary title
        holder.itinerary_title.setText(itinerariesList.get(position).getTitle());

        // Set itinerary country
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
            holder.likeBtn.setImageResource(R.drawable.ic_favorite_border_grey_24dp);
            holder.itinerary_likes.setTextColor(Color.GRAY);
        }

        // Set itinerary total likes
        holder.itinerary_likes.setText("(" + itinerariesList.get(position).getTotalLikes() + ")");

        // Set itinerary total comments
        holder.itinerary_comments.setText("(" + itinerariesList.get(position).getTotalComments() + ")");

        // Set itinerary user name
        holder.itinerary_user.setText(itinerariesList.get(position).getUser());

        // set user badge
        Picasso.get().load(itinerariesList.get(position).getUserBadge()).into(holder.itinerary_user_badge);

        // Set itinerary date
        String crdate = itinerariesList.get(position).getDate();
        holder.itinerary_date.setText(new AppHelper().convertDate(crdate.split(" ")[0]));

        // get current position item data
        holder.currentItem = itinerariesList.get(position);

        // when card is clicked, get id
        holder.contentsArea.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // redirect to view itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewItineraryActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getTitle());
                intentPage.putExtra("itinerary_user_id", itinerariesList.get(position).getUserId());
                v.getContext().startActivity(intentPage);

            }
        });

        // when comment button clicked, open comment tab for the itinerary
        holder.itinerary_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // redirect to view itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewItineraryActivity.class);
                intentPage.putExtra("itinerary_id", itinerariesList.get(position).getId());
                intentPage.putExtra("itinerary_title", itinerariesList.get(position).getTitle());
                intentPage.putExtra("itinerary_user_id", itinerariesList.get(position).getUserId());
                intentPage.putExtra("viewComment", true);
                v.getContext().startActivity(intentPage);
            }
        });

        // when share button clicked, open share intent, to share the itinerary
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String itTitle = itinerariesList.get(position).getTitle();
                String itUrl = AppHelper.baseurl + "/itinerary/" + itinerariesList.get(position).getId();
                String shareBody = itTitle + " " + itUrl;
                //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                v.getContext().startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        // when like button clicked, like or unlike process
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //check whether it is liked or unliked
                if (itinerariesList.get(position).getIsLiked()) {
                    //update unlike drawable
                    itinerariesList.get(position).setIsLiked(false);

                    final int totallikes = Integer.parseInt(itinerariesList.get(position).getTotalLikes());
                    int newtotal = (totallikes > 0) ? (totallikes - 1) : 0;
                    itinerariesList.get(position).setTotallikes(Integer.toString(newtotal));

                    notifyItemChanged(position);

                    // network request to Unlike API
                    JSONObject params = new JSONObject(); // login parameters

                    try {
                        params.put("itinerary_id", itinerariesList.get(position).getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Request a string response from the provided URL.
                    JsonObjectRequest unlikeRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/unlikeItinerary", params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // handle success
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // failed to unlike, revert back like
                            itinerariesList.get(position).setIsLiked(true);
                            itinerariesList.get(position).setTotallikes(Integer.toString(totallikes));
                            notifyItemChanged(position);
                            Toast.makeText(v.getContext(), "Network error.", Toast.LENGTH_SHORT).show();
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
                    VolleySingleton.getInstance(v.getContext()).addToRequestQueue(unlikeRequest);

                } else {
                    //update like drawable
                    itinerariesList.get(position).setIsLiked(true);

                    final int totallikes = Integer.parseInt(itinerariesList.get(position).getTotalLikes());
                    itinerariesList.get(position).setTotallikes(Integer.toString(totallikes + 1));

                    notifyItemChanged(position);

                    // network request to Like API
                    JSONObject params = new JSONObject(); // login parameters

                    try {
                        params.put("itinerary_id", itinerariesList.get(position).getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Request a string response from the provided URL.
                    JsonObjectRequest likeRequest = new JsonObjectRequest(Request.Method.POST, AppHelper.baseurl + "/api/likeItinerary", params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    // handle success
                                    try {

                                        int code = Integer.parseInt(response.getString("code"));
                                        // if error
                                        if(code == 400)
                                        {
                                            String errormsg = response.getString("message");
                                            Toast.makeText(v.getContext(), errormsg, Toast.LENGTH_SHORT).show();
                                            itinerariesList.get(position).setIsLiked(false);
                                            itinerariesList.get(position).setTotallikes(Integer.toString(totallikes));
                                            notifyItemChanged(position);
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // failed to unlike, revert back like
                            itinerariesList.get(position).setIsLiked(false);
                            itinerariesList.get(position).setTotallikes(Integer.toString(totallikes));
                            notifyItemChanged(position);
                            Toast.makeText(v.getContext(), "Network error.", Toast.LENGTH_SHORT).show();
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
                    VolleySingleton.getInstance(v.getContext()).addToRequestQueue(likeRequest);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itinerariesList.size();
    }
}