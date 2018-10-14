package com.afifzafri.backpacktrack;

public class CommentsModel {

    private String id;
    private String user_name;
    private String username;
    private String user_id;
    private String user_avatar;
    private String message;
    private String date_time;

    public CommentsModel(String id, String user_name, String username, String user_id, String user_avatar, String message, String date_time) {
        this.id = id;
        this.user_name = user_name;
        this.username = username;
        this.user_id = user_id;
        this.user_avatar = user_avatar;
        this.message = message;
        this.date_time = date_time;
    }

    public String getId() { return id; }

    public String getUserFName() { return user_name; }

    public String getUsername() { return username; }

    public String getUserId() { return user_id; }

    public String getUserAvatar() { return user_avatar; }

    public String getMessage() { return message; }

    public String getDateTime() { return date_time; }

}
