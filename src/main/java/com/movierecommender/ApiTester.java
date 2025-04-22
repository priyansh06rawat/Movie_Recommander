package com.movierecommender;

import com.movierecommender.model.Movie;
import com.movierecommender.service.OmdbApiService;
import com.movierecommender.util.ApiKeyManager;

import java.io.IOException;
import java.util.List;

/**
 * A simple tester class for the OMDB API
 */
public class ApiTester {
    public static void main(String[] args) {
        System.out.println("Starting API Tester...");
        System.out.println("Using API Key type: " + (System.getenv("OMDB_API_KEY") != null ? "Custom Key" : "Default Key"));
        
        try {
            // Create API service
            OmdbApiService apiService = new OmdbApiService();
            
            // Test search
            System.out.println("\nSearching for 'Avengers'...");
            List<Movie> searchResults = apiService.searchMovies("Avengers", 1);
            System.out.println("Found " + searchResults.size() + " results:");
            for (Movie movie : searchResults) {
                System.out.println(" - " + movie.getTitle() + " (" + movie.getYear() + ")");
            }
            
            // Test movie details
            if (!searchResults.isEmpty()) {
                String imdbId = searchResults.get(0).getImdbId();
                System.out.println("\nGetting details for movie with ID: " + imdbId);
                try {
                    Movie movieDetails = apiService.getMovieDetails(imdbId);
                    System.out.println("Movie details:");
                    System.out.println(" - Title: " + movieDetails.getTitle());
                    System.out.println(" - Year: " + movieDetails.getYear());
                    System.out.println(" - Director: " + movieDetails.getDirector());
                    System.out.println(" - Plot: " + movieDetails.getPlot());
                } catch (Exception e) {
                    System.err.println("Error getting movie details: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            // Test genre search
            System.out.println("\nSearching for 'Action' movies...");
            List<Movie> actionMovies = apiService.getMoviesByGenre("Action", 1);
            System.out.println("Found " + actionMovies.size() + " results:");
            for (Movie movie : actionMovies) {
                System.out.println(" - " + movie.getTitle() + " (" + movie.getYear() + ")");
            }
            
        } catch (IOException e) {
            System.err.println("Error in API Tester: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\nAPI Test completed.");
    }
}