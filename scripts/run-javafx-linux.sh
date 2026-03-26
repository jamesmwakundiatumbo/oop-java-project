#!/bin/bash
# CivicTrack JavaFX Runner for Linux

echo "Starting CivicTrack JavaFX Application..."
echo "Make sure MySQL is running and database is loaded!"

# Compile and run JavaFX
mvn clean compile javafx:run

echo "JavaFX application finished."