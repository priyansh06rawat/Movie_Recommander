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
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    private Button trailerButton;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private VBox similarMoviesContainer;
    
    @FXML
    private ScrollPane detailsContainer;
    
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
        
        // Check if UI components are properly initialized before using them
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
        } else {
            System.err.println("Warning: loadingIndicator is null");
        }
        
        if (detailsContainer != null) {
            detailsContainer.setVisible(false);
        } else {
            System.err.println("Warning: detailsContainer is null");
        }
        
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        } else {
            System.err.println("Warning: errorLabel is null");
        }
        
        // Load full movie details in background
        CompletableFuture.runAsync(() -> {
            try {
                Movie fullMovieDetails = omdbApiService.getMovieDetails(movie.getImdbId());
                List<Movie> similarMovies = recommendationService.getSimilarMovies(fullMovieDetails);
                
                Platform.runLater(() -> {
                    displayMovieDetails(fullMovieDetails);
                    displaySimilarMovies(similarMovies);
                    
                    // Hide loading indicator
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    if (detailsContainer != null) {
                        detailsContainer.setVisible(true);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    if (loadingIndicator != null) {
                        loadingIndicator.setVisible(false);
                    }
                    
                    if (errorLabel != null) {
                        errorLabel.setText("Error loading movie details: " + e.getMessage());
                        errorLabel.setVisible(true);
                    } else {
                        System.err.println("Error loading movie details: " + e.getMessage());
                    }
                    
                    e.printStackTrace();
                });
            }
        });
        
        // Set up close button
        closeButton.setOnAction(event -> handleClose());
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
        
        // Check if movie is already a favorite and update button
        if (recommendationService.isMovieFavorite(fullMovie.getImdbId())) {
            likeButton.setText("Liked ♥");
            likeButton.getStyleClass().add("netflix-button-liked");
        } else {
            likeButton.setText("Add to Favorites");
            likeButton.getStyleClass().remove("netflix-button-liked");
        }
        
        // Configure trailer button
        trailerButton.setOnAction(event -> watchTrailer());
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
                    String errorMsg = "Error loading movie details: " + e.getMessage();
                    if (errorLabel != null) {
                        errorLabel.setText(errorMsg);
                        errorLabel.setVisible(true);
                    } else {
                        System.err.println(errorMsg);
                    }
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
            // Check if the movie is already liked
            if (recommendationService.isMovieFavorite(movie.getImdbId())) {
                // Remove from favorites
                recommendationService.removeMovieFromFavorites(movie.getImdbId());
                
                // Update button appearance
                likeButton.setText("Add to Favorites");
                likeButton.getStyleClass().remove("netflix-button-liked");
                
                // Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Favorites Updated");
                alert.setHeaderText(null);
                alert.setContentText("Movie removed from your favorites!");
                alert.showAndWait();
            } else {
                // Add to favorites
                recommendationService.addPreferenceFromMovie(movie);
                
                // Update button appearance
                likeButton.setText("Liked ♥");
                likeButton.getStyleClass().add("netflix-button-liked");
                
                // Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Favorites Updated");
                alert.setHeaderText(null);
                alert.setContentText("Movie added to your favorites!");
                alert.showAndWait();
            }
        } catch (Exception e) {
            String errorMsg = "Error updating favorites: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            
            if (errorLabel != null) {
                errorLabel.setText(errorMsg);
                errorLabel.setVisible(true);
            }
        }
    }
    
    /**
     * Handles closing the window
     */
    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Opens a web browser to search for the movie trailer on YouTube
     */
    private void watchTrailer() {
        try {
            if (movie == null) {
                if (errorLabel != null) {
                    errorLabel.setText("Error opening trailer: Movie data is not available");
                    errorLabel.setVisible(true);
                } else {
                    System.err.println("Error opening trailer: Movie data is not available");
                }
                return;
            }
            
            // Create a YouTube search URL with the movie title and "trailer"
            String title = movie.getTitle() != null ? movie.getTitle() : "Unknown movie";
            String year = movie.getYear() != null ? movie.getYear() : "";
            String searchQuery = title + " " + year + " trailer";
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
            String youtubeUrl = "https://www.youtube.com/results?search_query=" + encodedQuery;
            
            System.out.println("Opening trailer URL: " + youtubeUrl);
            
            // Open the URL in the default browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(youtubeUrl));
            } else {
                throw new IOException("Desktop browsing not supported on this platform");
            }
        } catch (Exception e) {
            String errorMsg = "Error opening trailer: " + e.getMessage();
            System.err.println(errorMsg);
            e.printStackTrace();
            
            if (errorLabel != null) {
                errorLabel.setText(errorMsg);
                errorLabel.setVisible(true);
            }
        }
    }
}
