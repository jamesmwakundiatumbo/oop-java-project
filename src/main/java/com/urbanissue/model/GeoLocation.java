package com.urbanissue.model;

/**
 * Data class representing geographic location with coordinates and formatted address
 */
public class GeoLocation {
    private final double latitude;
    private final double longitude;
    private final String formattedAddress;

    private static final double EARTH_RADIUS_KM = 6371.0;

    public GeoLocation(double latitude, double longitude, String formattedAddress) {
        validateCoordinates(latitude, longitude);
        this.latitude = latitude;
        this.longitude = longitude;
        this.formattedAddress = formattedAddress;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    /**
     * Validates that coordinates are within valid ranges
     * @param latitude Must be between -90 and 90 degrees
     * @param longitude Must be between -180 and 180 degrees
     * @throws IllegalArgumentException if coordinates are invalid
     */
    private static void validateCoordinates(double latitude, double longitude) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees. Got: " + latitude);
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees. Got: " + longitude);
        }
    }

    /**
     * Calculates the distance between this location and another location using the Haversine formula
     * @param other The other location to calculate distance to
     * @return Distance in kilometers
     * @throws IllegalArgumentException if other location is null
     */
    public double distanceToKm(GeoLocation other) {
        if (other == null) {
            throw new IllegalArgumentException("Other location cannot be null");
        }

        double lat1Rad = Math.toRadians(this.latitude);
        double lat2Rad = Math.toRadians(other.latitude);
        double deltaLatRad = Math.toRadians(other.latitude - this.latitude);
        double deltaLonRad = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the distance between this location and another location in miles
     * @param other The other location to calculate distance to
     * @return Distance in miles
     * @throws IllegalArgumentException if other location is null
     */
    public double distanceToMiles(GeoLocation other) {
        return distanceToKm(other) * 0.621371;
    }

    /**
     * Creates a GeoLocation with coordinate validation
     * @param latitude Must be between -90 and 90 degrees
     * @param longitude Must be between -180 and 180 degrees
     * @param formattedAddress Human-readable address
     * @return New GeoLocation instance
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public static GeoLocation create(double latitude, double longitude, String formattedAddress) {
        return new GeoLocation(latitude, longitude, formattedAddress);
    }

    /**
     * Checks if coordinates are valid without throwing exceptions
     * @param latitude Latitude to validate
     * @param longitude Longitude to validate
     * @return true if coordinates are valid, false otherwise
     */
    public static boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90.0 && latitude <= 90.0 &&
               longitude >= -180.0 && longitude <= 180.0;
    }

    @Override
    public String toString() {
        return formattedAddress + " (" + latitude + ", " + longitude + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        GeoLocation that = (GeoLocation) obj;
        return Double.compare(that.latitude, latitude) == 0 &&
               Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(latitude, longitude);
    }
}