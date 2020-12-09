package com.pandeka.CariFilm.model;

public class Favorite {

    public String userId;
    public String displayName;
    public int movieId;
    public String movieTitle;

    public Favorite(String userId, String displayName, int movieId, String movieTitle) {
        this.userId = userId;
        this.displayName = displayName;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
    }

    public Favorite() {
    }
}
