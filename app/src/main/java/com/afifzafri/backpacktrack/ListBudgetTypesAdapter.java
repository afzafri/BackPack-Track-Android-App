package com.afifzafri.backpacktrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListBudgetTypesAdapter extends RecyclerView.Adapter<ListBudgetTypesAdapter.MyViewHolder> {

    private List<TotalBudgetTypeModel> budgetList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView budgetType;
        public TextView budgetTotal;
        public ImageView labelColor;

        public MyViewHolder(View v) {
            super(v);
            budgetType = (TextView) v.findViewById(R.id.budgetType);
            this.budgetType = budgetType;

            budgetTotal = (TextView) v.findViewById(R.id.budgetTotal);
            this.budgetTotal = budgetTotal;

            labelColor = (ImageView) v.findViewById(R.id.labelColor);
            this.labelColor = labelColor;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListBudgetTypesAdapter(List<TotalBudgetTypeModel> budgetList) {
        this.budgetList = budgetList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListBudgetTypesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_budget_types, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set budget type
        holder.budgetType.setText(budgetList.get(position).getBudgetType());

        // Set budget total
        holder.budgetTotal.setText(budgetList.get(position).getBudgetTotal());

        // Set label color
        holder.labelColor.setColorFilter(budgetList.get(position).getBudgetColor());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return budgetList.size();
    }
}