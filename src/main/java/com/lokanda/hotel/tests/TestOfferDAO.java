package com.lokanda.hotel.tests;


import com.lokanda.hotel.DAO.OfferDAO;
import com.lokanda.hotel.model.offer.Offer;

import java.sql.SQLException;
import java.util.List;

public class TestOfferDAO {

    public static void main(String[] args) {

        OfferDAO offerDAO = new OfferDAO();

        try {

            // Test Get All Offers
            List<Offer> offers = offerDAO.getAllOffers();

            System.out.println("Total Offers: " + offers.size());

            for (Offer offer : offers) {

                System.out.println("\n----------------------");

                System.out.println("Offer ID: " + offer.getOfferId());
                System.out.println("Name: " + offer.getName());
                System.out.println("Description: "
                        + offer.getDescription());

                System.out.println("Discount: "
                        + offer.getDiscountPercentage() + "%");

                System.out.println("Start Date: "
                        + offer.getStartDate());

                System.out.println("End Date: "
                        + offer.getEndDate());

                System.out.println("Active: "
                        + offer.isActive());

                // Test Get Offer By ID
                Offer offerById =
                        offerDAO.getOfferById(offer.getOfferId());

                if (offerById == null) {
                    throw new RuntimeException(
                            "Get Offer By ID failed!");
                }

                if (offerById.getOfferId() != offer.getOfferId()) {
                    throw new RuntimeException(
                            "Offer ID mismatch!");
                }

                System.out.println("Get By ID: Passed");
            }

            // Test Get Active Offers
            List<Offer> activeOffers =
                    offerDAO.getActiveOffers();

            System.out.println("\n----------------------");
            System.out.println("Active Offers: "
                    + activeOffers.size());

            for (Offer offer : activeOffers) {

                System.out.println(
                        offer.getName() + " - "
                                + offer.getDiscountPercentage() + "%"
                );
            }

            System.out.println("\nOfferDAO Test Passed!");

        } catch (SQLException e) {

            System.out.println("Database Error:");
            e.printStackTrace();

        } catch (RuntimeException e) {

            System.out.println("Test Failed:");
            e.printStackTrace();
        }
    }
}
