package com.afifzafri.backpacktrack;

public class BudgetTypeModel {

    private String id;
    private String type;

    public BudgetTypeModel(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() { return id; }

    public String getType() { return type; }

    @Override
    public String toString() { return type; }

}
