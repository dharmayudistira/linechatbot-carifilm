package com.pandeka.CariFilm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.linecorp.bot.model.event.Event;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LineEvents {

    private final List<Event> events;

    public LineEvents(@JsonProperty("events") List<Event> events) {
        this.events = events != null ? events : Collections.emptyList();
    }

    public List<Event> getEvents() {
        return events;
    }

}
