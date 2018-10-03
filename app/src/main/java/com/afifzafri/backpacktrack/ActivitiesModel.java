package com.afifzafri.backpacktrack;

public class ActivitiesModel {

    private String id;
    private String date;
    private String time;
    private String activity_title;
    private String description;
    private String place_name;
    private String lat;
    private String lng;
    private String budget;
    private String pic_url;
    private String itinerary_id;

    public ActivitiesModel(String id, String date, String time, String activity_title, String description, String place_name, String lat, String lng, String budget, String pic_url, String itinerary_id) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.activity_title = activity_title;
        this.description = description;
        this.place_name = place_name;
        this.lat = lat;
        this.lng = lng;
        this.budget = budget;
        this.pic_url = pic_url;
        this.itinerary_id = itinerary_id;
    }

    public String getId() { return id; }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getActivityTitle() { return activity_title; }

    public String getDescription() { return description; }

    public String getPlaceName() { return place_name; }

    public String getLat() { return lat; }

    public String getLng() { return lng; }

    public String getBudget() { return budget; }

    public String getPicUrl() { return pic_url; }

    public String getItineraryId() { return itinerary_id; }
}