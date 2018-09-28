package com.afifzafri.backpacktrack;

public class VisitedCountriesModel {

    private String name;
    private String code;
    private String id;

    public VisitedCountriesModel(String name, String code, String id) {
        this.name = name;
        this.code = code;
        this.id = id;
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
}