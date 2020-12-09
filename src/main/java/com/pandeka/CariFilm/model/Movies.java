package com.pandeka.CariFilm.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "results"
})
public class Movies {

    @JsonProperty("results")
    private List<Movie> results = null;

    @JsonProperty("results")
    public void setResults(List<Movie> results) {
        this.results = results;
    }

    @JsonProperty("results")
    public List<Movie> getResults() {
        return results;
    }
}
