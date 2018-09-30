package com.afifzafri.backpacktrack;

public class ArticlesModel {

    private String id;
    private String title;
    private String author;
    private String date;
    private String summary;

    public ArticlesModel(String id, String title, String author, String date, String summary) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
        this.summary = summary;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getSummary() {
        return summary;
    }
}