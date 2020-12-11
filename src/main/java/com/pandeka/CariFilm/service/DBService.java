package com.pandeka.CariFilm.service;

import com.pandeka.CariFilm.database.Dao;
import com.pandeka.CariFilm.model.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DBService {

    @Autowired
    private Dao mDao;

    // add movie into user favorite list
    public int addToFavorite(int movieId, String movieTitle, String userId) {
        return mDao.addToFavorite(movieId, movieTitle, userId);
    }

    public List<Favorite> getFavorite(String userId) {
        return mDao.getFavoriteById(userId);
    }

}
