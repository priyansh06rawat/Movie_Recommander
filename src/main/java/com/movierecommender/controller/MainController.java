package com.movierecommender.controller;

import com.movierecommender.model.Movie;
import com.movierecommender.service.OmdbApiService;
import com.movierecommender.service.RecommendationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for the main UI screen
 */
public class MainController implements Initializable {

    @FXML
    private ScrollPane mainScrollPane;
    
    @FXML
    private VBox contentContainer;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Label errorMessageLabel;
    
    private final OmdbApiService omdbApiService = new OmdbApiService();
    private final RecommendationService recommendationService = new RecommendationService();
    
    // List of genres/categories to display
    private final List<String> categories = Arrays.asList(
            "Action", "Comedy", "Drama", "Thriller", 
            "Sci-Fi", "Horror", "Romance", "Adventure"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hide error message initially
        errorMessageLabel.setVisible(false);
        
        // Configure the main scroll pane
        mainScrollPane.setFitToWidth(true);
        
        // Configure search functionality
        searchButton.setOnAction(event -> searchMovies());
        searchField.setOnAction(event -> searchMovies());
        
        // Load initial content
        loadInitialContent();
    }
    
    /**
     * Loads the initial content for the main screen
     */
    private void loadInitialContent() {
        contentContainer.getChildren().clear();
        
        // Show loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        HBox loadingBox = new HBox(progressIndicator);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        contentContainer.getChildren().add(loadingBox);
        
        // Fetch data in background
        CompletableFuture.runAsync(() -> {
            try {
                // Get featured/hero movie
                List<Movie> trendingMovies = recommendationService.getTrendingMovies();
                
                // Get recommendations
                List<Movie> recommendedMovies = recommendationService.getRecommendedMovies();
                
                // Get movies by categories
                Map<String, List<Movie>> categoryMovies = new HashMap<>();
                for (String category : categories) {
                    categoryMovies.put(category, recommendationService.getMoviesByCategory(category));
                }
                
                // Update UI on JavaFX application thread
                Platform.runLater(() -> {
                    try {
                        updateUIWithMovies(trendingMovies, recommendedMovies, categoryMovies);
                    } catch (Exception e) {
                        showError("Error building UI: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error loading content: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }
    
    /**
     * Updates the UI with fetched movies
     */
    private void updateUIWithMovies(List<Movie> trendingMovies, List<Movie> recommendedMovies, 
                                   Map<String, List<Movie>> categoryMovies) {
        contentContainer.getChildren().clear();
        
        // Add hero/featured section with first trending movie if available
        if (!trendingMovies.isEmpty()) {
            Movie heroMovie = trendingMovies.get(0);
            VBox heroSection = createHeroSection(heroMovie);
            contentContainer.getChildren().add(heroSection);
        }
        
        // Add favorites section
        try {
            List<Movie> favoriteMovies = recommendationService.getFavoriteMovies();
            if (!favoriteMovies.isEmpty()) {
                VBox favoritesSection = createMovieRow("My Favorites ♥", favoriteMovies);
                // Add a special style class for favorites row
                favoritesSection.getStyleClass().add("favorites-section");
                contentContainer.getChildren().add(favoritesSection);
            }
        } catch (IOException e) {
            System.err.println("Error loading favorites: " + e.getMessage());
        }
        
        // Add recommended movies row
        if (!recommendedMovies.isEmpty()) {
            VBox recommendedSection = createMovieRow("Recommended for You", recommendedMovies);
            contentContainer.getChildren().add(recommendedSection);
        }
        
        // Add trending movies row
        if (trendingMovies.size() > 1) {
            // Skip the first one as it's used in the hero section
            List<Movie> remainingTrending = trendingMovies.subList(1, trendingMovies.size());
            VBox trendingSection = createMovieRow("Trending Now", remainingTrending);
            contentContainer.getChildren().add(trendingSection);
        }
        
        // Add category rows
        for (String category : categories) {
            List<Movie> movies = categoryMovies.get(category);
            if (movies != null && !movies.isEmpty()) {
                VBox categorySection = createMovieRow(category, movies);
                contentContainer.getChildren().add(categorySection);
            }
        }
    }
    
    /**
     * Creates a hero section with a featured movie
     */
    private VBox createHeroSection(Movie movie) {
        VBox heroSection = new VBox();
        heroSection.setSpacing(10);
        heroSection.setPadding(new Insets(0, 0, 20, 0));
        
        // Create backdrop for hero movie
        StackPane heroBackdrop = new StackPane();
        heroBackdrop.setPrefHeight(400);
        heroBackdrop.getStyleClass().add("hero-backdrop");
        
        // Movie poster in hero section
        ImageView posterView = new ImageView();
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().equals("N/A")) {
            posterView.setImage(new Image(movie.getPosterUrl()));
        } else {
            posterView.setImage(new Image(getClass().getResource("/placeholder.png").toExternalForm()));
        }
        posterView.setFitHeight(300);
        posterView.setPreserveRatio(true);
        
        // Movie info in hero section
        VBox movieInfo = new VBox();
        movieInfo.setSpacing(10);
        movieInfo.setPadding(new Insets(15));
        movieInfo.getStyleClass().add("hero-info");
        
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("hero-title");
        
        Label yearLabel = new Label(movie.getYear());
        yearLabel.getStyleClass().add("hero-year");
        
        Button detailsButton = new Button("More Info");
        detailsButton.getStyleClass().add("netflix-button");
        detailsButton.setOnAction(event -> showMovieDetails(movie));
        
        movieInfo.getChildren().addAll(titleLabel, yearLabel, detailsButton);
        
        // Add poster and info to backdrop
        heroBackdrop.getChildren().addAll(posterView, movieInfo);
        StackPane.setAlignment(movieInfo, javafx.geometry.Pos.BOTTOM_LEFT);
        
        heroSection.getChildren().add(heroBackdrop);
        
        return heroSection;
    }
    
    /**
     * Creates a row of movies with a title
     */
    private VBox createMovieRow(String title, List<Movie> movies) {
        VBox rowSection = new VBox();
        rowSection.setSpacing(10);
        rowSection.setPadding(new Insets(10, 0, 30, 0));
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("row-title");
        
        // Create scrollable row of movie posters
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("movie-scroll-pane");
        scrollPane.setPrefHeight(200);
        scrollPane.setFitToHeight(true);
        
        HBox movieRow = new HBox();
        movieRow.setSpacing(15);
        movieRow.setPadding(new Insets(10, 20, 10, 20));
        
        // Add movie posters to the row
        for (Movie movie : movies) {
            StackPane movieCard = createMovieCard(movie);
            movieRow.getChildren().add(movieCard);
        }
        
        scrollPane.setContent(movieRow);
        rowSection.getChildren().addAll(titleLabel, scrollPane);
        
        return rowSection;
    }
    
    /**
     * Creates a movie card with poster and hover effect
     */
    private StackPane createMovieCard(Movie movie) {
        StackPane movieCard = new StackPane();
        movieCard.getStyleClass().add("movie-card");
        movieCard.setPrefWidth(150);
        movieCard.setPrefHeight(225);
        
        // Movie poster
        ImageView posterView = new ImageView();
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().equals("N/A")) {
            try {
                posterView.setImage(new Image(movie.getPosterUrl()));
            } catch (Exception e) {
                // If the image URL is invalid, create a placeholder image programmatically
                System.out.println("Error loading image: " + e.getMessage());
                // Create a default placeholder colored rectangle
                posterView.setFitWidth(150);
                posterView.setFitHeight(225);
                posterView.setStyle("-fx-background-color: #333333;");
            }
        } else {
            // Create a default placeholder colored rectangle instead of loading a resource
            posterView.setFitWidth(150);
            posterView.setFitHeight(225);
            posterView.setStyle("-fx-background-color: #333333;");
        }
        posterView.setFitWidth(150);
        posterView.setFitHeight(225);
        posterView.setPreserveRatio(true);
        
        // Overlay for hover effect
        VBox overlay = new VBox();
        overlay.getStyleClass().add("movie-card-overlay");
        overlay.setAlignment(javafx.geometry.Pos.CENTER);
        overlay.setVisible(false);
        
        Label titleLabel = new Label(movie.getTitle());
        titleLabel.getStyleClass().add("movie-card-title");
        titleLabel.setWrapText(true);
        
        Button infoButton = new Button("Info");
        infoButton.getStyleClass().add("movie-card-button");
        infoButton.setOnAction(event -> showMovieDetails(movie));
        
        overlay.getChildren().addAll(titleLabel, infoButton);
        
        movieCard.getChildren().addAll(posterView, overlay);
        
        // Add hover effect
        movieCard.setOnMouseEntered(event -> overlay.setVisible(true));
        movieCard.setOnMouseExited(event -> overlay.setVisible(false));
        
        // Add click handler
        movieCard.setOnMouseClicked(event -> showMovieDetails(movie));
        
        return movieCard;
    }
    
    /**
     * Handles movie search
     */
    private void searchMovies() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            showError("Please enter a search term");
            return;
        }
        
        // Show loading indicator
        contentContainer.getChildren().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        HBox loadingBox = new HBox(progressIndicator);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        loadingBox.setPadding(new Insets(20));
        contentContainer.getChildren().add(loadingBox);
        
        // Execute search in background
        CompletableFuture.runAsync(() -> {
            try {
                List<Movie> searchResults = omdbApiService.searchMovies(query, 1);
                
                Platform.runLater(() -> {
                    if (searchResults.isEmpty()) {
                        showSearchResultsEmpty(query);
                    } else {
                        showSearchResults(query, searchResults);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Search error: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }
    
    /**
     * Displays search results
     */
    private void showSearchResults(String query, List<Movie> movies) {
        contentContainer.getChildren().clear();
        
        // Add header with search info
        Label searchInfoLabel = new Label("Search results for: \"" + query + "\"");
        searchInfoLabel.getStyleClass().add("search-title");
        searchInfoLabel.setPadding(new Insets(20, 0, 20, 10));
        
        // Add button to go back to main content
        Button backButton = new Button("Back to Browse");
        backButton.getStyleClass().add("netflix-button");
        backButton.setOnAction(event -> loadInitialContent());
        
        HBox headerBox = new HBox(searchInfoLabel, backButton);
        headerBox.setSpacing(20);
        headerBox.setPadding(new Insets(10));
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        contentContainer.getChildren().add(headerBox);
        
        // Create grid of search results
        GridPane resultsGrid = new GridPane();
        resultsGrid.setHgap(15);
        resultsGrid.setVgap(20);
        resultsGrid.setPadding(new Insets(20));
        
        int column = 0;
        int row = 0;
        int maxColumns = 4;
        
        for (Movie movie : movies) {
            StackPane movieCard = createMovieCard(movie);
            
            resultsGrid.add(movieCard, column, row);
            
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
        
        contentContainer.getChildren().add(resultsGrid);
    }
    
    /**
     * Shows a message when search returns no results
     */
    private void showSearchResultsEmpty(String query) {
        contentContainer.getChildren().clear();
        
        VBox emptyBox = new VBox();
        emptyBox.setAlignment(javafx.geometry.Pos.CENTER);
        emptyBox.setSpacing(20);
        emptyBox.setPadding(new Insets(50));
        
        Label noResultsLabel = new Label("No results found for: \"" + query + "\"");
        noResultsLabel.getStyleClass().add("empty-results-label");
        
        Button backButton = new Button("Back to Browse");
        backButton.getStyleClass().add("netflix-button");
        backButton.setOnAction(event -> loadInitialContent());
        
        emptyBox.getChildren().addAll(noResultsLabel, backButton);
        
        contentContainer.getChildren().add(emptyBox);
    }
    
    /**
     * Opens movie details in a new window
     */
    private void showMovieDetails(Movie movie) {
        if (movie == null) {
            showError("Error: Cannot show details for null movie");
            return;
        }
        
        try {
            System.out.println("Opening details for movie: " + movie.getTitle());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/movieDetails.fxml"));
            Parent root = loader.load();
            
            MovieDetailsController detailsController = loader.getController();
            detailsController.initData(movie, recommendationService);
            
            Stage detailsStage = new Stage();
            detailsStage.setTitle(movie.getTitle() != null ? movie.getTitle() : "Movie Details");
            Scene scene = new Scene(root, 800, 600);
            
            URL cssUrl = getClass().getResource("/css/netflix-style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("Warning: Could not load CSS file");
            }
            
            detailsStage.setScene(scene);
            detailsStage.initModality(Modality.APPLICATION_MODAL);
            detailsStage.show();
        } catch (Exception e) {
            showError("Error showing movie details: " + e.getMessage());
            System.err.println("Error showing movie details: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Displays an error message
     */
    private void showError(String message) {
        errorMessageLabel.setText(message);
        errorMessageLabel.setVisible(true);
        
        // Hide error after 5 seconds
        new Timer().schedule(
            new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> errorMessageLabel.setVisible(false));
                }
            }, 
            5000
        );
    }
}
