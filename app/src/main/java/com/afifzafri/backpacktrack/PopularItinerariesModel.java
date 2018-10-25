package com.afifzafri.backpacktrack;

public class PopularItinerariesModel {

    private String itinerary_id;
    private String itinerary_title;
    private String itinerary_country;
    private String itinerary_poster_id;
    private String itinerary_poster_name;
    private String totallikes;

    public PopularItinerariesModel(String itinerary_id, String itinerary_title, String itinerary_country, String itinerary_poster_id, String itinerary_poster_name, String totallikes) {
        this.itinerary_id = itinerary_id;
        this.itinerary_title = itinerary_title;
        this.itinerary_country = itinerary_country;
        this.itinerary_poster_id = itinerary_poster_id;
        this.itinerary_poster_name = itinerary_poster_name;
        this.totallikes = totallikes;
    }

    public String getItineraryId() { return itinerary_id; }

    public String getItineraryTitle() { return itinerary_title; }

    public String getItineraryCountry() { return itinerary_country; }

    public String getItineraryPosterId() { return itinerary_poster_id; }

    public String getItineraryPosterName() { return itinerary_poster_name; }

    public String getTotalLikes() { return totallikes; }
}
