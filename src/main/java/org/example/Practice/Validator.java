package org.example.Practice;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

public class Validator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}$");

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidDate(String date) {
        if (date == null || !DATE_PATTERN.matcher(date).matches()) {
            return false;
        }

        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotFutureDate(String date) {
        if (!isValidDate(date)) {
            return false;
        }

        try {
            LocalDate inputDate = LocalDate.parse(date);
            LocalDate currentDate = LocalDate.now();
            return !inputDate.isAfter(currentDate);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAlphabetic(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }

        return value.chars().allMatch(Character::isLetter);
    }

    public static void validateField(String fieldName, boolean isValid, String errorMessage, List<String> errors) {
        if (!isValid) {
            errors.add(errorMessage);
        }
    }
}