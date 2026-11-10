package com.lokanda.hotel.DAO;
import com.lokanda.hotel.model.person.Guest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class GuestDAO {
    // =====================================
    // ADD GUEST
    // =====================================

    public boolean addGuest(Guest guest) throws SQLException {

        String personSQL = """
                INSERT INTO person (
                    first_name,
                    last_name,
                    email,
                    phone
                )
                VALUES (?, ?, ?, ?)
                """;

        String guestSQL = """
                INSERT INTO guest (
                    person_id,
                    national_id
                )
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int personId;

                // Insert into person table
                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     personSQL,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    statement.setString(1, guest.getFirstName());
                    statement.setString(2, guest.getLastName());
                    statement.setString(3, guest.getEmail());

                    if (guest.getPhone() != null) {
                        statement.setString(4, guest.getPhone());
                    } else {
                        statement.setNull(4, Types.VARCHAR);
                    }

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {

                        if (keys.next()) {
                            personId = keys.getInt(1);
                        } else {
                            throw new SQLException("Failed to generate person ID");
                        }
                    }
                }

                // Insert into guest table
                try (PreparedStatement statement =
                             connection.prepareStatement(guestSQL)) {

                    statement.setInt(1, personId);
                    statement.setString(2, guest.getNationalId());

                    statement.executeUpdate();
                }

                connection.commit();

                guest.setPersonId(personId);

                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }


    // =====================================
    // GET GUEST BY ID
    // =====================================

    public Guest getGuestById(int personId) throws SQLException {

        String sql = """
                SELECT p.*, g.national_id
                FROM person p
                JOIN guest g ON p.person_id = g.person_id
                WHERE p.person_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, personId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapGuest(result);
                }
            }
        }

        return null;
    }


    // =====================================
    // GET GUEST BY NATIONAL ID
    // =====================================

    public Guest getGuestByNationalId(String nationalId) throws SQLException {

        String sql = """
                SELECT p.*, g.national_id
                FROM person p
                JOIN guest g ON p.person_id = g.person_id
                WHERE g.national_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nationalId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapGuest(result);
                }
            }
        }

        return null;
    }


    // =====================================
    // GET ALL GUESTS
    // =====================================

    public List<Guest> getAllGuests() throws SQLException {

        List<Guest> guests = new ArrayList<>();

        String sql = """
                SELECT p.*, g.national_id
                FROM person p
                JOIN guest g ON p.person_id = g.person_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                guests.add(mapGuest(result));
            }
        }

        return guests;
    }


    // =====================================
    // UPDATE GUEST
    // =====================================

    public boolean updateGuest(Guest guest) throws SQLException {

        String personSQL = """
                UPDATE person
                SET first_name = ?,
                    last_name = ?,
                    email = ?,
                    phone = ?
                WHERE person_id = ?
                """;

        String guestSQL = """
                UPDATE guest
                SET national_id = ?
                WHERE person_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int personRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(personSQL)) {

                    statement.setString(1, guest.getFirstName());
                    statement.setString(2, guest.getLastName());
                    statement.setString(3, guest.getEmail());

                    if (guest.getPhone() != null) {
                        statement.setString(4, guest.getPhone());
                    } else {
                        statement.setNull(4, Types.VARCHAR);
                    }

                    statement.setInt(5, guest.getPersonId());

                    personRows = statement.executeUpdate();
                }

                int guestRows;

                try (PreparedStatement statement =
                             connection.prepareStatement(guestSQL)) {

                    statement.setString(1, guest.getNationalId());
                    statement.setInt(2, guest.getPersonId());

                    guestRows = statement.executeUpdate();
                }

                if (personRows == 0 || guestRows == 0) {
                    connection.rollback();
                    return false;
                }

                connection.commit();

                return true;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);
            }
        }
    }


    // =====================================
    // DELETE GUEST
    // =====================================

    public boolean deleteGuest(int personId) throws SQLException {

        String sql = """
                DELETE FROM person
                WHERE person_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, personId);

            return statement.executeUpdate() > 0;
        }
    }


    // =====================================
    // MAP RESULT TO GUEST OBJECT
    // =====================================

    private Guest mapGuest(ResultSet result) throws SQLException {

        return new Guest(
                result.getInt("person_id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("phone"),
                result.getString("national_id")
        );
    }
}
