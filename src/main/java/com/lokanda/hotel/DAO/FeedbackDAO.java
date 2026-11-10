
package com.lokanda.hotel.DAO;

import com.lokanda.hotel.model.feedback.Feedback;

import java.sql.*;

public class FeedbackDAO {

    public boolean addFeedback(int bookingId, Feedback feedback)
            throws SQLException {

        String sql = """
                INSERT INTO feedback (
                    booking_id, rating, comment, created_at
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, bookingId);
            statement.setInt(2, feedback.getRating());
            statement.setString(3, feedback.getComment());
            statement.setTimestamp(
                    4, Timestamp.valueOf(feedback.getCreatedAt())
            );

            if (statement.executeUpdate() > 0) {
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        feedback.setFeedbackId(keys.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    public Feedback getFeedbackByBookingId(int bookingId)
            throws SQLException {

        String sql = "SELECT * FROM feedback WHERE booking_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapFeedback(result);
                }
            }
        }

        return null;
    }

    public boolean updateFeedback(Feedback feedback) throws SQLException {

        String sql = """
                UPDATE feedback
                SET rating = ?, comment = ?, created_at = ?
                WHERE feedback_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, feedback.getRating());
            statement.setString(2, feedback.getComment());
            statement.setTimestamp(
                    3, Timestamp.valueOf(feedback.getCreatedAt())
            );
            statement.setInt(4, feedback.getFeedbackId());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteFeedback(int feedbackId) throws SQLException {

        String sql = "DELETE FROM feedback WHERE feedback_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, feedbackId);

            return statement.executeUpdate() > 0;
        }
    }

    private Feedback mapFeedback(ResultSet result) throws SQLException {

        Feedback feedback = new Feedback(
                result.getInt("rating"),
                result.getString("comment")
        );

        feedback.setFeedbackId(result.getInt("feedback_id"));
        feedback.setCreatedAt(
                result.getTimestamp("created_at").toLocalDateTime()
        );

        return feedback;
    }
}
