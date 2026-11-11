package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.ReceptionistDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.model.person.Guest;
import com.lokanda.hotel.model.person.Receptionist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ReceptionistScreen extends BaseScreen {
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();

    public ReceptionistScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Receptionist Management");
            ConsoleUI.option("1", "Add receptionist");
            ConsoleUI.option("2", "View all receptionists");
            ConsoleUI.option("3", "Search by employee ID");
            ConsoleUI.option("4", "Update receptionist");
            ConsoleUI.option("5", "Delete receptionist");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 5);
            switch (choice) {
                case 1 -> execute(this::addReceptionist);
                case 2 -> execute(this::listReceptionists);
                case 3 -> execute(this::searchReceptionist);
                case 4 -> execute(this::updateReceptionist);
                case 5 -> execute(this::deleteReceptionist);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void addReceptionist() throws SQLException {
        Receptionist receptionist = readReceptionist(0);
        receptionistDAO.addReceptionist(receptionist);
        ConsoleUI.success("Receptionist added. Person ID: " + receptionist.getPersonId());
    }

    private void listReceptionists() throws SQLException {
        List<List<String>> rows = new ArrayList<>();
        for (Receptionist receptionist : receptionistDAO.getAllReceptionists()) {
            rows.add(List.of(
                    String.valueOf(receptionist.getPersonId()),
                    receptionist.getFullName(),
                    receptionist.getEmployeeId(),
                    receptionist.getEmail(),
                    receptionist.getPhone()
            ));
        }
        ConsoleUI.printTable(List.of("PERSON ID", "NAME", "EMPLOYEE ID", "EMAIL", "PHONE"), rows);
    }

    private void searchReceptionist() throws SQLException {
        String employeeId = input.readRequired("Employee ID:");
        Receptionist receptionist = receptionistDAO.getReceptionistByEmployeeId(employeeId);
        if (receptionist == null) {
            ConsoleUI.warning("Receptionist not found.");
            return;
        }
        printReceptionist(receptionist);
    }

    private void updateReceptionist() throws SQLException {
        int id = readId("Receptionist person ID to update:");
        if (receptionistDAO.getReceptionistById(id) == null) {
            throw new IllegalArgumentException("Receptionist not found.");
        }
        Receptionist updated = readReceptionist(id);
        receptionistDAO.updateReceptionist(updated);
        ConsoleUI.success("Receptionist updated.");
    }

    private void deleteReceptionist() throws SQLException {
        int id = readId("Receptionist person ID to delete:");
        Receptionist receptionist = receptionistDAO.getReceptionistById(id);
        if (receptionist == null) {
            throw new IllegalArgumentException("Receptionist not found.");
        }
        printReceptionist(receptionist);
        if (!input.readYesNo("Delete this receptionist permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        receptionistDAO.deleteReceptionist(id);
        ConsoleUI.success("Receptionist deleted.");
    }

    private Receptionist readReceptionist(int id) {
        // Guest exposes the same inherited Person setters, so the existing Person validation is reused.
        Guest personValidator = new Guest();
        String firstName = input.readValidated("First name:", value -> {
            personValidator.setFirstName(value);
            return value;
        });
        String lastName = input.readValidated("Last name:", value -> {
            personValidator.setLastName(value);
            return value;
        });
        String email = input.readValidated("Email:", value -> {
            personValidator.setEmail(value);
            return value;
        });
        String phone = input.readValidated("Phone (Egyptian mobile, 11 digits):", value -> {
            personValidator.setPhone(value);
            return value;
        });
        Receptionist employeeValidator = new Receptionist(0, "Temp", "User",
                "temp@lokanda.local", "01000000000", "TMP-001");
        String employeeId = input.readValidated("Employee ID:", value -> {
            employeeValidator.setEmployeeId(value);
            return value;
        });
        return new Receptionist(id, firstName, lastName, email, phone, employeeId);
    }

    private void printReceptionist(Receptionist receptionist) {
        ConsoleUI.section("Receptionist details");
        ConsoleUI.info("Person ID  : " + receptionist.getPersonId());
        ConsoleUI.info("Name       : " + receptionist.getFullName());
        ConsoleUI.info("Employee ID: " + receptionist.getEmployeeId());
        ConsoleUI.info("Email      : " + receptionist.getEmail());
        ConsoleUI.info("Phone      : " + receptionist.getPhone());
    }
}
