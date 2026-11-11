package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;

/** Login screen dedicated to the standalone console; the original LoginService is untouched. */
public final class ConsoleLoginScreen {
    // Keep these values aligned with the current credentials in the original LoginService.
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";
    private static final int MAX_ATTEMPTS = 3;

    private ConsoleLoginScreen() { }

    public static boolean login(ConsoleInput input) {
        if (input == null) {
            throw new IllegalArgumentException("ConsoleInput cannot be null.");
        }

        int attemptsLeft = MAX_ATTEMPTS;
        while (attemptsLeft > 0) {
            ConsoleUI.clearScreen();
            ConsoleUI.header("Receptionist Login");

            String username = input.readRequired("Enter username:");
            String password = input.readRequired("Enter password:");

            if (USERNAME.equals(username) && PASSWORD.equals(password)) {
                ConsoleUI.success("Login successful. Welcome to LOKANDA!");
                return true;
            }

            attemptsLeft--;
            ConsoleUI.error("Invalid username or password.");
            if (attemptsLeft > 0) {
                ConsoleUI.warning("Remaining attempts: " + attemptsLeft);
                input.pause();
            }
        }

        ConsoleUI.clearScreen();
        ConsoleUI.header("Access Denied");
        ConsoleUI.error("Too many failed login attempts.");
        return false;
    }
}
