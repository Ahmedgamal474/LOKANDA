package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.GuestDAO;
import com.lokanda.hotel.DAO.OfferDAO;
import com.lokanda.hotel.DAO.ReceptionistDAO;
import com.lokanda.hotel.DAO.RoomDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.console.model.BookingSummary;
import com.lokanda.hotel.console.service.ConsoleBookingRepository;
import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.booking.BookingItem;
import com.lokanda.hotel.model.booking.BookingStatus;
import com.lokanda.hotel.model.booking.DatesValidator;
import com.lokanda.hotel.model.bookingpackage.AllInclusive;
import com.lokanda.hotel.model.bookingpackage.BookingPackage;
import com.lokanda.hotel.model.bookingpackage.BreakfastOnly;
import com.lokanda.hotel.model.bookingpackage.HalfBoard;
import com.lokanda.hotel.model.offer.Offer;
import com.lokanda.hotel.model.person.Guest;
import com.lokanda.hotel.model.person.Receptionist;
import com.lokanda.hotel.model.room.Room;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class BookingScreen extends BaseScreen {
    private final ConsoleBookingRepository bookingRepository = new ConsoleBookingRepository();
    private final GuestDAO guestDAO = new GuestDAO();
    private final ReceptionistDAO receptionistDAO = new ReceptionistDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final OfferDAO offerDAO = new OfferDAO();

    public BookingScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Booking Management");
            ConsoleUI.option("1", "Create new booking");
            ConsoleUI.option("2", "View all bookings");
            ConsoleUI.option("3", "Search booking by ID");
            ConsoleUI.option("4", "Modify booking dates");
            ConsoleUI.option("5", "Confirm booking");
            ConsoleUI.option("6", "Complete booking");
            ConsoleUI.option("7", "Cancel booking");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 7);
            switch (choice) {
                case 1 -> execute(this::createBooking);
                case 2 -> execute(this::listBookings);
                case 3 -> execute(this::searchBooking);
                case 4 -> execute(this::modifyBooking);
                case 5 -> execute(() -> changeStatus(BookingStatus.CONFIRMED));
                case 6 -> execute(() -> changeStatus(BookingStatus.COMPLETED));
                case 7 -> execute(() -> changeStatus(BookingStatus.CANCELLED));
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void createBooking() throws SQLException {
        Guest guest = guestDAO.getGuestById(readId("Guest ID:"));
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found. Register the guest first.");
        }

        Receptionist receptionist = receptionistDAO.getReceptionistById(
                readId("Receptionist person ID:"));
        if (receptionist == null) {
            throw new IllegalArgumentException("Receptionist not found.");
        }

        Booking booking = null;
        while (booking == null) {
            LocalDate checkIn = input.readDate("Check-in date (YYYY-MM-DD):");
            LocalDate checkOut = input.readDate("Check-out date (YYYY-MM-DD):");
            try {
                DatesValidator.validateDates(checkIn, checkOut);
                booking = new Booking(guest, checkIn, checkOut, receptionist);
            } catch (IllegalArgumentException exception) {
                ConsoleUI.error(exception.getMessage());
                ConsoleUI.info("Please enter the booking dates again.");
            }
        }

        Offer offer = readOptionalOffer(booking.getCheckIn());
        if (offer != null) {
            booking.setOffer(offer);
        }

        while (true) {
            Room room = roomDAO.getRoomById(readId("Room ID:"));
            if (room == null) {
                ConsoleUI.error("Room not found. Choose an existing room.");
                continue;
            }
            BookingPackage bookingPackage = readPackage();
            try {
                booking.addItem(new BookingItem(room, bookingPackage,
                        booking.getCheckIn(), booking.getCheckOut()));
                ConsoleUI.success("Room " + room.getRoomNumber() + " added to booking.");
            } catch (IllegalArgumentException | IllegalStateException exception) {
                ConsoleUI.error(exception.getMessage());
                continue;
            }
            if (!input.readYesNo("Add another room to this booking?")) {
                break;
            }
        }

        ConsoleUI.section("Booking price preview");
        BigDecimal gross = booking.calculateTotal();
        BigDecimal discount = offer == null ? BigDecimal.ZERO : offer.calculateDiscount(gross);
        ConsoleUI.info("Subtotal : " + gross);
        ConsoleUI.info("Discount : " + discount);
        ConsoleUI.info("Final    : " + gross.subtract(discount));

        if (!input.readYesNo("Save this booking?")) {
            ConsoleUI.warning("Booking creation cancelled.");
            return;
        }

        bookingRepository.create(booking);
        ConsoleUI.success("Booking created successfully. Booking ID: " + booking.getBookingId());
    }

    private Offer readOptionalOffer(LocalDate checkIn) throws SQLException {
        while (true) {
            int offerId = input.readInt("Offer ID (0 for no offer):", 0, Integer.MAX_VALUE);
            if (offerId == 0) {
                return null;
            }
            Offer offer = offerDAO.getOfferById(offerId);
            if (offer == null) {
                ConsoleUI.error("Offer not found. Enter another ID or 0.");
                continue;
            }
            if (!offer.isValid(checkIn)) {
                ConsoleUI.error("This offer is inactive or invalid for the check-in date.");
                continue;
            }
            return offer;
        }
    }

    private BookingPackage readPackage() {
        ConsoleUI.section("Choose booking package");
        ConsoleUI.option("1", "Breakfast Only");
        ConsoleUI.option("2", "Half Board");
        ConsoleUI.option("3", "All Inclusive");
        int choice = input.readInt("Package:", 1, 3);
        return switch (choice) {
            case 1 -> new BreakfastOnly();
            case 2 -> new HalfBoard();
            default -> new AllInclusive();
        };
    }

    private void listBookings() throws SQLException {
        List<List<String>> rows = new ArrayList<>();
        for (BookingSummary booking : bookingRepository.findAllSummaries()) {
            rows.add(summaryRow(booking));
        }
        ConsoleUI.printTable(List.of("ID", "GUEST", "CHECK-IN", "CHECK-OUT", "STATUS", "TOTAL"), rows);
    }

    private void searchBooking() throws SQLException {
        BookingSummary booking = bookingRepository.findSummaryById(readId("Booking ID:"));
        if (booking == null) {
            ConsoleUI.warning("Booking not found.");
            return;
        }
        ConsoleUI.printTable(
                List.of("ID", "GUEST", "CHECK-IN", "CHECK-OUT", "STATUS", "SUBTOTAL", "DISCOUNT", "FINAL"),
                List.of(List.of(
                        String.valueOf(booking.bookingId()),
                        booking.guestName(),
                        booking.checkIn().toString(),
                        booking.checkOut().toString(),
                        booking.status(),
                        booking.totalBeforeDiscount().toPlainString(),
                        booking.discount().toPlainString(),
                        booking.finalTotal().toPlainString()
                ))
        );
    }

    private void modifyBooking() throws SQLException {
        int id = readId("Booking ID to modify:");
        LocalDate checkIn;
        LocalDate checkOut;
        while (true) {
            checkIn = input.readDate("New check-in (YYYY-MM-DD):");
            checkOut = input.readDate("New check-out (YYYY-MM-DD):");
            try {
                DatesValidator.validateDates(checkIn, checkOut);
                break;
            } catch (IllegalArgumentException exception) {
                ConsoleUI.error(exception.getMessage());
            }
        }
        bookingRepository.modifyDates(id, checkIn, checkOut);
        ConsoleUI.success("Booking dates and room prices updated.");
    }

    private void changeStatus(BookingStatus status) throws SQLException {
        int id = readId("Booking ID:");
        bookingRepository.changeStatus(id, status);
        ConsoleUI.success("Booking status changed to " + status + ".");
    }

    private List<String> summaryRow(BookingSummary booking) {
        return List.of(
                String.valueOf(booking.bookingId()),
                booking.guestName(),
                booking.checkIn().toString(),
                booking.checkOut().toString(),
                booking.status(),
                booking.finalTotal().toPlainString()
        );
    }
}
