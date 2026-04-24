package com.urbanissue.service;

import com.urbanissue.model.GeoLocation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.SocketTimeoutException;
import java.net.ConnectException;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Service for geocoding addresses using OpenStreetMap Nominatim API
 */
public class GeoLocationService {

    private static final Logger logger = Logger.getLogger(GeoLocationService.class.getName());
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "CivicTrack/1.0";
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    private static final int DEFAULT_READ_TIMEOUT = 10000;
    private static final int SEARCH_CONNECT_TIMEOUT = 3000;
    private static final int SEARCH_READ_TIMEOUT = 5000;

    /**
     * Geocodes an address string to latitude/longitude coordinates
     * @param address The address to geocode
     * @return GeoLocation object with coordinates and formatted address, or null if not found
     * @throws IllegalArgumentException if address is null or empty
     */
    public GeoLocation geocodeAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String encodedAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
            String urlString = NOMINATIM_URL + "?q=" + encodedAddress + "&format=json&limit=1";

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.log(Level.WARNING, "Geocoding failed for address ''{0}'' with HTTP {1}",
                          new Object[]{address, responseCode});
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray results = new JSONArray(response.toString());
            if (results.length() == 0) {
                logger.log(Level.INFO, "No geocoding results found for address: {0}", address);
                return null;
            }

            JSONObject result = results.getJSONObject(0);

            if (!result.has("lat") || !result.has("lon")) {
                logger.log(Level.WARNING, "Invalid response format: missing coordinates for address ''{0}''", address);
                return null;
            }

            double latitude = result.getDouble("lat");
            double longitude = result.getDouble("lon");
            String displayName = result.optString("display_name", address);

            if (!GeoLocation.isValidCoordinates(latitude, longitude)) {
                logger.log(Level.WARNING, "Invalid coordinates received: lat={0}, lon={1} for address ''{2}''",
                          new Object[]{latitude, longitude, address});
                return null;
            }

            return new GeoLocation(latitude, longitude, displayName);

        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, "Timeout while geocoding address ''{0}'': {1}",
                      new Object[]{address, e.getMessage()});
            return null;
        } catch (ConnectException e) {
            logger.log(Level.WARNING, "Connection failed while geocoding address ''{0}'': {1}",
                      new Object[]{address, e.getMessage()});
            return null;
        } catch (JSONException e) {
            logger.log(Level.WARNING, "Invalid JSON response while geocoding address ''{0}'': {1}",
                      new Object[]{address, e.getMessage()});
            return null;
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO error during geocoding for address ''{0}'': {1}",
                      new Object[]{address, e.getMessage()});
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during geocoding for address ''{0}'': {1}",
                      new Object[]{address, e.getMessage()});
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing reader: {0}", e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Search for location suggestions based on partial input
     * @param query The partial address or location query
     * @param limit Maximum number of suggestions to return (must be positive)
     * @return List of location suggestions, empty list if no results or error occurs
     * @throws IllegalArgumentException if query is null, too short, or limit is invalid
     */
    public java.util.List<String> searchLocationSuggestions(String query, int limit) {
        if (query == null) {
            throw new IllegalArgumentException("Query cannot be null");
        }
        if (query.trim().length() < 2) {
            throw new IllegalArgumentException("Query must be at least 2 characters long");
        }
        if (limit <= 0 || limit > 50) {
            throw new IllegalArgumentException("Limit must be between 1 and 50");
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            String urlString = NOMINATIM_URL + "?q=" + encodedQuery + "&format=json&limit=" + limit + "&addressdetails=1";

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(SEARCH_CONNECT_TIMEOUT);
            connection.setReadTimeout(SEARCH_READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.log(Level.WARNING, "Location search failed for query ''{0}'' with HTTP {1}",
                          new Object[]{query, responseCode});
                return new java.util.ArrayList<>();
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray results = new JSONArray(response.toString());
            java.util.List<String> suggestions = new java.util.ArrayList<>();

            for (int i = 0; i < results.length() && i < limit; i++) {
                try {
                    JSONObject result = results.getJSONObject(i);
                    if (result.has("display_name")) {
                        String displayName = result.getString("display_name");
                        String cleanDisplayName = formatDisplayName(displayName);
                        if (cleanDisplayName != null && !cleanDisplayName.trim().isEmpty()) {
                            suggestions.add(cleanDisplayName);
                        }
                    }
                } catch (JSONException e) {
                    logger.log(Level.WARNING, "Skipping invalid result at index {0} for query ''{1}'': {2}",
                              new Object[]{i, query, e.getMessage()});
                }
            }

            logger.log(Level.INFO, "Found {0} location suggestions for query: {1}",
                      new Object[]{suggestions.size(), query});
            return suggestions;

        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, "Timeout while searching for query ''{0}'': {1}",
                      new Object[]{query, e.getMessage()});
            return new java.util.ArrayList<>();
        } catch (ConnectException e) {
            logger.log(Level.WARNING, "Connection failed while searching for query ''{0}'': {1}",
                      new Object[]{query, e.getMessage()});
            return new java.util.ArrayList<>();
        } catch (JSONException e) {
            logger.log(Level.WARNING, "Invalid JSON response while searching for query ''{0}'': {1}",
                      new Object[]{query, e.getMessage()});
            return new java.util.ArrayList<>();
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO error during location search for query ''{0}'': {1}",
                      new Object[]{query, e.getMessage()});
            return new java.util.ArrayList<>();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during location search for query ''{0}'': {1}",
                      new Object[]{query, e.getMessage()});
            return new java.util.ArrayList<>();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing reader: {0}", e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Formats display name to be more readable by prioritizing important parts
     */
    private String formatDisplayName(String displayName) {
        // Split by commas and take the most relevant parts (usually first 3-4 parts)
        String[] parts = displayName.split(",");
        if (parts.length <= 3) {
            return displayName;
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < Math.min(4, parts.length); i++) {
            if (i > 0) formatted.append(", ");
            formatted.append(parts[i].trim());
        }
        return formatted.toString();
    }

    /**
     * Reverse geocodes coordinates to an address
     * @param latitude The latitude coordinate (must be between -90 and 90)
     * @param longitude The longitude coordinate (must be between -180 and 180)
     * @return Formatted address string, or null if not found
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public String reverseGeocode(double latitude, double longitude) {
        if (!GeoLocation.isValidCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException(
                String.format("Invalid coordinates: latitude=%f (must be -90 to 90), longitude=%f (must be -180 to 180)",
                             latitude, longitude));
        }

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = "https://nominatim.openstreetmap.org/reverse?lat=" + latitude +
                             "&lon=" + longitude + "&format=json";

            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                logger.log(Level.WARNING, "Reverse geocoding failed for coordinates ({0}, {1}) with HTTP {2}",
                          new Object[]{latitude, longitude, responseCode});
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject result = new JSONObject(response.toString());

            if (!result.has("display_name")) {
                logger.log(Level.WARNING, "No display_name in reverse geocoding response for coordinates ({0}, {1})",
                          new Object[]{latitude, longitude});
                return null;
            }

            String displayName = result.getString("display_name");
            if (displayName == null || displayName.trim().isEmpty()) {
                logger.log(Level.WARNING, "Empty display_name in reverse geocoding response for coordinates ({0}, {1})",
                          new Object[]{latitude, longitude});
                return null;
            }

            return displayName;

        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, "Timeout during reverse geocoding for coordinates ({0}, {1}): {2}",
                      new Object[]{latitude, longitude, e.getMessage()});
            return null;
        } catch (ConnectException e) {
            logger.log(Level.WARNING, "Connection failed during reverse geocoding for coordinates ({0}, {1}): {2}",
                      new Object[]{latitude, longitude, e.getMessage()});
            return null;
        } catch (JSONException e) {
            logger.log(Level.WARNING, "Invalid JSON response during reverse geocoding for coordinates ({0}, {1}): {2}",
                      new Object[]{latitude, longitude, e.getMessage()});
            return null;
        } catch (IOException e) {
            logger.log(Level.WARNING, "IO error during reverse geocoding for coordinates ({0}, {1}): {2}",
                      new Object[]{latitude, longitude, e.getMessage()});
            return null;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during reverse geocoding for coordinates ({0}, {1}): {2}",
                      new Object[]{latitude, longitude, e.getMessage()});
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error closing reader: {0}", e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}