package com.example.ecommerce.utils;

/**
 * Interface to be implemented by components using DigitalNumberInput.
 * It provides a callback for setting the formatted text in the input field.
 */
public interface DigitalNumberInputListener {
    /**
     * Called when the formatted input is ready to be set in the input field.
     *
     * @param formattedValue The formatted string value to be displayed.
     * @param cursorPosition The cursor position to maintain after formatting.
     */
    void onInputReadyToSet(String formattedValue, int cursorPosition);
}