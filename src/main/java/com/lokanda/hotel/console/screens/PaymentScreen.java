package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.PaymentDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.console.model.BookingSummary;
import com.lokanda.hotel.console.service.ConsoleBookingRepository;
import com.lokanda.hotel.model.payment.Payment;
import com.lokanda.hotel.model.payment.PaymentMethod;
import com.lokanda.hotel.model.payment.PaymentStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class PaymentScreen extends BaseScreen {
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ConsoleBookingRepository bookingRepository = new ConsoleBookingRepository();

    public PaymentScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Payment Management");
            ConsoleUI.option("1", "Record payment");
            ConsoleUI.option("2", "View payments for a booking");
            ConsoleUI.option("3", "Update payment status");
            ConsoleUI.option("4", "Delete payment");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 4);
            switch (choice) {
                case 1 -> execute(this::addPayment);
                case 2 -> execute(this::listPayments);
                case 3 -> execute(this::updatePaymentStatus);
                case 4 -> execute(this::deletePayment);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void addPayment() throws SQLException {
        int bookingId = readId("Booking ID:");
        BookingSummary booking = bookingRepository.findSummaryById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        if ("CANCELLED".equals(booking.status())) {
            throw new IllegalArgumentException("Cannot record payment for a cancelled booking.");
        }

        BigDecimal amount = input.readValidated("Payment amount:", value -> {
            BigDecimal parsed;
            try {
                parsed = new BigDecimal(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Enter a valid payment amount.");
            }
            Payment probe = new Payment(parsed, PaymentMethod.CASH);
            return probe.getAmount();
        });
        PaymentMethod method = input.readEnum("Payment method (CASH / CREDIT_CARD / DEBIT_CARD):", PaymentMethod.class);
        Payment payment = new Payment(amount, method);
        if (!payment.processPayment()) {
            throw new IllegalStateException("Payment could not be processed.");
        }
        if (paymentDAO.addPayment(bookingId, payment)) {
            ConsoleUI.success("Payment recorded. Payment ID: " + payment.getPaymentId());
            ConsoleUI.info("Payment status: " + payment.getStatus());
        } else {
            ConsoleUI.warning("Payment was not saved.");
        }
    }

    private void listPayments() throws SQLException {
        int bookingId = readId("Booking ID:");
        if (bookingRepository.findSummaryById(bookingId) == null) {
            throw new IllegalArgumentException("Booking not found.");
        }
        List<List<String>> rows = new ArrayList<>();
        paymentDAO.getPaymentsByBookingId(bookingId).forEach(payment -> rows.add(List.of(
                String.valueOf(payment.getPaymentId()),
                payment.getAmount().toPlainString(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getPaymentDate().toString()
        )));
        ConsoleUI.printTable(List.of("PAYMENT ID", "AMOUNT", "METHOD", "STATUS", "DATE"), rows);
    }

    private void updatePaymentStatus() throws SQLException {
        int id = readId("Payment ID:");
        Payment payment = paymentDAO.getPaymentById(id);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found.");
        }
        PaymentStatus status = input.readEnum("New status (PENDING / COMPLETED / FAILED):", PaymentStatus.class);
        payment.setStatus(status);
        if (paymentDAO.updatePayment(payment)) {
            ConsoleUI.success("Payment status updated.");
        } else {
            ConsoleUI.warning("Payment was not updated.");
        }
    }

    private void deletePayment() throws SQLException {
        int id = readId("Payment ID to delete:");
        Payment payment = paymentDAO.getPaymentById(id);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found.");
        }
        ConsoleUI.info("Payment: " + payment);
        if (!input.readYesNo("Delete this payment permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        if (paymentDAO.deletePayment(id)) {
            ConsoleUI.success("Payment deleted.");
        } else {
            ConsoleUI.warning("Payment was not deleted.");
        }
    }
}
