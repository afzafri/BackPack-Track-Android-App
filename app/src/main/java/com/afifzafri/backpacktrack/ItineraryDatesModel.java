package com.afifzafri.backpacktrack;

import java.util.List;

public class ItineraryDatesModel {

    private String date;
    private List<ActivitiesModel> activitiesList;

    public ItineraryDatesModel(String date, List<ActivitiesModel> activitiesList) {
        this.date = date;
        this.activitiesList = activitiesList;
    }

    public String getDate() { return date; }

    public List<ActivitiesModel> getActivitiesList() { return activitiesList; }
}