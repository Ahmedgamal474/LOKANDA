package com.lokanda.hotel.tests;


import com.lokanda.hotel.DAO.GuestDAO;
import com.lokanda.hotel.model.person.Guest;

import java.sql.SQLException;
import java.util.List;

public class TestGuestDAO {

    public static void main(String[] args) {

        GuestDAO guestDAO = new GuestDAO();

        try {

            List<Guest> guests = guestDAO.getAllGuests();

            System.out.println("Total Guests: " + guests.size());

            if (guests.isEmpty()) {
                System.out.println("No guests found in database.");
                return;
            }

            for (Guest guest : guests) {

                System.out.println("\n----------------------");

                System.out.println("Guest ID: " + guest.getPersonId());
                System.out.println("Name: " + guest.getFirstName()
                        + " " + guest.getLastName());
                System.out.println("Email: " + guest.getEmail());
                System.out.println("Phone: " + guest.getPhone());
                System.out.println("National ID: " + guest.getNationalId());

                Guest guestById =
                        guestDAO.getGuestById(guest.getPersonId());

                if (guestById == null) {
                    throw new RuntimeException(
                            "Get Guest By ID failed!");
                }

                Guest guestByNationalId =
                        guestDAO.getGuestByNationalId(
                                guest.getNationalId());

                if (guestByNationalId == null) {
                    throw new RuntimeException(
                            "Get Guest By National ID failed!");
                }

                if (guestById.getPersonId()
                        != guestByNationalId.getPersonId()) {
                    throw new RuntimeException(
                            "Guest data mismatch!");
                }

                System.out.println("Get By ID: Passed");
                System.out.println("Get By National ID: Passed");
            }

            System.out.println("\nGuestDAO Test Passed!");

        } catch (SQLException e) {

            System.out.println("Database Error:");
            e.printStackTrace();

        } catch (RuntimeException e) {

            System.out.println("Test Failed:");
            e.printStackTrace();
        }
    }
}
