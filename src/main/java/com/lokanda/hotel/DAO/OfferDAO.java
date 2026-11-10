package com.lokanda.hotel.DAO;
import com.lokanda.hotel.model.offer.Offer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class OfferDAO {
    // Add Offer
    public void addOffer(Offer offer) throws SQLException {

        String sql = """
                INSERT INTO offer
                (name, description, discount_percentage, start_date, end_date, active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, offer.getName());
            statement.setString(2, offer.getDescription());
            statement.setDouble(3, offer.getDiscountPercentage());
            statement.setDate(4, Date.valueOf(offer.getStartDate()));
            statement.setDate(5, Date.valueOf(offer.getEndDate()));
            statement.setBoolean(6, offer.isActive());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    offer.setOfferId(keys.getInt(1));
                } else {
                    throw new SQLException("Failed to get generated offer ID");
                }
            }
        }
    }

    // Get Offer By ID
    public Offer getOfferById(int offerId) throws SQLException {

        String sql = "SELECT * FROM offer WHERE offer_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, offerId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapOffer(result);
                }
            }
        }

        return null;
    }

    // Get All Offers
    public List<Offer> getAllOffers() throws SQLException {

        List<Offer> offers = new ArrayList<>();

        String sql = "SELECT * FROM offer";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                offers.add(mapOffer(result));
            }
        }

        return offers;
    }

    // Get Active Offers
    public List<Offer> getActiveOffers() throws SQLException {

        List<Offer> offers = new ArrayList<>();

        String sql = """
                SELECT * FROM offer
                WHERE active = TRUE
                AND CURDATE() BETWEEN start_date AND end_date
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                offers.add(mapOffer(result));
            }
        }

        return offers;
    }

    // Update Offer
    public void updateOffer(Offer offer) throws SQLException {

        String sql = """
                UPDATE offer
                SET name = ?,
                    description = ?,
                    discount_percentage = ?,
                    start_date = ?,
                    end_date = ?,
                    active = ?
                WHERE offer_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, offer.getName());
            statement.setString(2, offer.getDescription());
            statement.setDouble(3, offer.getDiscountPercentage());
            statement.setDate(4, Date.valueOf(offer.getStartDate()));
            statement.setDate(5, Date.valueOf(offer.getEndDate()));
            statement.setBoolean(6, offer.isActive());
            statement.setInt(7, offer.getOfferId());

            statement.executeUpdate();
        }
    }

    // Delete Offer
    public void deleteOffer(int offerId) throws SQLException {

        String sql = "DELETE FROM offer WHERE offer_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, offerId);
            statement.executeUpdate();
        }
    }

    // Map ResultSet To Offer
    private Offer mapOffer(ResultSet result) throws SQLException {

        return new Offer(
                result.getInt("offer_id"),
                result.getString("name"),
                result.getString("description"),
                result.getDouble("discount_percentage"),
                result.getDate("start_date").toLocalDate(),
                result.getDate("end_date").toLocalDate(),
                result.getBoolean("active")
        );
    }
}
