package com.pandeka.CariFilm.model;

public class Favorite {

    public String userId;
    public int movieId;
    public String movieTitle;

    public Favorite(int movieId, String movieTitle, String userId) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.userId = userId;
    }

    public Favorite() {
    }
}
