package com.lokanda.hotel;


import com.lokanda.hotel.DAO.BookingDAO;
import com.lokanda.hotel.DAO.FeedbackDAO;
import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.feedback.Feedback;

import java.sql.SQLException;
import java.util.List;

public class TestFeedbackDAO {

    public static void main(String[] args) {

        BookingDAO bookingDAO = new BookingDAO();
        FeedbackDAO feedbackDAO = new FeedbackDAO();

        try {

            List<Booking> bookings = bookingDAO.getAllBookings();

            int totalFeedbacks = 0;

            for (Booking booking : bookings) {

                Feedback feedback =
                        feedbackDAO.getFeedbackByBookingId(
                                booking.getBookingId());

                if (feedback != null) {

                    totalFeedbacks++;

                    System.out.println("\n----------------------");

                    System.out.println("Booking ID: "
                            + booking.getBookingId());

                    System.out.println("Feedback ID: "
                            + feedback.getFeedbackId());

                    System.out.println("Rating: "
                            + feedback.getRating());

                    System.out.println("Comment: "
                            + feedback.getComment());

                    System.out.println("Created At: "
                            + feedback.getCreatedAt());

                    // Check feedback again
                    Feedback feedbackAgain =
                            feedbackDAO.getFeedbackByBookingId(
                                    booking.getBookingId());

                    if (feedbackAgain == null) {
                        throw new RuntimeException(
                                "Get Feedback By Booking ID failed!");
                    }

                    if (feedbackAgain.getFeedbackId()
                            != feedback.getFeedbackId()) {
                        throw new RuntimeException(
                                "Feedback ID mismatch!");
                    }

                    if (feedbackAgain.getRating()
                            != feedback.getRating()) {
                        throw new RuntimeException(
                                "Feedback rating mismatch!");
                    }

                    System.out.println("Get Feedback: Passed");

                } else {

                    System.out.println(
                            "No feedback for Booking ID: "
                                    + booking.getBookingId());
                }
            }

            System.out.println("\nTotal Feedbacks: "
                    + totalFeedbacks);

            System.out.println("FeedbackDAO Test Passed!");

        } catch (SQLException e) {

            System.out.println("Database Error:");
            e.printStackTrace();

        } catch (RuntimeException e) {

            System.out.println("Test Failed:");
            e.printStackTrace();
        }
    }
}

