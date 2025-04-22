package com.movierecommender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Movie model class that represents a movie with attributes matching the OMDb API response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Movie {
    
    @JsonProperty("imdbID")
    private String imdbId;
    
    @JsonProperty("Title")
    private String title;
    
    @JsonProperty("Year")
    private String year;
    
    @JsonProperty("Poster")
    private String posterUrl;
    
    @JsonProperty("Plot")
    private String plot;
    
    @JsonProperty("Director")
    private String director;
    
    @JsonProperty("Actors")
    private String actors;
    
    @JsonProperty("Genre")
    private String genre;
    
    @JsonProperty("imdbRating")
    private String imdbRating;
    
    @JsonProperty("Runtime")
    private String runtime;
    
    @JsonProperty("Released")
    private String releaseDate;
    
    // Empty constructor for Jackson
    public Movie() {}
    
    // Constructor with basic properties
    public Movie(String imdbId, String title, String year, String posterUrl) {
        this.imdbId = imdbId;
        this.title = title;
        this.year = year;
        this.posterUrl = posterUrl;
    }
    
    // Getters and setters
    public String getImdbId() {
        return imdbId;
    }
    
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getYear() {
        return year;
    }
    
    public void setYear(String year) {
        this.year = year;
    }
    
    public String getPosterUrl() {
        return posterUrl;
    }
    
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
    
    public String getPlot() {
        return plot;
    }
    
    public void setPlot(String plot) {
        this.plot = plot;
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public String getActors() {
        return actors;
    }
    
    public void setActors(String actors) {
        this.actors = actors;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public String getImdbRating() {
        return imdbRating;
    }
    
    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }
    
    public String getRuntime() {
        return runtime;
    }
    
    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", imdbId='" + imdbId + '\'' +
                '}';
    }
}
