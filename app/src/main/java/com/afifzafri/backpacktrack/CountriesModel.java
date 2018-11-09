package com.afifzafri.backpacktrack;

public class CountriesModel {

    private String name;
    private String code;
    private String id;
    private String totalitineraries;

    public CountriesModel(String name, String code, String id, String totalitineraries) {
        this.name = name;
        this.code = code;
        this.id = id;
        this.totalitineraries = totalitineraries;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    public String getTotalItineraries() { return totalitineraries; }
}