package com.afifzafri.backpacktrack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ListDailyBudgetAdapter extends RecyclerView.Adapter<ListDailyBudgetAdapter.MyViewHolder> {

    private List<DailyBudgetModel> dailyList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView budgetDay;
        public TextView budgetBudget;

        public MyViewHolder(View v) {
            super(v);
            budgetDay = (TextView) v.findViewById(R.id.budgetDay);
            this.budgetDay = budgetDay;

            budgetBudget = (TextView) v.findViewById(R.id.budgetBudget);
            this.budgetBudget = budgetBudget;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListDailyBudgetAdapter(List<DailyBudgetModel> dailyList) {
        this.dailyList = dailyList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListDailyBudgetAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View v= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_daily_budgets, parent, false);
        MyViewHolder vh= new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        // Set budget type
        holder.budgetDay.setText(dailyList.get(position).getDay());

        // Set budget total
        holder.budgetBudget.setText(dailyList.get(position).getBudget());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dailyList.size();
    }
}