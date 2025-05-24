package com.movierecommender.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movierecommender.model.Movie;
import com.movierecommender.util.ApiKeyManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Service class to interact with the OMDb API
 */
public class OmdbApiService {
    private static final String BASE_URL = "http://www.omdbapi.com/";
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public OmdbApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
        this.apiKey = ApiKeyManager.getApiKey();
    }

    /**
     * Search movies by title
     * 
     * @param title the title to search for
     * @param page the page number of results
     * @return a list of movies matching the search criteria
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> searchMovies(String title, int page) throws IOException {
        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
        String url = String.format("%s?apikey=%s&s=%s&page=%d", BASE_URL, apiKey, encodedTitle, page);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Implement retry logic
        int maxRetries = 3;
        int retryCount = 0;
        IOException lastException = null;

        while (retryCount < maxRetries) {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                if (!"True".equals(rootNode.path("Response").asText())) {
                    return new ArrayList<>();
                }
                
                List<Movie> movies = new ArrayList<>();
                JsonNode resultsNode = rootNode.path("Search");
                
                if (resultsNode.isArray()) {
                    for (JsonNode movieNode : resultsNode) {
                        Movie movie = new Movie();
                        movie.setImdbId(movieNode.path("imdbID").asText());
                        movie.setTitle(movieNode.path("Title").asText());
                        movie.setYear(movieNode.path("Year").asText());
                        movie.setPosterUrl(movieNode.path("Poster").asText());
                        movies.add(movie);
                    }
                }
                
                return movies;
            } catch (IOException e) {
                lastException = e;
                retryCount++;
                if (retryCount < maxRetries) {
                    System.out.println("Retry attempt " + retryCount + " for search after error: " + e.getMessage());
                    try {
                        // Wait before retrying (exponential backoff)
                        Thread.sleep(1000 * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", ie);
                    }
                }
            }
        }
        
        // If we get here, all retries failed
        throw new IOException("Failed search after " + maxRetries + " attempts", lastException);
    }

    /**
     * Get detailed information about a movie by its IMDb ID
     * 
     * @param imdbId the IMDb ID of the movie
     * @return a Movie object with detailed information
     * @throws IOException if an I/O error occurs
     */
    public Movie getMovieDetails(String imdbId) throws IOException {
        String url = String.format("%s?apikey=%s&i=%s&plot=full", BASE_URL, apiKey, imdbId);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Implement retry logic
        int maxRetries = 3;
        int retryCount = 0;
        IOException lastException = null;

        while (retryCount < maxRetries) {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                if (!"True".equals(rootNode.path("Response").asText())) {
                    throw new IOException("Error fetching movie details: " + rootNode.path("Error").asText());
                }
                
                // Print received plot for debugging
                String plot = rootNode.path("Plot").asText();
                System.out.println("Received plot from API: " + (plot != null ? plot.substring(0, Math.min(50, plot.length())) + "..." : "null"));
                
                Movie movie = objectMapper.treeToValue(rootNode, Movie.class);
                
                // Add additional check for empty plot
                if (movie.getPlot() == null || movie.getPlot().trim().isEmpty() || "N/A".equals(movie.getPlot())) {
                    System.out.println("Plot is empty or N/A, setting default plot message");
                    movie.setPlot("No plot information available for this movie from the API.");
                }
                
                return movie;
            } catch (IOException e) {
                lastException = e;
                retryCount++;
                if (retryCount < maxRetries) {
                    System.out.println("Retry attempt " + retryCount + " after error: " + e.getMessage());
                    try {
                        // Wait before retrying (exponential backoff)
                        Thread.sleep(1000 * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", ie);
                    }
                }
            }
        }
        
        // If we get here, all retries failed
        throw new IOException("Failed after " + maxRetries + " attempts", lastException);
    }

    /**
     * Get movies by genre
     * 
     * @param genre the genre to search for
     * @param page the page number of results
     * @return a list of movies in the specified genre
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getMoviesByGenre(String genre, int page) throws IOException {
        // OMDb doesn't directly support genre search, so we'll search for the genre
        // and then filter the results. This is not ideal but works for demo purposes.
        String encodedGenre = URLEncoder.encode(genre, StandardCharsets.UTF_8.toString());
        String url = String.format("%s?apikey=%s&s=%s&page=%d", BASE_URL, apiKey, encodedGenre, page);
        
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Implement retry logic
        int maxRetries = 3;
        int retryCount = 0;
        IOException lastException = null;

        while (retryCount < maxRetries) {
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }

                String responseBody = response.body().string();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                
                if (!"True".equals(rootNode.path("Response").asText())) {
                    return new ArrayList<>();
                }
                
                List<Movie> movies = new ArrayList<>();
                JsonNode resultsNode = rootNode.path("Search");
                
                if (resultsNode.isArray()) {
                    for (JsonNode movieNode : resultsNode) {
                        Movie movie = new Movie();
                        movie.setImdbId(movieNode.path("imdbID").asText());
                        movie.setTitle(movieNode.path("Title").asText());
                        movie.setYear(movieNode.path("Year").asText());
                        movie.setPosterUrl(movieNode.path("Poster").asText());
                        movies.add(movie);
                    }
                }
                
                return movies;
            } catch (IOException e) {
                lastException = e;
                retryCount++;
                if (retryCount < maxRetries) {
                    System.out.println("Retry attempt " + retryCount + " for genre search after error: " + e.getMessage());
                    try {
                        // Wait before retrying (exponential backoff)
                        Thread.sleep(1000 * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry", ie);
                    }
                }
            }
        }
        
        // If we get here, all retries failed
        throw new IOException("Failed genre search after " + maxRetries + " attempts", lastException);
    }

    /**
     * Get popular movies (as a demonstration, we'll search for highly-rated films)
     * 
     * @param page the page number of results
     * @return a list of popular movies
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getPopularMovies(int page) throws IOException {
        // In a real app, you might get this from a different API or curated list
        // Here we're just searching for a popular keyword
        return searchMovies("popular", page);
    }
    
    /**
     * Get trending movies (for demo purposes)
     * 
     * @param page the page number of results
     * @return a list of trending movies
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getTrendingMovies(int page) throws IOException {
        // Simulating "trending" with a search for recent movies
        return searchMovies("2023", page);
    }
}
