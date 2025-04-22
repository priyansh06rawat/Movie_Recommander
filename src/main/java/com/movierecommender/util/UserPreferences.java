package com.movierecommender.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Utility class for managing user preferences
 */
public class UserPreferences {
    private static final String PREFERENCES_FILE = System.getProperty("user.home") + "/.movie_recommender_prefs.txt";
    private Set<String> preferredGenres;
    
    /**
     * Creates a new UserPreferences instance and loads existing preferences
     */
    public UserPreferences() {
        preferredGenres = new HashSet<>();
        loadPreferences();
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
}
