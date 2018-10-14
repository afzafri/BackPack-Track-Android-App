package com.afifzafri.backpacktrack;

public class TotalBudgetTypeModel {
    private String budget_type;
    private String budget_total;
    private int budget_color;

    public TotalBudgetTypeModel(String budget_type, String budget_total, int budget_color) {
        this.budget_type = budget_type;
        this.budget_total = budget_total;
        this.budget_color = budget_color;
    }

    public String getBudgetType() { return budget_type; }

    public String getBudgetTotal() { return budget_total; }

    public int getBudgetColor() { return budget_color; }

}
