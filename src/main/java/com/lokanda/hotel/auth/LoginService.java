package com.lokanda.hotel.auth;

import java.util.Scanner;

public class LoginService {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    public static boolean login(Scanner scanner) {

        int attempts = 3;

        while (attempts > 0) {

            clearScreen();

            System.out.println("=================================");
            System.out.println("          LOKANDA HOTEL          ");
            System.out.println("       RECEPTIONIST LOGIN        ");
            System.out.println("=================================");

            System.out.print("Enter Username: ");
            String username = scanner.nextLine();

            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            if (username.equals(USERNAME) &&
                    password.equals(PASSWORD)) {

                clearScreen();

                System.out.println("=================================");
                System.out.println("       LOGIN SUCCESSFUL!");
                System.out.println("       Welcome to LOKANDA");
                System.out.println("=================================");

                return true;
            }

            attempts--;

            System.out.println("\nInvalid username or password!");

            if (attempts > 0) {
                System.out.println("Remaining attempts: " + attempts);
                System.out.println("\nPress Enter to try again...");
                scanner.nextLine();
            }
        }

        clearScreen();

        System.out.println("=================================");
        System.out.println("       ACCESS DENIED!");
        System.out.println("       Too many failed attempts.");
        System.out.println("=================================");

        return false;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}