package com.pandeka.CariFilm.service;

import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.pandeka.CariFilm.model.Movie;
import com.pandeka.CariFilm.model.Movies;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class BotTemplate {

    // base create template message (with message and button only)
    public TemplateMessage createButton(String message, String actionTitle, String actionText) {
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                null,
                null,
                message,
                Collections.singletonList(new MessageAction(actionTitle, actionText))
        );

        return new TemplateMessage(actionTitle, buttonsTemplate);
    }

    // create greeting templateMessage
    public TemplateMessage createGreetingButton(String message, String actionTitle, String actionText) {
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(
                null,
                null,
                message,
                Arrays.asList(
                        new MessageAction(actionTitle, actionText),
                        new MessageAction("Lihat Favorite", "Lihat Favorite")
                )
        );

        return new TemplateMessage(actionTitle, buttonsTemplate);
    }

    // greeting message
    public TemplateMessage greetingMessage(Source source, UserProfileResponse sender) {
        String message = "Hi %s! Selamat datang di Easy Movie, cari film favorit kamu yuk!";
        String action = "Lihat Daftar Film";

        if (source instanceof GroupSource) {
            message = String.format(message, "sobat group ez");
        } else if (source instanceof RoomSource) {
            message = String.format(message, "sobat room ez");
        } else if (source instanceof UserSource) {
            message = String.format(message, sender.getDisplayName());
        } else {
            message = "Unknown message source!";
        }

        return createGreetingButton(message, action, action);
    }

    // make a carousel movies
    public TemplateMessage carouselMovies(Movies movies) {
        String image, title, releaseDate;

        CarouselColumn column;
        List<CarouselColumn> carouselColumns = new ArrayList<>();
        for (int i = 0; i < 3; i++) { // looping untill the first 3 item
            Movie movie = movies.getResults().get(i);

            image = "http://image.tmdb.org/t/p/w780" + movie.getBackdropPath();
            image = "http://image.tmdb.org/t/p/w780/jeAQdDX9nguP6YOX6QSWKDPkbBo.jpg";
            title = movie.getTitle();
            releaseDate = movie.getReleaseDate();

            column = new CarouselColumn(image, title, releaseDate,
                    Collections.singletonList(
                            new MessageAction("Add to favorite", "Menambahkan " + title + " kedalam daftar favorite anda")
                    )
            );

            carouselColumns.add(column);
        }

        CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumns);
        return new TemplateMessage("Daftar film yang tayang hari ini!", carouselTemplate);
    }

}
