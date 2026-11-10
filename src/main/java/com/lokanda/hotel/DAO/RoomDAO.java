package DAO;
import model.Room;
import model.RoomType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // =====================================
    // ADD ROOM
    // =====================================

    public boolean addRoom(Room room) throws SQLException {

        String sql = "INSERT INTO room (room_number, room_type) VALUES (?, ?)";


        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, room.getRoomNumber());
            statement.setString(2, room.getType().name());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {

                try (ResultSet keys = statement.getGeneratedKeys()) {

                    if (keys.next()) {
                        room.setRoomId(keys.getInt(1));
                    }
                }

                return true;
            }
        }

        return false;
    }


    // =====================================
    // GET ROOM BY ID
    // =====================================

    public Room getRoomById(int roomId) throws SQLException {

        String sql = """
                SELECT *
                FROM room
                WHERE room_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, roomId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return mapRoom(result);
                }
            }
        }

        return null;
    }


    // =====================================
    // GET ALL ROOMS
    // =====================================

    public List<Room> getAllRooms() throws SQLException {

        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT * FROM room";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                rooms.add(mapRoom(result));
            }
        }

        return rooms;
    }


    // =====================================
    // UPDATE ROOM
    // =====================================

    public boolean updateRoom(Room room) throws SQLException {

        String sql = """
                UPDATE room
                SET room_number = ?, room_type = ?
                WHERE room_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, room.getRoomNumber());
            statement.setString(2, room.getType().name());
            statement.setInt(3, room.getRoomId());

            return statement.executeUpdate() > 0;
        }
    }


    // =====================================
    // DELETE ROOM
    // =====================================

    public boolean deleteRoom(int roomId) throws SQLException {

        String sql = """
                DELETE FROM room
                WHERE room_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, roomId);

            return statement.executeUpdate() > 0;
        }
    }


    // =====================================
    // MAP RESULT TO ROOM OBJECT
    // =====================================

    private Room mapRoom(ResultSet result) throws SQLException {

        int roomId = result.getInt("room_id");
        String roomNumber = result.getString("room_number");

        RoomType roomType = RoomType.valueOf(
                result.getString("room_type")
        );

        return new Room(roomId, roomNumber, roomType);
    }
}
