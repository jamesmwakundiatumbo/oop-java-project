package com.urbanissue.util;

import java.util.regex.Pattern;

/**
 * Input validation methods for forms.
 */
public final class ValidationHelper {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationHelper() {}

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPasswordLength(String password, int minLength) {
        return password != null && password.length() >= minLength;
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (isBlank(value)) return fieldName + " is required.";
        return null;
    }
}
