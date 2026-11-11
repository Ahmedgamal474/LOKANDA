package com.lokanda.hotel;

import java.util.List;

import com.lokanda.hotel.DAO.BookingDAO;
import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.booking.BookingItem;

public class TestBookingDAO {

    public static void main(String[] args) {

        try {

            BookingDAO bookingDAO = new BookingDAO();

            List<Booking> bookings = bookingDAO.getAllBookings();

            System.out.println("Bookings Retrieved Successfully!");
            System.out.println("Total Bookings: " + bookings.size());

            System.out.println("-----------------------------");

            for (Booking booking : bookings) {

                System.out.println("Booking ID: " + booking.getBookingId());

                System.out.println(
                        "Guest: " +
                                booking.getGuest().getFirstName() + " " +
                                booking.getGuest().getLastName()
                );

                System.out.println(
                        "Receptionist: " +
                                booking.getReceptionist().getFirstName() + " " +
                                booking.getReceptionist().getLastName()
                );

                System.out.println("Check In: " + booking.getCheckIn());
                System.out.println("Check Out: " + booking.getCheckOut());

                System.out.println("Status: " + booking.getStatus());

                System.out.println(
                        "Discount: " + booking.getDiscountAmount()
                );

                System.out.println("Items:");

                for (BookingItem item : booking.getItems()) {

                    System.out.println(
                            "Room ID: " + item.getRoom().getRoomId()
                    );

                    System.out.println(
                            "Package: " + item.getBookingPackage().getName()
                    );

                    System.out.println(
                            "Price: " + item.getPriceSnapshot()
                    );

                    System.out.println("-----------------------------");
                }

                System.out.println("=============================");
            }

            System.out.println("BookingDAO Test Passed!");

        } catch (Exception e) {

            System.out.println("BookingDAO Test Failed!");
            e.printStackTrace();
        }
    }
}
