package com.afifzafri.backpacktrack;

public class ItinerariesModel {

    private String id;
    private String user_id;
    private String user;
    private String user_rank;
    private String user_badge;
    private String title;
    private String country;
    private String duration;
    private String date;
    private String totalbudget;
    private String totallikes;
    private String totalcomments;
    private Boolean isLiked;

    public ItinerariesModel(String id, String user_id, String user, String user_rank, String user_badge, String title, String country, String duration, String date, String totalbudget, String totallikes, String totalcomments, Boolean isLiked) {
        this.id = id;
        this.user_id = user_id;
        this.user = user;
        this.user_rank = user_rank;
        this.user_badge = user_badge;
        this.title = title;
        this.country = country;
        this.duration = duration;
        this.date = date;
        this.totalbudget = totalbudget;
        this.totallikes = totallikes;
        this.totalcomments = totalcomments;
        this.isLiked = isLiked;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getUser() {
        return user;
    }

    public String getUserRank() { return user_rank; }

    public String getUserBadge() { return user_badge; }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public String getDuration() {
        return duration;
    }

    public String getDate() {
        return date;
    }

    public String getTotalBudget() {
        return totalbudget;
    }

    public String getTotalLikes() { return totallikes; }

    public String getTotalComments() { return totalcomments; }

    public Boolean getIsLiked() { return isLiked; }

    public void setIsLiked(Boolean isLiked)
    {
        this.isLiked = isLiked;
    }

    public void setTotallikes(String totallikes)
    {
        this.totallikes = totallikes;
    }
}