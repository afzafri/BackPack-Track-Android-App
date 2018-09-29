package com.afifzafri.backpacktrack;

public class ItinerariesModel {

    private String id;
    private String user;
    private String title;
    private String duration;
    private String totalbudget;

    public ItinerariesModel(String id, String user, String title, String duration, String totalbudget) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.duration = duration;
        this.totalbudget = totalbudget;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public String getTotalBudget() {
        return totalbudget;
    }
}