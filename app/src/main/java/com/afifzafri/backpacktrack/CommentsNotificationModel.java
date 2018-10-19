package com.afifzafri.backpacktrack;

public class CommentsNotificationModel {

    private String id;
    private String date_time;
    private String user_id;
    private String user_name;
    private String user_username;
    private String user_avatar;
    private String itinerary_id;
    private String itinerary_title;
    private String itinerary_user_id;

    public CommentsNotificationModel(String id, String date_time, String user_id, String user_name, String user_username, String user_avatar, String itinerary_id, String itinerary_title, String itinerary_user_id) {
        this.id = id;
        this.date_time = date_time;
        this.user_id = user_id;
        this.user_name = user_name;
        this.user_username = user_username;
        this.user_avatar = user_avatar;
        this.itinerary_id = itinerary_id;
        this.itinerary_title = itinerary_title;
        this.itinerary_user_id = itinerary_user_id;
    }

    public String getId() { return id; }

    public String getDateTime() { return date_time; }

    public String getUserId() { return user_id; }

    public String getUserFName() { return user_name; }

    public String getUsername() { return user_username; }

    public String getUserAvatar() { return user_avatar; }

    public String getItineraryId() { return itinerary_id; }

    public String getItineraryTitle() { return itinerary_title; }

    public String getItineraryUserId() { return itinerary_user_id; }
}