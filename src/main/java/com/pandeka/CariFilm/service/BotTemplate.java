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

import java.time.format.DateTimeFormatter;
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
            message = String.format(message, "sobat group izy");
        } else if (source instanceof RoomSource) {
            message = String.format(message, "sobat room izy");
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
        int id;

        CarouselColumn column;
        List<CarouselColumn> carouselColumns = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // looping untill the first 10 item
            Movie movie = movies.getResults().get(i);

            id = movie.getId();
            image = "https://image.tmdb.org/t/p/w780" + movie.getBackdropPath();
            title = movie.getTitle();
            releaseDate = "Release: " + movie.getReleaseDate();

            column = new CarouselColumn(image, title, releaseDate,
                    Collections.singletonList(
                            new MessageAction("Add to Favorite", "Menambahkan \"" + title + "\" kedalam daftar favorite anda. [" + id + "]")
                    )
            );

            carouselColumns.add(column);
        }

        CarouselTemplate carouselTemplate = new CarouselTemplate(carouselColumns);
        return new TemplateMessage("Daftar film yang tayang hari ini!", carouselTemplate);
    }

}
