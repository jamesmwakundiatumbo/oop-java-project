#!/bin/bash
# CivicTrack Web Browser Deployment

echo "Starting CivicTrack Web Server..."
echo "This will run a web interface at http://localhost:8080"

# Make sure dependencies are available
mvn clean compile

# Run Spring Boot web server
mvn spring-boot:run

echo "Web application should be available at: http://localhost:8080"