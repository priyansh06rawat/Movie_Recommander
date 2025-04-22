#!/bin/bash

echo "=== Starting MovieMatchMaker Netflix-style Movie Recommender ==="
echo ""

# Check for OMDB API key
if [ -z "$OMDB_API_KEY" ]; then
  echo "WARNING: OMDB_API_KEY environment variable is not set"
  echo "You may experience API errors. Please set it with:"
  echo "  export OMDB_API_KEY=your_api_key_here"
  echo ""
  read -p "Would you like to continue anyway? (y/n): " choice
  case "$choice" in 
    y|Y ) echo "Continuing without API key...";;
    * ) echo "Exiting. Please set your API key and try again."; exit 1;;
  esac
else
  echo "OMDB API key detected ✓"
fi

echo ""
echo "Setting memory allocation to 1GB to prevent exit code 137 errors..."
# Set Java memory options (prevents error code 137 - out of memory)
export MAVEN_OPTS="-Xmx1024m"

# Clean and run the application
echo "Building and running the application..."
mvn clean javafx:run

# If the application fails to start, provide guidance
if [ $? -ne 0 ]; then
  echo ""
  echo "!!! Application failed to start. Troubleshooting Guide !!!"
  echo "--------------------------------------------------------"
  echo "1. Memory Issues (Error code 137):"
  echo "   Try running with even more memory:"
  echo "   export MAVEN_OPTS=\"-Xmx2048m\""
  echo "   mvn clean javafx:run"
  echo ""
  echo "2. API Key Issues:"
  echo "   Verify the OMDb API key is set correctly:"
  echo "   export OMDB_API_KEY=your_api_key_here"
  echo ""
  echo "3. JavaFX Issues:"
  echo "   Make sure JavaFX is properly installed and accessible"
  echo "   On some systems, you may need to install it separately."
  echo ""
  echo "4. If you see 'Cannot invoke 'javafx.scene.control.ProgressIndicator.setVisible(boolean)' because 'this.loadingIndicator' is null'"
  echo "   This has been fixed in the latest version. Please make sure you have the latest code."
  echo "   Try running: mvn clean compile before running the application."
  echo ""
fi