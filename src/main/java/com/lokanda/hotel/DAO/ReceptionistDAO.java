package com.lokanda.hotel.DAO;
import com.lokanda.hotel.model.person.Receptionist;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ReceptionistDAO {

    // Add Receptionist
    public void addReceptionist(Receptionist receptionist) throws SQLException {

        String personSQL = """
                INSERT INTO person (first_name, last_name, email, phone)
                VALUES (?, ?, ?, ?)
                """;

        String receptionistSQL = """
                INSERT INTO receptionist (person_id, employee_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int personId;

                try (PreparedStatement statement =
                             connection.prepareStatement(personSQL, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, receptionist.getFirstName());
                    statement.setString(2, receptionist.getLastName());
                    statement.setString(3, receptionist.getEmail());
                    statement.setString(4, receptionist.getPhone());

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (keys.next()) {
                            personId = keys.getInt(1);
                        } else {
                            throw new SQLException("Failed to get generated person ID");
                        }
                    }
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(receptionistSQL)) {

                    statement.setInt(1, personId);
                    statement.setString(2, receptionist.getEmployeeId());

                    statement.executeUpdate();
                }

                connection.commit();
                receptionist.setPersonId(personId);

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Get Receptionist By ID
    public Receptionist getReceptionistById(int personId) throws SQLException {

        String sql = """
                SELECT p.*, r.employee_id
                FROM person p
                JOIN receptionist r ON p.person_id = r.person_id
                WHERE p.person_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, personId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReceptionist(result);
                }
            }
        }

        return null;
    }

    // Get Receptionist By Employee ID
    public Receptionist getReceptionistByEmployeeId(String employeeId) throws SQLException {

        String sql = """
                SELECT p.*, r.employee_id
                FROM person p
                JOIN receptionist r ON p.person_id = r.person_id
                WHERE r.employee_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, employeeId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReceptionist(result);
                }
            }
        }

        return null;
    }

    // Get All Receptionists
    public List<Receptionist> getAllReceptionists() throws SQLException {

        List<Receptionist> receptionists = new ArrayList<>();

        String sql = """
                SELECT p.*, r.employee_id
                FROM person p
                JOIN receptionist r ON p.person_id = r.person_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                receptionists.add(mapReceptionist(result));
            }
        }

        return receptionists;
    }

    // Update Receptionist
    public void updateReceptionist(Receptionist receptionist) throws SQLException {

        String personSQL = """
                UPDATE person
                SET first_name = ?, last_name = ?, email = ?, phone = ?
                WHERE person_id = ?
                """;

        String receptionistSQL = """
                UPDATE receptionist
                SET employee_id = ?
                WHERE person_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                try (PreparedStatement statement =
                             connection.prepareStatement(personSQL)) {

                    statement.setString(1, receptionist.getFirstName());
                    statement.setString(2, receptionist.getLastName());
                    statement.setString(3, receptionist.getEmail());
                    statement.setString(4, receptionist.getPhone());
                    statement.setInt(5, receptionist.getPersonId());

                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                             connection.prepareStatement(receptionistSQL)) {

                    statement.setString(1, receptionist.getEmployeeId());
                    statement.setInt(2, receptionist.getPersonId());

                    statement.executeUpdate();
                }

                connection.commit();

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    // Delete Receptionist
    public void deleteReceptionist(int personId) throws SQLException {

        String sql = "DELETE FROM person WHERE person_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, personId);
            statement.executeUpdate();
        }
    }

    // Map ResultSet To Receptionist
    private Receptionist mapReceptionist(ResultSet result) throws SQLException {

        return new Receptionist(
                result.getInt("person_id"),
                result.getString("first_name"),
                result.getString("last_name"),
                result.getString("email"),
                result.getString("phone"),
                result.getString("employee_id")
        );
    }
}
