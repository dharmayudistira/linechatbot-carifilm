package com.pandeka.CariFilm.database;

import com.pandeka.CariFilm.model.Favorite;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class DaoImpl implements Dao{

    private final static String FAVORITE_TABLE = "table_favorite";
    private final static String SQL_SELECT_ALL = "SELECT user_id, display_name, movie_id, movie_title FROM " + FAVORITE_TABLE;
    private final static String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE LOWER(user_id) LIKE LOWER(?);";
    private final static String SQL_INSERT = "INSERT INTO " + FAVORITE_TABLE + " (user_id, display_name, movie_id, movie_title) VALUES (?, ?, ?, ?);";

    private JdbcTemplate mJdbc;

    private final static ResultSetExtractor<Favorite> SINGLE_RS_EXECUTOR = new ResultSetExtractor<Favorite>() {
        @Override
        public Favorite extractData(ResultSet rs) throws SQLException, DataAccessException {
            while (rs.next()) {
                Favorite favorite = new Favorite(
                        rs.getString("user_id"),
                        rs.getString("display_name"),
                        rs.getInt("movie_id"),
                        rs.getString("movie_title")
                );
                return favorite;
            }

            return null;
        }
    };

    private final static ResultSetExtractor<List<Favorite>> MULTIPLE_RS_EXECUTOR = new ResultSetExtractor<List<Favorite>>() {
        @Override
        public List<Favorite> extractData(ResultSet rs) throws SQLException, DataAccessException {

            List<Favorite> favorites = new Vector<Favorite>();

            while (rs.next()) {
                Favorite favorite = new Favorite(
                        rs.getString("user_id"),
                        rs.getString("display_name"),
                        rs.getInt("movie_id"),
                        rs.getString("movie_title")
                );

                favorites.add(favorite);
            }

            return favorites;
        }
    };

    public DaoImpl(DataSource dataSource) {
        mJdbc = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Favorite> getFavoriteById(String userId) {
        return mJdbc.query(SQL_SELECT_BY_ID, new Object[]{"%"+userId+"%"}, MULTIPLE_RS_EXECUTOR);
    }

    @Override
    public int addToFavorite(int movieId, String movieTitle, String userId) {
        return mJdbc.update(SQL_INSERT, new Object[]{movieId, movieTitle, userId});
    }

}
