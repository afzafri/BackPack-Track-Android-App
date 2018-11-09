package com.afifzafri.backpacktrack;

public class UsersModel {

    private String id;
    private String name;
    private String username;
    private String country;
    private String avatar;
    private String totalitineraries;
    private String rank;
    private String badge;

    public UsersModel(String id, String name, String username, String country, String avatar, String totalitineraries, String rank, String badge) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.country = country;
        this.avatar = avatar;
        this.totalitineraries = totalitineraries;
        this.rank = rank;
        this.badge = badge;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() { return username; }

    public String getCountry() { return country; }

    public String getAvatar() { return avatar; }

    public String getTotalItineraries() { return totalitineraries; }

    public String getRank() { return rank; }

    public String getBadge() { return badge; }
}