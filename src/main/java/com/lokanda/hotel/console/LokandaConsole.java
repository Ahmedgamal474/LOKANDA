package com.lokanda.hotel.console;

import com.lokanda.hotel.DAO.DatabaseConnection;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.console.screens.BookingScreen;
import com.lokanda.hotel.console.screens.FeedbackScreen;
import com.lokanda.hotel.console.screens.GuestScreen;
import com.lokanda.hotel.console.screens.OfferScreen;
import com.lokanda.hotel.console.screens.PaymentScreen;
import com.lokanda.hotel.console.screens.ReceptionistScreen;
import com.lokanda.hotel.console.screens.RoomScreen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/** Standalone interactive terminal for the existing LOKANDA hotel system. */
public final class LokandaConsole {
    private final ConsoleInput input;
    private final BookingScreen bookingScreen;
    private final GuestScreen guestScreen;
    private final RoomScreen roomScreen;
    private final OfferScreen offerScreen;
    private final PaymentScreen paymentScreen;
    private final FeedbackScreen feedbackScreen;
    private final ReceptionistScreen receptionistScreen;

    public LokandaConsole(Scanner scanner) {
        this.input = new ConsoleInput(scanner);
        this.bookingScreen = new BookingScreen(input);
        this.guestScreen = new GuestScreen(input);
        this.roomScreen = new RoomScreen(input);
        this.offerScreen = new OfferScreen(input);
        this.paymentScreen = new PaymentScreen(input);
        this.feedbackScreen = new FeedbackScreen(input);
        this.receptionistScreen = new ReceptionistScreen(input);
    }

    public void run() {
        ConsoleUI.printWelcome();
        boolean running = true;
        while (running) {
            ConsoleUI.header("Main Menu");
            ConsoleUI.option("1", "Booking management");
            ConsoleUI.option("2", "Guest management");
            ConsoleUI.option("3", "Room management");
            ConsoleUI.option("4", "Offer management");
            ConsoleUI.option("5", "Payment management");
            ConsoleUI.option("6", "Feedback management");
            ConsoleUI.option("7", "Receptionist management");
            ConsoleUI.option("8", "Test database connection");
            ConsoleUI.option("0", "Exit LOKANDA console");
            ConsoleUI.divider();

            int choice = input.readInt("Choose an option:", 0, 8);
            switch (choice) {
                case 1 -> bookingScreen.showMenu();
                case 2 -> guestScreen.showMenu();
                case 3 -> roomScreen.showMenu();
                case 4 -> offerScreen.showMenu();
                case 5 -> paymentScreen.showMenu();
                case 6 -> feedbackScreen.showMenu();
                case 7 -> receptionistScreen.showMenu();
                case 8 -> testDatabase();
                case 0 -> running = false;
                default -> { }
            }
        }
        ConsoleUI.success("Thank you for using LOKANDA. Goodbye!");
    }

    private void testDatabase() {
        try (Connection ignored = DatabaseConnection.getConnection()) {
            ConsoleUI.success("Database connection is working.");
        } catch (SQLException exception) {
            ConsoleUI.error("Database connection failed: " + exception.getMessage());
        }
        input.pause();
    }
}
