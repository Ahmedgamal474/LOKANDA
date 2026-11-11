package com.lokanda.hotel.tests;


import com.lokanda.hotel.DAO.PaymentDAO;
import com.lokanda.hotel.model.payment.Payment;

import java.sql.SQLException;
import java.util.List;

public class TestPaymentDAO {

    public static void main(String[] args) {

        PaymentDAO paymentDAO = new PaymentDAO();

        try {

            // Get all payments using existing bookings
            com.lokanda.hotel.DAO.BookingDAO bookingDAO =
                    new com.lokanda.hotel.DAO.BookingDAO();

            List<com.lokanda.hotel.model.booking.Booking> bookings =
                    bookingDAO.getAllBookings();

            int totalPayments = 0;

            for (com.lokanda.hotel.model.booking.Booking booking : bookings) {

                List<Payment> payments =
                        paymentDAO.getPaymentsByBookingId(
                                booking.getBookingId());

                for (Payment payment : payments) {

                    totalPayments++;

                    System.out.println("\n----------------------");

                    System.out.println("Payment ID: "
                            + payment.getPaymentId());

                    System.out.println("Booking ID: "
                            + booking.getBookingId());

                    System.out.println("Amount: "
                            + payment.getAmount());

                    System.out.println("Payment Date: "
                            + payment.getPaymentDate());

                    System.out.println("Method: "
                            + payment.getMethod());

                    System.out.println("Status: "
                            + payment.getStatus());

                    // Test Get Payment By ID
                    Payment paymentById =
                            paymentDAO.getPaymentById(
                                    payment.getPaymentId());

                    if (paymentById == null) {
                        throw new RuntimeException(
                                "Get Payment By ID failed!");
                    }

                    if (paymentById.getPaymentId()
                            != payment.getPaymentId()) {
                        throw new RuntimeException(
                                "Payment ID mismatch!");
                    }

                    if (paymentById.getAmount().compareTo(
                            payment.getAmount()) != 0) {
                        throw new RuntimeException(
                                "Payment amount mismatch!");
                    }

                    System.out.println("Get By ID: Passed");
                }
            }

            System.out.println("\nTotal Payments: " + totalPayments);

            System.out.println("PaymentDAO Test Passed!");

        } catch (SQLException e) {

            System.out.println("Database Error:");
            e.printStackTrace();

        } catch (RuntimeException e) {

            System.out.println("Test Failed:");
            e.printStackTrace();
        }
    }
}
