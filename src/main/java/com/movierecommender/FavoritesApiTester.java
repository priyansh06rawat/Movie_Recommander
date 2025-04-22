package com.movierecommender;

import com.movierecommender.model.Movie;
import com.movierecommender.service.OmdbApiService;
import com.movierecommender.service.RecommendationService;
import com.movierecommender.util.UserPreferences;

import java.io.IOException;
import java.util.List;

/**
 * Test class for the Favorites functionality
 */
public class FavoritesApiTester {

    public static void main(String[] args) {
        System.out.println("Starting Favorites API Tester...");
        
        // Initialize services
        OmdbApiService omdbApiService = new OmdbApiService();
        RecommendationService recommendationService = new RecommendationService();
        UserPreferences userPreferences = new UserPreferences();
        
        try {
            // Clear existing favorites to start from a clean slate
            System.out.println("Clearing existing favorites...");
            userPreferences.clearFavorites();
            
            // Search for some movies
            System.out.println("\nSearching for movies...");
            List<Movie> avengersMovies = omdbApiService.searchMovies("Avengers", 1);
            List<Movie> lordOfTheRingsMovies = omdbApiService.searchMovies("Lord of the Rings", 1);
            
            if (avengersMovies.isEmpty() || lordOfTheRingsMovies.isEmpty()) {
                System.out.println("Error: Could not find test movies");
                return;
            }
            
            // Add movies to favorites
            System.out.println("\nAdding movies to favorites...");
            Movie avengers = omdbApiService.getMovieDetails(avengersMovies.get(0).getImdbId());
            Movie lotr = omdbApiService.getMovieDetails(lordOfTheRingsMovies.get(0).getImdbId());
            
            System.out.println("Adding: " + avengers.getTitle() + " (" + avengers.getYear() + ")");
            recommendationService.addPreferenceFromMovie(avengers);
            
            System.out.println("Adding: " + lotr.getTitle() + " (" + lotr.getYear() + ")");
            recommendationService.addPreferenceFromMovie(lotr);
            
            // Check if movies are in favorites
            System.out.println("\nChecking if movies are in favorites...");
            boolean isAvengersInFavorites = recommendationService.isMovieFavorite(avengers.getImdbId());
            boolean isLotrInFavorites = recommendationService.isMovieFavorite(lotr.getImdbId());
            
            System.out.println(avengers.getTitle() + " in favorites: " + isAvengersInFavorites);
            System.out.println(lotr.getTitle() + " in favorites: " + isLotrInFavorites);
            
            // Display all favorites
            System.out.println("\nFetching all favorites...");
            List<Movie> favoriteMovies = recommendationService.getFavoriteMovies();
            
            System.out.println("All favorites (" + favoriteMovies.size() + "):");
            for (Movie movie : favoriteMovies) {
                System.out.println(" - " + movie.getTitle() + " (" + movie.getYear() + ")");
            }
            
            // Remove a movie from favorites
            System.out.println("\nRemoving a movie from favorites...");
            recommendationService.removeMovieFromFavorites(avengers.getImdbId());
            
            // Check favorites again
            System.out.println("\nFetching favorites after removal...");
            favoriteMovies = recommendationService.getFavoriteMovies();
            
            System.out.println("Remaining favorites (" + favoriteMovies.size() + "):");
            for (Movie movie : favoriteMovies) {
                System.out.println(" - " + movie.getTitle() + " (" + movie.getYear() + ")");
            }
            
            // Check if removed movie is no longer in favorites
            isAvengersInFavorites = recommendationService.isMovieFavorite(avengers.getImdbId());
            System.out.println("\n" + avengers.getTitle() + " in favorites after removal: " + isAvengersInFavorites);
            
            System.out.println("\nFavorites functionality test completed.");
            
        } catch (IOException e) {
            System.err.println("Error during favorites testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}