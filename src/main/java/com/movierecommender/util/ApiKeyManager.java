package com.movierecommender.util;

/**
 * Utility class for managing API keys
 */
public class ApiKeyManager {
    // Default key from the requirements (should be replaced with env var in production)
    private static final String DEFAULT_API_KEY = "24118512";
    
    /**
     * Gets the OMDb API key from environment variables or uses the default key
     * 
     * @return the API key
     */
    public static String getApiKey() {
        String apiKey = System.getenv("OMDB_API_KEY");
        
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return DEFAULT_API_KEY;
        }
        
        return apiKey;
    }
    
    /**
     * Checks if an API key is available
     * 
     * @return true if an API key is available, false otherwise
     */
    public static boolean isApiKeyAvailable() {
        String apiKey = getApiKey();
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
