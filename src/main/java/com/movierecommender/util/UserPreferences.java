package com.movierecommender.util;

import com.movierecommender.model.Movie;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class for managing user preferences
 */
public class UserPreferences {
    private static final String PREFERENCES_FILE = System.getProperty("user.home") + "/.movie_recommender_prefs.txt";
    private static final String FAVORITES_FILE = System.getProperty("user.home") + "/.movie_recommender_favorites.txt";
    private Set<String> preferredGenres;
    private Map<String, String> favoriteMovies; // Map of imdbId -> title
    
    /**
     * Creates a new UserPreferences instance and loads existing preferences
     */
    public UserPreferences() {
        preferredGenres = new HashSet<>();
        favoriteMovies = new HashMap<>();
        loadPreferences();
        loadFavorites();
    }
    
    /**
     * Loads user preferences from the preferences file
     */
    private void loadPreferences() {
        File prefsFile = new File(PREFERENCES_FILE);
        
        if (!prefsFile.exists()) {
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(PREFERENCES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    preferredGenres.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading preferences: " + e.getMessage());
        }
    }
    
    /**
     * Saves user preferences to the preferences file
     * 
     * @throws IOException if an I/O error occurs
     */
    public void savePreferences() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PREFERENCES_FILE))) {
            for (String genre : preferredGenres) {
                writer.write(genre);
                writer.newLine();
            }
        }
    }
    
    /**
     * Adds a genre to the list of preferred genres
     * 
     * @param genre the genre to add
     */
    public void addPreferredGenre(String genre) {
        if (genre != null && !genre.trim().isEmpty()) {
            preferredGenres.add(genre.trim());
        }
    }
    
    /**
     * Removes a genre from the list of preferred genres
     * 
     * @param genre the genre to remove
     */
    public void removePreferredGenre(String genre) {
        preferredGenres.remove(genre);
    }
    
    /**
     * Gets the list of preferred genres
     * 
     * @return the list of preferred genres
     */
    public List<String> getPreferredGenres() {
        return new ArrayList<>(preferredGenres);
    }
    
    /**
     * Clears all preferred genres
     * 
     * @throws IOException if an I/O error occurs
     */
    public void clearPreferences() throws IOException {
        preferredGenres.clear();
        savePreferences();
    }
    
    /**
     * Loads favorite movies from the favorites file
     */
    private void loadFavorites() {
        File favoritesFile = new File(FAVORITES_FILE);
        
        if (!favoritesFile.exists()) {
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FAVORITES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        favoriteMovies.put(parts[0].trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading favorites: " + e.getMessage());
        }
    }
    
    /**
     * Saves favorite movies to the favorites file
     * 
     * @throws IOException if an I/O error occurs
     */
    public void saveFavorites() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FAVORITES_FILE))) {
            for (Map.Entry<String, String> entry : favoriteMovies.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        }
    }
    
    /**
     * Adds a movie to favorites
     * 
     * @param movie the movie to add
     * @throws IOException if an I/O error occurs
     */
    public void addFavoriteMovie(Movie movie) throws IOException {
        if (movie != null && movie.getImdbId() != null && !movie.getImdbId().isEmpty()) {
            favoriteMovies.put(movie.getImdbId(), movie.getTitle());
            saveFavorites();
        }
    }
    
    /**
     * Removes a movie from favorites
     * 
     * @param imdbId the IMDb ID of the movie to remove
     * @throws IOException if an I/O error occurs
     */
    public void removeFavoriteMovie(String imdbId) throws IOException {
        if (favoriteMovies.containsKey(imdbId)) {
            favoriteMovies.remove(imdbId);
            saveFavorites();
        }
    }
    
    /**
     * Checks if a movie is in favorites
     * 
     * @param imdbId the IMDb ID of the movie to check
     * @return true if the movie is in favorites, false otherwise
     */
    public boolean isFavoriteMovie(String imdbId) {
        return favoriteMovies.containsKey(imdbId);
    }
    
    /**
     * Gets the list of favorite movie IDs
     * 
     * @return the list of favorite movie IMDb IDs
     */
    public List<String> getFavoriteMovieIds() {
        return new ArrayList<>(favoriteMovies.keySet());
    }
    
    /**
     * Gets a map of favorite movies (imdbId -> title)
     * 
     * @return the map of favorite movies
     */
    public Map<String, String> getFavoriteMovies() {
        return new HashMap<>(favoriteMovies);
    }
    
    /**
     * Clears all favorite movies
     * 
     * @throws IOException if an I/O error occurs
     */
    public void clearFavorites() throws IOException {
        favoriteMovies.clear();
        saveFavorites();
    }
}
