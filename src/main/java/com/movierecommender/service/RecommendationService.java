package com.movierecommender.service;

import com.movierecommender.model.Movie;
import com.movierecommender.util.UserPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for providing movie recommendations based on user preferences
 */
public class RecommendationService {
    private final OmdbApiService omdbApiService;
    private final UserPreferences userPreferences;
    
    public RecommendationService() {
        this.omdbApiService = new OmdbApiService();
        this.userPreferences = new UserPreferences();
    }
    
    /**
     * Get movie recommendations based on user's preferred genres
     * 
     * @return a list of recommended movies
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getRecommendedMovies() throws IOException {
        List<String> preferredGenres = userPreferences.getPreferredGenres();
        List<Movie> recommendations = new ArrayList<>();
        Set<String> addedMovieIds = new HashSet<>();
        
        // If user has no preferences yet, return popular movies
        if (preferredGenres.isEmpty()) {
            return omdbApiService.getPopularMovies(1);
        }
        
        // Otherwise, get movies from each preferred genre
        for (String genre : preferredGenres) {
            List<Movie> genreMovies = omdbApiService.getMoviesByGenre(genre, 1);
            
            // Add movies that haven't been added yet
            for (Movie movie : genreMovies) {
                if (!addedMovieIds.contains(movie.getImdbId())) {
                    recommendations.add(movie);
                    addedMovieIds.add(movie.getImdbId());
                }
                
                // Limit the number of recommendations
                if (recommendations.size() >= 10) {
                    break;
                }
            }
            
            // If we have enough recommendations, stop
            if (recommendations.size() >= 10) {
                break;
            }
        }
        
        return recommendations;
    }
    
    /**
     * Get movie recommendations based on a specific movie
     * 
     * @param movie the movie to get similar recommendations for
     * @return a list of similar movies
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getSimilarMovies(Movie movie) throws IOException {
        // Extract the first genre from the movie's genres
        String firstGenre = movie.getGenre() != null ? 
                movie.getGenre().split(",")[0].trim() : "";
        
        if (firstGenre.isEmpty()) {
            return omdbApiService.getPopularMovies(1);
        }
        
        List<Movie> similarMovies = omdbApiService.getMoviesByGenre(firstGenre, 1);
        
        // Filter out the original movie
        return similarMovies.stream()
                .filter(m -> !m.getImdbId().equals(movie.getImdbId()))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * Add a genre to user's preferences based on a movie they liked
     * and add the movie to favorites
     * 
     * @param movie the movie the user liked
     * @throws IOException if an I/O error occurs
     */
    public void addPreferenceFromMovie(Movie movie) throws IOException {
        if (movie.getGenre() == null || movie.getGenre().isEmpty()) {
            // If the movie doesn't have genre info yet, fetch complete details
            Movie fullMovie = omdbApiService.getMovieDetails(movie.getImdbId());
            if (fullMovie.getGenre() != null && !fullMovie.getGenre().isEmpty()) {
                String[] genres = fullMovie.getGenre().split(",");
                for (String genre : genres) {
                    userPreferences.addPreferredGenre(genre.trim());
                }
            }
            
            // Add the movie to favorites
            userPreferences.addFavoriteMovie(fullMovie);
        } else {
            String[] genres = movie.getGenre().split(",");
            for (String genre : genres) {
                userPreferences.addPreferredGenre(genre.trim());
            }
            
            // Add the movie to favorites
            userPreferences.addFavoriteMovie(movie);
        }
        
        // Save preferences
        userPreferences.savePreferences();
    }
    
    /**
     * Check if a movie is in the user's favorites
     * 
     * @param imdbId the IMDb ID of the movie to check
     * @return true if the movie is in favorites, false otherwise
     */
    public boolean isMovieFavorite(String imdbId) {
        return userPreferences.isFavoriteMovie(imdbId);
    }
    
    /**
     * Remove a movie from favorites
     * 
     * @param imdbId the IMDb ID of the movie to remove
     * @throws IOException if an I/O error occurs
     */
    public void removeMovieFromFavorites(String imdbId) throws IOException {
        userPreferences.removeFavoriteMovie(imdbId);
    }
    
    /**
     * Get list of favorite movies
     * 
     * @return list of favorite movies (full details)
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getFavoriteMovies() throws IOException {
        List<String> favoriteIds = userPreferences.getFavoriteMovieIds();
        List<Movie> favorites = new ArrayList<>();
        
        for (String imdbId : favoriteIds) {
            try {
                Movie movie = omdbApiService.getMovieDetails(imdbId);
                favorites.add(movie);
            } catch (IOException e) {
                System.err.println("Error fetching details for favorite movie " + imdbId + ": " + e.getMessage());
                // Create a basic movie with just the ID and title from preferences
                String title = userPreferences.getFavoriteMovies().get(imdbId);
                if (title != null) {
                    Movie basicMovie = new Movie(imdbId, title, "", "");
                    favorites.add(basicMovie);
                }
            }
        }
        
        return favorites;
    }
    
    /**
     * Get trending movies of the week
     * 
     * @return a list of trending movies
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getTrendingMovies() throws IOException {
        return omdbApiService.getTrendingMovies(1);
    }
    
    /**
     * Get a selection of movies by category (genre)
     * 
     * @param category the category/genre to get movies for
     * @return a list of movies in the specified category
     * @throws IOException if an I/O error occurs
     */
    public List<Movie> getMoviesByCategory(String category) throws IOException {
        return omdbApiService.getMoviesByGenre(category, 1);
    }
}
