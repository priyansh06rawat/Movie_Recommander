package com.movierecommender;

import com.movierecommender.util.ApiKeyManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that starts the Movie Recommendation Application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Check if API key is available
        if (!ApiKeyManager.isApiKeyAvailable()) {
            System.err.println("OMDb API key not found. Please set the OMDB_API_KEY environment variable.");
            System.exit(1);
        }

        // Load the main UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();
        
        // Set up the primary stage
        primaryStage.setTitle("Netflix-Style Movie Recommender");
        Scene scene = new Scene(root, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("/css/netflix-style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * Main method to launch the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
