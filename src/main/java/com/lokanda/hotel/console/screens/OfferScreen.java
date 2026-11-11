package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.OfferDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.model.offer.Offer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class OfferScreen extends BaseScreen {
    private final OfferDAO offerDAO = new OfferDAO();

    public OfferScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Offer Management");
            ConsoleUI.option("1", "Create offer");
            ConsoleUI.option("2", "View all offers");
            ConsoleUI.option("3", "View active offers");
            ConsoleUI.option("4", "Update offer");
            ConsoleUI.option("5", "Delete offer");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 5);
            switch (choice) {
                case 1 -> execute(() -> saveOffer(0));
                case 2 -> execute(() -> listOffers(false));
                case 3 -> execute(() -> listOffers(true));
                case 4 -> execute(this::updateOffer);
                case 5 -> execute(this::deleteOffer);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void listOffers(boolean activeOnly) throws SQLException {
        List<Offer> offers = activeOnly ? offerDAO.getActiveOffers() : offerDAO.getAllOffers();
        List<List<String>> rows = new ArrayList<>();
        for (Offer offer : offers) {
            rows.add(List.of(
                    String.valueOf(offer.getOfferId()),
                    offer.getName(),
                    offer.getDiscountPercentage() + "%",
                    offer.getStartDate().toString(),
                    offer.getEndDate().toString(),
                    offer.isActive() ? "ACTIVE" : "INACTIVE"
            ));
        }
        ConsoleUI.printTable(List.of("ID", "NAME", "DISCOUNT", "START", "END", "STATE"), rows);
    }

    private void updateOffer() throws SQLException {
        int id = readId("Offer ID to update:");
        Offer existing = offerDAO.getOfferById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Offer not found.");
        }
        saveOffer(id);
    }

    private void saveOffer(int id) throws SQLException {
        while (true) {
            String name = input.readRequired("Offer name:");
            String description = input.readRequired("Offer description:");
            double discount = input.readDouble("Discount percentage (0-100):");
            LocalDate startDate = input.readDate("Start date (YYYY-MM-DD):");
            LocalDate endDate = input.readDate("End date (YYYY-MM-DD):");
            boolean active = input.readYesNo("Is this offer active?");

            try {
                Offer offer = new Offer(id, name, description, discount, startDate, endDate, active);
                if (id == 0) {
                    offerDAO.addOffer(offer);
                    ConsoleUI.success("Offer created. Offer ID: " + offer.getOfferId());
                } else {
                    offerDAO.updateOffer(offer);
                    ConsoleUI.success("Offer updated.");
                }
                return;
            } catch (IllegalArgumentException exception) {
                ConsoleUI.error(exception.getMessage());
                ConsoleUI.info("Please re-enter the offer details.");
            }
        }
    }

    private void deleteOffer() throws SQLException {
        int id = readId("Offer ID to delete:");
        Offer offer = offerDAO.getOfferById(id);
        if (offer == null) {
            throw new IllegalArgumentException("Offer not found.");
        }
        ConsoleUI.info("Offer: " + offer.getName() + " (" + offer.getDiscountPercentage() + "%)");
        if (!input.readYesNo("Delete this offer permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        offerDAO.deleteOffer(id);
        ConsoleUI.success("Offer deleted.");
    }
}
