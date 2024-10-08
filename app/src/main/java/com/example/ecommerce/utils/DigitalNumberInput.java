package com.example.ecommerce.utils;

import android.util.Log;

import java.util.Locale;

public class DigitalNumberInput {
    private static final String TAG = "DigitalNumberInput";
    private String currentText = ""; // Store current text to avoid redundant formatting

    /**
     * Handles incoming input for the ATM-style number field, formats it,
     * and notifies the listener to update the UI accordingly.
     *
     * @param newText  The latest text input by the user.
     * @param listener The listener that applies the formatted value to the input field.
     */
    public void onInputComesIn(String newText, DigitalNumberInputListener listener) {
        Log.d(TAG, "New Text Received: " + newText);

        // Only format if the new text differs from the current text to avoid unnecessary processing.
        if (!newText.equals(currentText)) {
            // Format the input according to ATM-style number input rules
            String formattedValue = formatInput(newText);

            Log.d(TAG, "Formatted Value: " + formattedValue);

            // Update currentText to prevent recursive formatting
            currentText = formattedValue;

            // Notify the listener to update the input field with the formatted text
            listener.onInputReadyToSet(formattedValue, currentText.length());
        }
    }

    /**
     * Formats the input text to simulate ATM-style number input:
     * - Accepts only numeric input.
     * - Automatically adds a decimal point to ensure two decimal places.
     * - Ensures right-to-left digit insertion.
     *
     * @param input The raw text input entered by the user.
     * @return A formatted string with two decimal places.
     */
    private String formatInput(String input) {
        // Remove any non-numeric characters from the input
        String cleanInput = input.replaceAll("[^\\d]", ""); // Only keep digits (no commas, periods, etc.)

        Log.d(TAG, "Cleaned Input: " + cleanInput);

        // If the input is empty or only non-numeric characters were entered, return a default value of "0.00"
        if (cleanInput.isEmpty()) {
            return "0.00";
        }

        // Parse the cleaned input as a long to ensure precision and to handle large numbers
        long parsedValue = Long.parseLong(cleanInput);

        // Format the parsed value as a decimal number with 2 decimal places (divide by 100 for ATM-style input)
        // Locale.US ensures dot (.) as the decimal separator
        return String.format(Locale.US, "%.2f", parsedValue / 100.0);
    }
}
