package com.lokanda.hotel.console.service;

import com.lokanda.hotel.DAO.DatabaseConnection;
import com.lokanda.hotel.DAO.GuestDAO;
import com.lokanda.hotel.DAO.OfferDAO;
import com.lokanda.hotel.DAO.ReceptionistDAO;
import com.lokanda.hotel.DAO.RoomDAO;
import com.lokanda.hotel.console.model.BookingSummary;
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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Booking adapter used only by the standalone console.
 * It uses the actual `package_name` column from the supplied SQL schema and
 * delegates entity validation to the existing LOKANDA model classes.
 */
public final class ConsoleBookingRepository {
    private final GuestDAO guestDAO = new GuestDAO();
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final OfferDAO offerDAO = new OfferDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    public List<BookingSummary> findAllSummaries() throws SQLException {
        String sql = summarySql(false);
        List<BookingSummary> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                bookings.add(mapSummary(result));
            }
        }
        return bookings;
    }

    public BookingSummary findSummaryById(int bookingId) throws SQLException {
        String sql = summarySql(true);
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapSummary(result) : null;
            }
        }
    }

    public Booking findActiveBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM booking WHERE booking_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                BookingStatus status = BookingStatus.valueOf(result.getString("status"));
                if (status == BookingStatus.CANCELLED || status == BookingStatus.COMPLETED) {
                    return null;
                }

                Guest guest = guestDAO.getGuestById(result.getInt("guest_id"));
                Receptionist receptionist = receptionistDAO.getReceptionistById(
                        result.getInt("receptionist_id"));

                int offerId = result.getInt("offer_id");
                Offer offer = result.wasNull() ? null : offerDAO.getOfferById(offerId);

                Booking booking = new Booking(
                        result.getInt("booking_id"),
                        guest,
                        result.getDate("check_in").toLocalDate(),
                        result.getDate("check_out").toLocalDate(),
                        receptionist,
                        offer,
                        result.getBigDecimal("discount_amount"),
                        status,
                        result.getTimestamp("created_at").toLocalDateTime()
                );
                bookingItems(connection, booking);
                return booking;
            }
        }
    }

    public void create(Booking booking) throws SQLException {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null.");
        }
        if (booking.getItems().isEmpty()) {
            throw new IllegalArgumentException("Add at least one room to the booking.");
        }

        Offer offer = booking.getOffer();
        if (offer != null) {
            if (!offer.isValid(booking.getCheckIn())) {
                throw new IllegalArgumentException("The selected offer is not valid for this check-in date.");
            }
            booking.setDiscountAmount(offer.calculateDiscount(booking.calculateTotal()));
        } else {
            booking.setDiscountAmount(BigDecimal.ZERO);
        }
        booking.calculateFinalPrice();

        String bookingSql = """
                INSERT INTO booking (
                    guest_id, receptionist_id, offer_id, check_in, check_out,
                    discount_amount, status, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String itemSql = """
                INSERT INTO booking_item (booking_id, room_id, package_id, price_snapshot)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ensureRoomsAvailable(connection, booking.getItems(),
                        booking.getCheckIn(), booking.getCheckOut(), -1);

                int bookingId;
                try (PreparedStatement statement = connection.prepareStatement(
                        bookingSql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setInt(1, booking.getGuest().getPersonId());
                    statement.setInt(2, booking.getReceptionist().getPersonId());
                    if (offer == null) {
                        statement.setNull(3, Types.INTEGER);
                    } else {
                        statement.setInt(3, offer.getOfferId());
                    }
                    statement.setDate(4, java.sql.Date.valueOf(booking.getCheckIn()));
                    statement.setDate(5, java.sql.Date.valueOf(booking.getCheckOut()));
                    statement.setBigDecimal(6, booking.getDiscountAmount());
                    statement.setString(7, booking.getStatus().name());
                    statement.setTimestamp(8, Timestamp.valueOf(booking.getCreatedAt()));
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException("The database did not return a booking ID.");
                        }
                        bookingId = keys.getInt(1);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                    for (BookingItem item : booking.getItems()) {
                        statement.setInt(1, bookingId);
                        statement.setInt(2, item.getRoom().getRoomId());
                        statement.setInt(3, findPackageId(connection, item.getBookingPackage().getName()));
                        statement.setBigDecimal(4, item.getPriceSnapshot());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }

                connection.commit();
                booking.setBookingId(bookingId);
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void modifyDates(int bookingId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        Booking booking = findActiveBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Active booking was not found.");
        }

        // Booking.modify performs the existing model's date and state validation.
        booking.modify(checkIn, checkOut);

        String updateBooking = "UPDATE booking SET check_in = ?, check_out = ?, discount_amount = ? WHERE booking_id = ?";
        String updateItem = "UPDATE booking_item SET price_snapshot = ? WHERE booking_id = ? AND room_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                ensureRoomsAvailable(connection, booking.getItems(), checkIn, checkOut, bookingId);
                try (PreparedStatement statement = connection.prepareStatement(updateBooking)) {
                    statement.setDate(1, java.sql.Date.valueOf(checkIn));
                    statement.setDate(2, java.sql.Date.valueOf(checkOut));
                    statement.setBigDecimal(3, booking.getDiscountAmount());
                    statement.setInt(4, bookingId);
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException("Booking was not updated.");
                    }
                }
                try (PreparedStatement statement = connection.prepareStatement(updateItem)) {
                    for (BookingItem item : booking.getItems()) {
                        statement.setBigDecimal(1, item.getPriceSnapshot());
                        statement.setInt(2, bookingId);
                        statement.setInt(3, item.getRoom().getRoomId());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException | RuntimeException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public void changeStatus(int bookingId, BookingStatus newStatus) throws SQLException {
        if (newStatus == null) {
            throw new IllegalArgumentException("Booking status cannot be null.");
        }
        Booking booking = findActiveBookingById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Active booking was not found.");
        }

        switch (newStatus) {
            case CONFIRMED -> booking.confirm();
            case COMPLETED -> booking.complete();
            case CANCELLED -> booking.cancel();
            case PENDING -> throw new IllegalArgumentException("A booking cannot be moved back to PENDING.");
        }

        String sql = "UPDATE booking SET status = ? WHERE booking_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, booking.getStatus().name());
            statement.setInt(2, bookingId);
            if (statement.executeUpdate() == 0) {
                throw new SQLException("Booking status was not updated.");
            }
        }
    }

    private String summarySql(boolean byId) {
        String where = byId ? "WHERE b.booking_id = ?" : "";
        return """
                SELECT b.booking_id,
                       CONCAT(p.first_name, ' ', p.last_name) AS guest_name,
                       b.check_in,
                       b.check_out,
                       b.status,
                       COALESCE(SUM(bi.price_snapshot), 0) AS gross_total,
                       b.discount_amount
                FROM booking b
                JOIN guest g ON g.person_id = b.guest_id
                JOIN person p ON p.person_id = g.person_id
                LEFT JOIN booking_item bi ON bi.booking_id = b.booking_id
                """ + where + " GROUP BY b.booking_id, p.first_name, p.last_name, "
                + "b.check_in, b.check_out, b.status, b.discount_amount ORDER BY b.booking_id DESC";
    }

    private BookingSummary mapSummary(ResultSet result) throws SQLException {
        BigDecimal gross = result.getBigDecimal("gross_total");
        BigDecimal discount = result.getBigDecimal("discount_amount");
        BigDecimal finalTotal = gross.subtract(discount).max(BigDecimal.ZERO);
        return new BookingSummary(
                result.getInt("booking_id"),
                result.getString("guest_name"),
                result.getDate("check_in").toLocalDate(),
                result.getDate("check_out").toLocalDate(),
                result.getString("status"),
                gross,
                discount,
                finalTotal
        );
    }

    private void bookingItems(Connection connection, Booking booking) throws SQLException {
        String sql = """
                SELECT bi.item_id, bi.room_id, bi.price_snapshot, bp.package_name
                FROM booking_item bi
                JOIN booking_package bp ON bp.package_id = bi.package_id
                WHERE bi.booking_id = ?
                ORDER BY bi.item_id
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, booking.getBookingId());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Room room = roomDAO.getRoomById(result.getInt("room_id"));
                    BookingPackage bookingPackage = createPackage(result.getString("package_name"));
                    BookingItem item = new BookingItem(
                            result.getInt("item_id"),
                            room,
                            bookingPackage,
                            result.getBigDecimal("price_snapshot")
                    );
                    booking.addItem(item);
                }
            }
        }
    }

    private void ensureRoomsAvailable(Connection connection,
                                      List<BookingItem> items,
                                      LocalDate checkIn,
                                      LocalDate checkOut,
                                      int excludedBookingId) throws SQLException {
        Set<Integer> selectedRooms = new HashSet<>();
        String sql = """
                SELECT COUNT(*)
                FROM booking b
                JOIN booking_item bi ON bi.booking_id = b.booking_id
                WHERE bi.room_id = ?
                  AND b.status IN ('PENDING', 'CONFIRMED')
                  AND b.check_in < ?
                  AND b.check_out > ?
                  AND (? < 0 OR b.booking_id <> ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (BookingItem item : items) {
                int roomId = item.getRoom().getRoomId();
                if (!selectedRooms.add(roomId)) {
                    throw new IllegalArgumentException("The same room cannot be added twice.");
                }
                statement.setInt(1, roomId);
                statement.setDate(2, java.sql.Date.valueOf(checkOut));
                statement.setDate(3, java.sql.Date.valueOf(checkIn));
                statement.setInt(4, excludedBookingId);
                statement.setInt(5, excludedBookingId);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next() && result.getInt(1) > 0) {
                        throw new IllegalStateException("Room " + item.getRoom().getRoomNumber()
                                + " is already booked for these dates.");
                    }
                }
            }
        }
    }

    private int findPackageId(Connection connection, String packageName) throws SQLException {
        String sql = "SELECT package_id FROM booking_package WHERE package_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, packageName);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("package_id");
                }
            }
        }
        throw new SQLException("Package not found in database: " + packageName);
    }

    private BookingPackage createPackage(String packageName) throws SQLException {
        if (packageName == null) {
            throw new SQLException("Package name cannot be null.");
        }
        return switch (packageName.trim().toUpperCase()) {
            case "BREAKFAST ONLY", "BREAKFAST_ONLY" -> new BreakfastOnly();
            case "HALF BOARD", "HALF_BOARD" -> new HalfBoard();
            case "ALL INCLUSIVE", "ALL_INCLUSIVE" -> new AllInclusive();
            default -> throw new SQLException("Unknown booking package: " + packageName);
        };
    }
}
