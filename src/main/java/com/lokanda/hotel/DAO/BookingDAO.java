
package com.lokanda.hotel.DAO;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.booking.BookingItem;
import com.lokanda.hotel.model.booking.BookingStatus;

import com.lokanda.hotel.model.bookingpackage.AllInclusive;
import com.lokanda.hotel.model.bookingpackage.BookingPackage;
import com.lokanda.hotel.model.bookingpackage.BreakfastOnly;
import com.lokanda.hotel.model.bookingpackage.HalfBoard;

import com.lokanda.hotel.model.offer.Offer;
import com.lokanda.hotel.model.person.Guest;
import com.lokanda.hotel.model.person.Receptionist;
import com.lokanda.hotel.model.room.Room;

public class BookingDAO {

    private final GuestDAO guestDAO = new GuestDAO();
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final OfferDAO offerDAO = new OfferDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    // ADD BOOKING

    public boolean addBooking(Booking booking) throws SQLException {

        String bookingSQL = """
                INSERT INTO booking (
                    guest_id,
                    receptionist_id,
                    offer_id,
                    check_in,
                    check_out,
                    discount_amount,
                    status,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String itemSQL = """
                INSERT INTO booking_item (
                    booking_id,
                    room_id,
                    package_id,
                    price_snapshot
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                int bookingId;

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     bookingSQL,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, booking.getGuest().getPersonId());

                    statement.setInt(2, booking.getReceptionist().getPersonId());

                    if (booking.getOffer() != null) {
                        statement.setInt(3, booking.getOffer().getOfferId());
                    } else {
                        statement.setNull(3, Types.INTEGER);
                    }

                    statement.setDate(4, Date.valueOf(booking.getCheckIn()));

                    statement.setDate(5, Date.valueOf(booking.getCheckOut()));

                    statement.setBigDecimal(6, booking.getDiscountAmount());

                    statement.setString(7, booking.getStatus().name());

                    statement.setTimestamp(
                            8,
                            Timestamp.valueOf(booking.getCreatedAt())
                    );

                    statement.executeUpdate();

                    try (ResultSet keys = statement.getGeneratedKeys()) {

                        if (keys.next()) {
                            bookingId = keys.getInt(1);
                            booking.setBookingId(bookingId);
                        } else {
                            throw new SQLException("Failed to get booking ID.");
                        }
                    }
                }

                // Insert booking items

                try (PreparedStatement itemStatement =
                             connection.prepareStatement(itemSQL)) {

                    for (BookingItem item : booking.getItems()) {

                        itemStatement.setInt(1, bookingId);

                        itemStatement.setInt(
                                2,
                                item.getRoom().getRoomId()
                        );

                        int packageId = getPackageId(
                                connection,
                                item.getBookingPackage().getName()
                        );

                        itemStatement.setInt(3, packageId);

                        itemStatement.setBigDecimal(
                                4,
                                item.getPriceSnapshot()
                        );

                        itemStatement.addBatch();
                    }

                    itemStatement.executeBatch();
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

    // GET PACKAGE ID BY NAME

    private int getPackageId(
            Connection connection,
            String packageName
    ) throws SQLException {

        String sql = """
                SELECT package_id
                FROM booking_package
                WHERE name = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, toDatabasePackageName(packageName));

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt("package_id");
                }
            }
        }

        throw new SQLException("Package not found: " + packageName);
    }

    // UPDATE BOOKING

    public boolean updateBooking(Booking booking) throws SQLException {

        String sql = """
                UPDATE booking
                SET offer_id = ?,
                    check_in = ?,
                    check_out = ?,
                    discount_amount = ?,
                    status = ?
                WHERE booking_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (booking.getOffer() != null) {
                statement.setInt(1, booking.getOffer().getOfferId());
            } else {
                statement.setNull(1, Types.INTEGER);
            }

            statement.setDate(2, Date.valueOf(booking.getCheckIn()));
            statement.setDate(3, Date.valueOf(booking.getCheckOut()));

            statement.setBigDecimal(4, booking.getDiscountAmount());

            statement.setString(5, booking.getStatus().name());

            statement.setInt(6, booking.getBookingId());

            return statement.executeUpdate() > 0;
        }
    }

    // DELETE BOOKING

    public boolean deleteBooking(int bookingId) throws SQLException {

        String sql = """
                DELETE FROM booking
                WHERE booking_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            return statement.executeUpdate() > 0;
        }
    }

    // UPDATE BOOKING STATUS

    public boolean updateBookingStatus(
            int bookingId,
            String status
    ) throws SQLException {

        String sql = """
                UPDATE booking
                SET status = ?
                WHERE booking_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, bookingId);

            return statement.executeUpdate() > 0;
        }
    }

    // GET BOOKING BY ID

    public Booking getBookingById(int bookingId) throws SQLException {

        String sql = """
                SELECT *
                FROM booking
                WHERE booking_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    Booking booking = mapBooking(result);

                    booking.setItems(getBookingItems(bookingId));

                    return booking;
                }
            }
        }

        return null;
    }

    // GET ALL BOOKINGS

    public List<Booking> getAllBookings() throws SQLException {

        List<Booking> bookings = new ArrayList<>();

        String sql = "SELECT * FROM booking";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Booking booking = mapBooking(result);

                booking.setItems(
                        getBookingItems(booking.getBookingId())
                );

                bookings.add(booking);
            }
        }

        return bookings;
    }

    // GET BOOKING ITEMS

    private List<BookingItem> getBookingItems(int bookingId)
            throws SQLException {

        List<BookingItem> items = new ArrayList<>();

        String sql = """
                SELECT bi.*, bp.name
                FROM booking_item bi
                JOIN booking_package bp
                ON bi.package_id = bp.package_id
                WHERE bi.booking_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    int itemId = result.getInt("item_id");

                    int roomId = result.getInt("room_id");

                    String packageName = result.getString("name");

                    BigDecimal priceSnapshot =
                            result.getBigDecimal("price_snapshot");

                    Room room = roomDAO.getRoomById(roomId);

                    BookingPackage bookingPackage =
                            createBookingPackage(packageName);

                    BookingItem item = new BookingItem(
                            itemId,
                            room,
                            bookingPackage,
                            priceSnapshot
                    );

                    items.add(item);
                }
            }
        }

        return items;
    }

    // CREATE BOOKING PACKAGE

    private BookingPackage createBookingPackage(String packageName)
            throws SQLException {

        if (packageName == null) {
            throw new SQLException("Package name cannot be null");
        }

        switch (toDatabasePackageName(packageName)) {

            case "BREAKFAST_ONLY":
                return new BreakfastOnly();

            case "HALF_BOARD":
                return new HalfBoard();

            case "ALL_INCLUSIVE":
                return new AllInclusive();

            default:
                throw new SQLException(
                        "Unknown booking package: " + packageName
                );
        }
    }

    // CONVERT PACKAGE NAME

    private String toDatabasePackageName(String packageName)
            throws SQLException {

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
                throw new SQLException(
                        "Unknown package: " + packageName
                );
        }
    }

    // MAP RESULTSET TO BOOKING

    private Booking mapBooking(ResultSet result) throws SQLException {

        int bookingId = result.getInt("booking_id");

        Guest guest = guestDAO.getGuestById(
                result.getInt("guest_id")
        );

        Receptionist receptionist =
                receptionistDAO.getReceptionistById(
                        result.getInt("receptionist_id")
                );

        int offerId = result.getInt("offer_id");

        Offer offer = null;

        if (!result.wasNull()) {
            offer = offerDAO.getOfferById(offerId);
        }

        return new Booking(
                bookingId,
                guest,
                result.getDate("check_in").toLocalDate(),
                result.getDate("check_out").toLocalDate(),
                receptionist,
                offer,
                result.getBigDecimal("discount_amount"),
                BookingStatus.valueOf(result.getString("status")),
                result.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
