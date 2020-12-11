package com.pandeka.CariFilm.database;

import com.pandeka.CariFilm.model.Favorite;

import java.util.List;

public interface Dao {

    public List<Favorite> getFavoriteById(String userId);
    public int addToFavorite(int movieId, String movieTitle, String userId);

}
