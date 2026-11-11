
package com.lokanda.hotel.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lokanda.hotel.model.bookingpackage.BookingPackage;
import com.lokanda.hotel.model.bookingpackage.BreakfastOnly;
import com.lokanda.hotel.model.bookingpackage.HalfBoard;
import com.lokanda.hotel.model.bookingpackage.AllInclusive;

public class BookingPackageDAO {

    public int getPackageIdByName(String packageName) throws SQLException {

        String sql = "SELECT package_id FROM booking_package WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, toDatabaseName(packageName));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("package_id");
                }
            }
        }

        throw new SQLException("Package not found: " + packageName);
    }

    public BookingPackage getPackageById(int packageId) throws SQLException {

        String sql = "SELECT name FROM booking_package WHERE package_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, packageId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createPackage(resultSet.getString("name"));
                }
            }
        }

        return null;
    }

    public List<BookingPackage> getAllPackages() throws SQLException {

        List<BookingPackage> packages = new ArrayList<>();

        String sql = "SELECT name FROM booking_package ORDER BY package_id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                packages.add(createPackage(resultSet.getString("name")));
            }
        }

        return packages;
    }

    public boolean packageExists(String packageName) throws SQLException {

        String sql = "SELECT 1 FROM booking_package WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, toDatabaseName(packageName));

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private String toDatabaseName(String packageName) throws SQLException {

        if (packageName == null) {
            throw new SQLException("Package name cannot be null");
        }

        switch (packageName.trim().toUpperCase().replace(' ', '_')) {
            case "BREAKFAST_ONLY":
                return "BREAKFAST_ONLY";

            case "HALF_BOARD":
                return "HALF_BOARD";

            case "ALL_INCLUSIVE":
                return "ALL_INCLUSIVE";

            default:
                throw new SQLException("Unknown package: " + packageName);
        }
    }

    private BookingPackage createPackage(String packageName) throws SQLException {

        if (packageName == null) {
            throw new SQLException("Package name cannot be null");
        }

        switch (packageName) {
            case "BREAKFAST_ONLY":
                return new BreakfastOnly();

            case "HALF_BOARD":
                return new HalfBoard();

            case "ALL_INCLUSIVE":
                return new AllInclusive();

            default:
                throw new SQLException("Unknown package: " + packageName);
        }
    }
}
