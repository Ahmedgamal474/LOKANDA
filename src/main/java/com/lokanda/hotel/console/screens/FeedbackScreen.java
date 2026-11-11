package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.FeedbackDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.console.model.BookingSummary;
import com.lokanda.hotel.console.service.ConsoleBookingRepository;
import com.lokanda.hotel.model.feedback.Feedback;

import java.sql.SQLException;

public final class FeedbackScreen extends BaseScreen {
    private final FeedbackDAO feedbackDAO = new FeedbackDAO();
    private final ConsoleBookingRepository bookingRepository = new ConsoleBookingRepository();

    public FeedbackScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Feedback Management");
            ConsoleUI.option("1", "Add feedback to a completed booking");
            ConsoleUI.option("2", "View feedback by booking ID");
            ConsoleUI.option("3", "Update feedback");
            ConsoleUI.option("4", "Delete feedback");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 4);
            switch (choice) {
                case 1 -> execute(this::addFeedback);
                case 2 -> execute(this::viewFeedback);
                case 3 -> execute(this::updateFeedback);
                case 4 -> execute(this::deleteFeedback);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void addFeedback() throws SQLException {
        int bookingId = readId("Booking ID:");
        BookingSummary booking = bookingRepository.findSummaryById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        if (!"COMPLETED".equals(booking.status())) {
            throw new IllegalStateException("Feedback is allowed only for completed bookings.");
        }
        if (feedbackDAO.getFeedbackByBookingId(bookingId) != null) {
            throw new IllegalStateException("This booking already has feedback.");
        }

        Feedback feedback = new Feedback();
        input.readValidated("Rating (1-5):", value -> {
            try {
                feedback.setRating(Integer.parseInt(value));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Rating must be a whole number from 1 to 5.");
            }
            return feedback.getRating();
        });
        input.readValidated("Comment:", value -> {
            feedback.setComment(value);
            return feedback.getComment();
        });
        feedback.setCreatedAt(java.time.LocalDateTime.now());
        if (feedbackDAO.addFeedback(bookingId, feedback)) {
            ConsoleUI.success("Feedback saved.");
        } else {
            ConsoleUI.warning("Feedback was not saved.");
        }
    }

    private void viewFeedback() throws SQLException {
        int bookingId = readId("Booking ID:");
        Feedback feedback = feedbackDAO.getFeedbackByBookingId(bookingId);
        if (feedback == null) {
            ConsoleUI.warning("No feedback found for this booking.");
            return;
        }
        printFeedback(feedback);
    }

    private void updateFeedback() throws SQLException {
        int bookingId = readId("Booking ID:");
        Feedback feedback = feedbackDAO.getFeedbackByBookingId(bookingId);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found.");
        }
        input.readValidated("New rating (1-5):", value -> {
            try {
                feedback.setRating(Integer.parseInt(value));
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Rating must be a whole number from 1 to 5.");
            }
            return feedback.getRating();
        });
        input.readValidated("New comment:", value -> {
            feedback.setComment(value);
            return feedback.getComment();
        });
        feedback.setCreatedAt(java.time.LocalDateTime.now());
        if (feedbackDAO.updateFeedback(feedback)) {
            ConsoleUI.success("Feedback updated.");
        } else {
            ConsoleUI.warning("Feedback was not updated.");
        }
    }

    private void deleteFeedback() throws SQLException {
        int id = readId("Feedback ID to delete:");
        if (!input.readYesNo("Delete this feedback permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        if (feedbackDAO.deleteFeedback(id)) {
            ConsoleUI.success("Feedback deleted.");
        } else {
            ConsoleUI.warning("Feedback ID not found.");
        }
    }

    private void printFeedback(Feedback feedback) {
        ConsoleUI.section("Feedback details");
        ConsoleUI.info("Feedback ID: " + feedback.getFeedbackId());
        ConsoleUI.info("Rating     : " + feedback.getRating() + "/5");
        ConsoleUI.info("Comment    : " + feedback.getComment());
        ConsoleUI.info("Created at : " + feedback.getCreatedAt());
    }
}
