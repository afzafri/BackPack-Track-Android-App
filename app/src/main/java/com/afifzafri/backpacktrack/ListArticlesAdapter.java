package com.afifzafri.backpacktrack;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListArticlesAdapter extends RecyclerView.Adapter<ListArticlesAdapter.MyViewHolder> {

    private List<ArticlesModel> articlesList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView article_title;
        public TextView article_author;
        public TextView article_date;
        public TextView article_summary;
        public CardView mCardView;
        public ArticlesModel currentItem;

        public MyViewHolder(View v) {
            super(v);
            article_title = (TextView) v.findViewById(R.id.article_title);
            this.article_title = article_title;

            article_author = (TextView) v.findViewById(R.id.article_author);
            this.article_author = article_author;

            article_date = (TextView) v.findViewById(R.id.article_date);
            this.article_date = article_date;

            article_summary = (TextView) v.findViewById(R.id.article_summary);
            this.article_summary = article_summary;

            mCardView = (CardView) v.findViewById(R.id.article_card);
            this.mCardView = mCardView;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListArticlesAdapter(List<ArticlesModel> articlesList) {
        this.articlesList = articlesList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListArticlesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_articles, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set article title
        holder.article_title.setText(articlesList.get(position).getTitle());

        // Set article author
        holder.article_author.setText(articlesList.get(position).getAuthor());

        // Set article date
        holder.article_date.setText(new AppHelper().convertDate(articlesList.get(position).getDate()));

        // Set article summary
        holder.article_summary.setText(articlesList.get(position).getSummary());

        // when card is clicked, get id
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                // item clicked
                // redirect to list itinerary page
                Intent intentPage = new Intent(v.getContext(), ViewArticleActivity.class);
                intentPage.putExtra("article_id", articlesList.get(position).getId());
                v.getContext().startActivity(intentPage);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return articlesList.size();
    }
}