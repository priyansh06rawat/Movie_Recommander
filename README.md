# Netflix-Style Movie Recommender Application

This is a Netflix-style Java desktop application for movie recommendations using the OMDb API. The application provides a user-friendly interface for browsing and searching movies, with personalized recommendations based on your preferences.

## Features

- **Netflix-like UI**: Clean, modern interface similar to streaming platforms
- **Movie Recommendations**: Get personalized movie suggestions based on genres you like
- **Search Functionality**: Find movies by title or keywords
- **Detailed Information**: View comprehensive details about each movie
- **Similar Movies**: For each movie, see related recommendations
- **User Preferences**: System learns your preferences as you like movies
- **Favorites Section**: Save and view your favorite movies in a dedicated section
- **Trailer Integration**: Watch trailers for movies with one click (opens in browser)

## How it Looks

When the application is running in Fedora with VS Code, you would see:

### Main Screen
The main screen features multiple rows of movie posters organized by category, similar to Netflix:
- Hero section with featured movie
- "Recommended for You" section based on your preferences
- "Trending Now" section with popular movies
- Genre-based categories (Action, Comedy, Drama, etc.)

![Main Screen](https://i.imgur.com/KlmVJsQ.jpg)

### Movie Details
Clicking on any movie poster opens a detailed view with:
- Movie poster
- Title, year, rating
- Director and cast information
- Plot summary
- Similar movie recommendations

![Movie Details](https://i.imgur.com/xZYoXpZ.jpg)

### Search Results
The search functionality allows you to find specific movies:

![Search Results](https://i.imgur.com/cq3H8Gd.jpg)

## Technical Information

- Built with **Java** and **JavaFX** for the UI
- Uses **FXML** for UI layout definition
- Connects to **OMDb API** for movie data
- Implements **MVC pattern** (Model-View-Controller)
- Asynchronous data loading for smooth user experience

## Running in Fedora

To run the application in Fedora with VS Code:

1. Install prerequisites:
   ```
   sudo dnf install java-11-openjdk java-11-openjdk-devel maven
   ```

2. Install VS Code and Java extensions:
   ```
   sudo rpm --import https://packages.microsoft.com/keys/microsoft.asc
   sudo sh -c 'echo -e "[code]\nname=Visual Studio Code\nbaseurl=https://packages.microsoft.com/yumrepos/vscode\nenabled=1\ngpgcheck=1\ngpgkey=https://packages.microsoft.com/keys/microsoft.asc" > /etc/yum.repos.d/vscode.repo'
   sudo dnf install code
   ```
   
3. Install JavaFX:
   ```
   sudo dnf install java-openjfx
   ```

4. Open the project in VS Code and run:
   ```
   mvn clean javafx:run
   ```

## API Information

This application uses the OMDb API and requires an API key.
The key is configured through the environment variable `OMDB_API_KEY`.

## User Preferences & Favorites

The application saves:
- Your genre preferences to a local file at `~/.movie_recommender_prefs.txt` to provide personalized recommendations.
- Your favorite movies to a local file at `~/.movie_recommender_favorites.txt` for easy access to movies you like.

### Favorites Feature
The favorites feature allows you to:
- Add movies to your favorites collection
- View all your favorites in a dedicated section on the main screen
- Remove movies from favorites when desired
- Your favorites list persists between application restarts

### Trailer Feature
The trailer feature allows you to:
- Watch trailers for any movie with a single click
- Opens YouTube search results for the selected movie trailer in your default browser
- Combines movie title and year for accurate trailer search results