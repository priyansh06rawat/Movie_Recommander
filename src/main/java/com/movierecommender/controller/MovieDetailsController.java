package com.movierecommender.controller;

import com.movierecommender.model.Movie;
import com.movierecommender.service.OmdbApiService;
import com.movierecommender.service.RecommendationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the movie details view
 */
public class MovieDetailsController {

    @FXML
    private ImageView posterImageView;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label yearLabel;
    
    @FXML
    private Label genreLabel;
    
    @FXML
    private Label directorLabel;
    
    @FXML
    private Label actorsLabel;
    
    @FXML
    private Label runtimeLabel;
    
    @FXML
    private Label ratingLabel;
    
    @FXML
    private TextArea plotTextArea;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private Button likeButton;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private VBox similarMoviesContainer;
    
    @FXML
    private VBox detailsContainer;
    
    private Movie movie;
    private RecommendationService recommendationService;
    private final OmdbApiService omdbApiService = new OmdbApiService();
    
    /**
     * Initializes the controller with movie data
     * 
     * @param movie the movie to display details for
     * @param recommendationService the recommendation service for similar movies
     */
    public void initData(Movie movie, RecommendationService recommendationService) {
        this.movie = movie;
        this.recommendationService = recommendationService;
        
        // Show loading state
        loadingIndicator.setVisible(true);
        detailsContainer.setVisible(false);
        errorLabel.setVisible(false);
        
        // Load full movie details in background
        CompletableFuture.runAsync(() -> {
            try {
                Movie fullMovieDetails = omdbApiService.getMovieDetails(movie.getImdbId());
                List<Movie> similarMovies = recommendationService.getSimilarMovies(fullMovieDetails);
                
                Platform.runLater(() -> {
                    displayMovieDetails(fullMovieDetails);
                    displaySimilarMovies(similarMovies);
                    
                    // Hide loading indicator
                    loadingIndicator.setVisible(false);
                    detailsContainer.setVisible(true);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingIndicator.setVisible(false);
                    errorLabel.setText("Error loading movie details: " + e.getMessage());
                    errorLabel.setVisible(true);
                    e.printStackTrace();
                });
            }
        });
        
        // Set up close button
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
    }
    
    /**
     * Displays detailed movie information
     */
    private void displayMovieDetails(Movie fullMovie) {
        // Update movie reference with full details
        this.movie = fullMovie;
        
        // Set poster image
        if (fullMovie.getPosterUrl() != null && !fullMovie.getPosterUrl().equals("N/A")) {
            posterImageView.setImage(new Image(fullMovie.getPosterUrl()));
        } else {
            posterImageView.setImage(new Image(getClass().getResource("/placeholder.png").toExternalForm()));
        }
        
        // Set text fields
        titleLabel.setText(fullMovie.getTitle());
        yearLabel.setText(fullMovie.getYear());
        genreLabel.setText(fullMovie.getGenre() != null ? fullMovie.getGenre() : "N/A");
        directorLabel.setText(fullMovie.getDirector() != null ? fullMovie.getDirector() : "N/A");
        actorsLabel.setText(fullMovie.getActors() != null ? fullMovie.getActors() : "N/A");
        runtimeLabel.setText(fullMovie.getRuntime() != null ? fullMovie.getRuntime() : "N/A");
        ratingLabel.setText(fullMovie.getImdbRating() != null ? fullMovie.getImdbRating() + "/10" : "N/A");
        plotTextArea.setText(fullMovie.getPlot() != null ? fullMovie.getPlot() : "No plot available.");
        
        // Configure like button
        likeButton.setOnAction(event -> likeMovie());
    }
    
    /**
     * Displays similar movies section
     */
    private void displaySimilarMovies(List<Movie> similarMovies) {
        similarMoviesContainer.getChildren().clear();
        
        if (similarMovies.isEmpty()) {
            Label noSimilarLabel = new Label("No similar movies found.");
            similarMoviesContainer.getChildren().add(noSimilarLabel);
            return;
        }
        
        Label titleLabel = new Label("You might also like:");
        titleLabel.getStyleClass().add("section-title");
        similarMoviesContainer.getChildren().add(titleLabel);
        
        // Create scrollable row of similar movie posters
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("movie-scroll-pane");
        scrollPane.setPrefHeight(180);
        scrollPane.setFitToHeight(true);
        
        HBox movieRow = new HBox();
        movieRow.setSpacing(15);
        movieRow.setPrefHeight(150);
        
        // Add similar movie posters to the row
        for (Movie similarMovie : similarMovies) {
            VBox movieBox = new VBox();
            movieBox.setSpacing(5);
            movieBox.setPrefWidth(100);
            movieBox.setAlignment(javafx.geometry.Pos.CENTER);
            
            ImageView posterView = new ImageView();
            if (similarMovie.getPosterUrl() != null && !similarMovie.getPosterUrl().equals("N/A")) {
                posterView.setImage(new Image(similarMovie.getPosterUrl()));
            } else {
                posterView.setImage(new Image(getClass().getResource("/placeholder.png").toExternalForm()));
            }
            posterView.setFitWidth(100);
            posterView.setFitHeight(150);
            posterView.setPreserveRatio(true);
            
            Label movieTitle = new Label(similarMovie.getTitle());
            movieTitle.getStyleClass().add("similar-movie-title");
            movieTitle.setWrapText(true);
            movieTitle.setPrefWidth(100);
            
            movieBox.getChildren().addAll(posterView, movieTitle);
            
            // Add click handler to load this movie
            movieBox.setOnMouseClicked(event -> {
                try {
                    Movie fullMovieDetails = omdbApiService.getMovieDetails(similarMovie.getImdbId());
                    List<Movie> newSimilarMovies = recommendationService.getSimilarMovies(fullMovieDetails);
                    
                    displayMovieDetails(fullMovieDetails);
                    displaySimilarMovies(newSimilarMovies);
                } catch (IOException e) {
                    errorLabel.setText("Error loading movie details: " + e.getMessage());
                    errorLabel.setVisible(true);
                    e.printStackTrace();
                }
            });
            
            movieRow.getChildren().add(movieBox);
        }
        
        scrollPane.setContent(movieRow);
        similarMoviesContainer.getChildren().add(scrollPane);
    }
    
    /**
     * Handles clicking the like button
     */
    private void likeMovie() {
        try {
            recommendationService.addPreferenceFromMovie(movie);
            
            // Update button appearance
            likeButton.setText("Liked ♥");
            likeButton.setDisable(true);
            
            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Preferences Updated");
            alert.setHeaderText(null);
            alert.setContentText("Your preferences have been updated based on this movie!");
            alert.showAndWait();
        } catch (Exception e) {
            errorLabel.setText("Error updating preferences: " + e.getMessage());
            errorLabel.setVisible(true);
            e.printStackTrace();
        }
    }
}
