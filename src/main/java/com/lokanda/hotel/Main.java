package com.lokanda.hotel;

import com.lokanda.hotel.auth.LoginService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        boolean loggedIn = LoginService.login(scanner);

        if (!loggedIn) {
            scanner.close();
            return;
        }

        int choice;

        do {

            LoginService.clearScreen();

            System.out.println("=================================");
            System.out.println("          LOKANDA HOTEL          ");
            System.out.println("             MAIN MENU           ");
            System.out.println("=================================");

            System.out.println("1. Create New Booking");
            System.out.println("2. View All Bookings");
            System.out.println("3. Search Booking");
            System.out.println("4. Modify Booking");
            System.out.println("5. Cancel Booking");
            System.out.println("6. Manage Rooms");
            System.out.println("7. Manage Offers");
            System.out.println("8. Exit");

            System.out.println("=================================");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                choice = -1;
            }

            LoginService.clearScreen();

            switch (choice) {

                case 1:
                    System.out.println("Create New Booking");
                    break;

                case 2:
                    System.out.println("View All Bookings");
                    break;

                case 3:
                    System.out.println("Search Booking");
                    break;

                case 4:
                    System.out.println("Modify Booking");
                    break;

                case 5:
                    System.out.println("Cancel Booking");
                    break;

                case 6:
                    System.out.println("Manage Rooms");
                    break;

                case 7:
                    System.out.println("Manage Offers");
                    break;

                case 8:
                    System.out.println("Thank you for using LOKANDA!");
                    break;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }

            if (choice != 8) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 8);

        scanner.close();
    }
}
