package com.afifzafri.backpacktrack;

public class BudgetTypesModel {
    private String budget_type;
    private String budget_total;

    public BudgetTypesModel(String budget_type, String budget_total) {
        this.budget_type = budget_type;
        this.budget_total = budget_total;
    }

    public String getBudgetType() { return budget_type; }

    public String getBudgetTotal() { return budget_total; }
}
