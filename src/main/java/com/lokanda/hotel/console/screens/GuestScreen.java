package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.GuestDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.model.person.Guest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class GuestScreen extends BaseScreen {
    private final GuestDAO guestDAO = new GuestDAO();

    public GuestScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Guest Management");
            ConsoleUI.option("1", "Register new guest");
            ConsoleUI.option("2", "View all guests");
            ConsoleUI.option("3", "Search guest");
            ConsoleUI.option("4", "Update guest");
            ConsoleUI.option("5", "Delete guest");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 5);
            switch (choice) {
                case 1 -> execute(this::addGuest);
                case 2 -> execute(this::listGuests);
                case 3 -> execute(this::searchGuest);
                case 4 -> execute(this::updateGuest);
                case 5 -> execute(this::deleteGuest);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void addGuest() throws SQLException {
        Guest guest = new Guest();
        readGuestFields(guest);
        if (guestDAO.addGuest(guest)) {
            ConsoleUI.success("Guest registered. Guest ID: " + guest.getPersonId());
        } else {
            ConsoleUI.warning("The guest was not added.");
        }
    }

    private void listGuests() throws SQLException {
        List<List<String>> rows = new ArrayList<>();
        for (Guest guest : guestDAO.getAllGuests()) {
            rows.add(List.of(
                    String.valueOf(guest.getPersonId()),
                    guest.getFullName(),
                    guest.getEmail(),
                    guest.getPhone(),
                    guest.getNationalId()
            ));
        }
        ConsoleUI.printTable(List.of("ID", "NAME", "EMAIL", "PHONE", "NATIONAL ID"), rows);
    }

    private void searchGuest() throws SQLException {
        ConsoleUI.section("Search guest");
        ConsoleUI.option("1", "Search by guest ID");
        ConsoleUI.option("2", "Search by national ID");
        int mode = input.readInt("Search method:", 1, 2);
        Guest guest = mode == 1
                ? guestDAO.getGuestById(readId("Guest ID:"))
                : guestDAO.getGuestByNationalId(input.readRequired("National ID:"));
        if (guest == null) {
            ConsoleUI.warning("No guest matched your search.");
            return;
        }
        printGuest(guest);
    }

    private void updateGuest() throws SQLException {
        int id = readId("Guest ID to update:");
        Guest guest = guestDAO.getGuestById(id);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }
        readGuestFields(guest);
        if (guestDAO.updateGuest(guest)) {
            ConsoleUI.success("Guest information updated.");
        } else {
            ConsoleUI.warning("No guest record was updated.");
        }
    }

    private void deleteGuest() throws SQLException {
        int id = readId("Guest ID to delete:");
        Guest guest = guestDAO.getGuestById(id);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }
        printGuest(guest);
        if (!input.readYesNo("Delete this guest permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        if (guestDAO.deleteGuest(id)) {
            ConsoleUI.success("Guest deleted.");
        } else {
            ConsoleUI.warning("Guest was not deleted.");
        }
    }

    private void readGuestFields(Guest guest) {
        input.readValidated("First name:", value -> { guest.setFirstName(value); return value; });
        input.readValidated("Last name:", value -> { guest.setLastName(value); return value; });
        input.readValidated("Email:", value -> { guest.setEmail(value); return value; });
        input.readValidated("Phone (Egyptian mobile, 11 digits):", value -> { guest.setPhone(value); return value; });
        input.readValidated("National ID (14 digits):", value -> { guest.setNationalId(value); return value; });
    }

    private void printGuest(Guest guest) {
        ConsoleUI.section("Guest details");
        ConsoleUI.info("ID          : " + guest.getPersonId());
        ConsoleUI.info("Name        : " + guest.getFullName());
        ConsoleUI.info("Email       : " + guest.getEmail());
        ConsoleUI.info("Phone       : " + guest.getPhone());
        ConsoleUI.info("National ID : " + guest.getNationalId());
    }
}
