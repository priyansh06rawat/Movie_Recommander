# Running the Netflix-Style Movie Recommender Application

This document provides detailed instructions on how to run the Movie Recommender application. Follow these steps to get the application up and running on your system.

## Prerequisites

Before you begin, make sure you have the following installed:

1. **Java 11 or later** - The application is built using Java
2. **Maven** - Used for project management and building
3. **JavaFX** - Required for the user interface
4. **OMDb API Key** - Needed to fetch movie data

## Setting Up Your OMDb API Key

The application requires an OMDb API key to fetch movie data:

1. If you don't have an API key, you can get one from the [OMDb API website](http://www.omdbapi.com/apikey.aspx)
2. Once you have your API key, set it as an environment variable:

```bash
# For Linux/macOS:
export OMDB_API_KEY=your_api_key_here

# For Windows (Command Prompt):
set OMDB_API_KEY=your_api_key_here

# For Windows (PowerShell):
$env:OMDB_API_KEY = "your_api_key_here"
```

## Running the Application

### Method 1: Using the Run Script (Recommended)

We've provided a convenient script that handles memory allocation and other settings automatically:

1. Make the script executable (Linux/macOS only):
   ```bash
   chmod +x run_movie_recommender.sh
   ```

2. Run the application:
   ```bash
   ./run_movie_recommender.sh
   ```

### Method 2: Using Maven Directly

If you prefer to use Maven commands directly:

```bash
# Set memory allocation to prevent out-of-memory errors
export MAVEN_OPTS="-Xmx1024m"

# Build and run the application
mvn clean javafx:run
```

## Troubleshooting Common Issues

### 1. "Cannot invoke 'javafx.scene.control.ProgressIndicator.setVisible(boolean)' because 'this.loadingIndicator' is null"

This issue has been fixed in the latest version. Make sure you:
- Have the latest code
- Clean and compile before running:
  ```bash
  mvn clean compile
  mvn javafx:run
  ```

### 2. "Exit code 137" or other memory-related errors

This indicates insufficient memory allocation. Try:
```bash
export MAVEN_OPTS="-Xmx2048m"
mvn clean javafx:run
```

### 3. API-related errors

- Verify your API key is set correctly
- Check your internet connection
- The application includes retry mechanisms, but very slow connections may still time out

### 4. JavaFX not found

Make sure JavaFX is installed correctly:
```bash
# For Fedora/RHEL:
sudo dnf install java-openjfx

# For Ubuntu/Debian:
sudo apt install openjfx

# For Mac (using Homebrew):
brew install openjfx
```

## Using the Application

Once running, you can:
- Browse movie recommendations in the main view
- Search for movies using the search bar
- Click on any movie poster to see detailed information
- Add movies to your favorites collection
- Open movie trailers with the trailer button
- View your favorites in the dedicated section

## Additional Information

- User preferences and favorites are saved to `~/.movie_recommender_prefs.txt` and `~/.movie_recommender_favorites.txt`
- The application uses JavaFX for a modern, responsive user interface
- The design follows the Model-View-Controller (MVC) pattern
- Data loading is asynchronous for a smooth user experience