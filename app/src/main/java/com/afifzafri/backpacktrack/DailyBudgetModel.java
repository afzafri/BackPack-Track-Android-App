package com.afifzafri.backpacktrack;

public class DailyBudgetModel {
    private String day;
    private String date;
    private String budget;

    public DailyBudgetModel(String day, String date, String budget) {
        this.day = day;
        this.date = date;
        this.budget = budget;
    }

    public String getDay() { return day; }

    public String getDate() { return date; }

    public String getBudget() { return budget; }
}
