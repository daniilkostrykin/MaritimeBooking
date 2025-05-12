package org.example.Practice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Класс для валидации входных данных
 */
public class Validator {
    // Паттерн для проверки email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Паттерн для проверки даты формата YYYY-MM-DD
    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}$");

    /**
     * Проверяет, что строка не пустая
     * 
     * @param value значение
     * @return true если строка не пустая
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Проверяет, что строка содержит число с плавающей точкой
     * 
     * @param value значение
     * @return true если строка содержит число
     */
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

    /**
     * Проверяет, что строка содержит целое число
     * 
     * @param value значение
     * @return true если строка содержит целое число
     */
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

    /**
     * Проверяет, что строка содержит корректный email
     * 
     * @param email строка с email
     * @return true если email корректный
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Проверяет, что строка содержит корректную дату в формате YYYY-MM-DD
     * 
     * @param date строка с датой
     * @return true если дата корректная
     */
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

    /**
     * Проверяет, что дата не в будущем
     * 
     * @param date строка с датой в формате YYYY-MM-DD
     * @return true если дата не в будущем
     */
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

    /**
     * Проверяет, что строка содержит только буквы (без цифр)
     * 
     * @param value строка для проверки
     * @return true если строка содержит только буквы
     */
    public static boolean isAlphabetic(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }

        return value.chars().allMatch(Character::isLetter);
    }

    /**
     * Выполняет валидацию поля и добавляет сообщение об ошибке при необходимости
     * 
     * @param fieldName    имя поля
     * @param isValid      результат проверки
     * @param errorMessage сообщение об ошибке
     * @param errors       список ошибок
     */
    public static void validateField(String fieldName, boolean isValid, String errorMessage, List<String> errors) {
        if (!isValid) {
            errors.add(errorMessage);
        }
    }
}