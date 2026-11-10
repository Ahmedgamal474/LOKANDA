
package com.lokanda.hotel.DAO;

import com.lokanda.hotel.model.payment.Payment;
import com.lokanda.hotel.model.payment.PaymentMethod;
import com.lokanda.hotel.model.payment.PaymentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public boolean addPayment(int bookingId, Payment payment) throws SQLException {

        String sql = """
                INSERT INTO payment (
                    booking_id, amount, payment_date, method, status
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, bookingId);
            statement.setBigDecimal(2, payment.getAmount());
            statement.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            statement.setString(4, payment.getMethod().name());
            statement.setString(5, payment.getStatus().name());

            if (statement.executeUpdate() > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        payment.setPaymentId(keys.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    public Payment getPaymentById(int paymentId) throws SQLException {

        String sql = "SELECT * FROM payment WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, paymentId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapPayment(result);
                }
            }
        }

        return null;
    }

    public List<Payment> getPaymentsByBookingId(int bookingId) throws SQLException {

        List<Payment> payments = new ArrayList<>();

        String sql = "SELECT * FROM payment WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    payments.add(mapPayment(result));
                }
            }
        }

        return payments;
    }

    public boolean updatePayment(Payment payment) throws SQLException {

        String sql = """
                UPDATE payment
                SET amount = ?, payment_date = ?, method = ?, status = ?
                WHERE payment_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, payment.getAmount());
            statement.setTimestamp(2, Timestamp.valueOf(payment.getPaymentDate()));
            statement.setString(3, payment.getMethod().name());
            statement.setString(4, payment.getStatus().name());
            statement.setInt(5, payment.getPaymentId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePaymentStatus(int paymentId, PaymentStatus status)
            throws SQLException {

        String sql = "UPDATE payment SET status = ? WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setInt(2, paymentId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePayment(int paymentId) throws SQLException {

        String sql = "DELETE FROM payment WHERE payment_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, paymentId);

            return statement.executeUpdate() > 0;
        }
    }

    private Payment mapPayment(ResultSet result) throws SQLException {

        Payment payment = new Payment(
                result.getBigDecimal("amount"),
                PaymentMethod.valueOf(result.getString("method"))
        );

        payment.setPaymentId(result.getInt("payment_id"));
        payment.setPaymentDate(
                result.getTimestamp("payment_date").toLocalDateTime()
        );
        payment.setStatus(
                PaymentStatus.valueOf(result.getString("status"))
        );

        return payment;
    }
}
