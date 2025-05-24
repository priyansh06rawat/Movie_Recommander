# Netflix-Style Movie Recommender Application

This is a Netflix-style Java desktop application for movie recommendations using the OMDb API. The application provides a user-friendly interface for browsing and searching movies, with personalized recommendations based on your preferences.

## Authors

- Priyansh
- Rahul Berwal

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

![Main Screen](https://github.com/priyansh06rawat/Movie_Recommander/blob/main/Main_Screen.png)

### Movie Details
Clicking on any movie poster opens a detailed view with:
- Movie poster
- Title, year, rating
- Director and cast information
- Plot summary
- Similar movie recommendations

![Movie Details](https://github.com/priyansh06rawat/Movie_Recommander/blob/main/movie_detail.png)

### Search Results
The search functionality allows you to find specific movies:

![Search Results](https://github.com/priyansh06rawat/Movie_Recommander/blob/main/Search.png)

## Technical Information

- Built with **Java** and **JavaFX** for the UI
- Uses **FXML** for UI layout definition
- Connects to **OMDb API** for movie data
- Implements **MVC pattern** (Model-View-Controller)
- Asynchronous data loading for smooth user experience

## Installation and Running the Application

### Prerequisites
1. Java 11 or later
2. Maven
3. JavaFX
4. OMDb API key

### Quick Start with the Provided Script (Recommended)

1. Make the run script executable:
   ```bash
   chmod +x run_movie_recommender.sh
   ```

2. Set your OMDb API key:
   ```bash
   export OMDB_API_KEY=your_api_key_here
   ```

3. Run the application:
   ```bash
   ./run_movie_recommender.sh
   ```

### Running in Fedora

To run the application in Fedora with VS Code:

1. Install prerequisites:
   ```bash
   sudo dnf install java-11-openjdk java-11-openjdk-devel maven
   ```

2. Install VS Code and Java extensions:
   ```bash
   sudo rpm --import https://packages.microsoft.com/keys/microsoft.asc
   sudo sh -c 'echo -e "[code]\nname=Visual Studio Code\nbaseurl=https://packages.microsoft.com/yumrepos/vscode\nenabled=1\ngpgcheck=1\ngpgkey=https://packages.microsoft.com/keys/microsoft.asc" > /etc/yum.repos.d/vscode.repo'
   sudo dnf install code
   ```
   
3. Install JavaFX:
   ```bash
   sudo dnf install java-openjfx
   ```

4. Set your API key:
   ```bash
   export OMDB_API_KEY=your_api_key_here
   ```

5. Run the application using either:
   ```bash
   # Using our run script with improved error handling:
   ./run_movie_recommender.sh
   
   # Or with Maven directly:
   export MAVEN_OPTS="-Xmx1024m"  # Prevents memory errors
   mvn clean javafx:run
   ```

### Troubleshooting
If you encounter any issues:

1. **Memory Issues (Exit code 137)**: 
   - This is a sign of insufficient memory. Increase allocation:
   ```bash
   export MAVEN_OPTS="-Xmx2048m"
   mvn clean javafx:run
   ```

2. **API Key Issues**:
   - Verify your OMDb API key is correctly set in your environment

3. **"Cannot invoke 'loadingIndicator.setVisible(boolean)' because 'loadingIndicator' is null"**:
   - This has been fixed in the latest version
   - Make sure to run `mvn clean compile` to rebuild the latest code

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
