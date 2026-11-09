
package com.lokanda.hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lokanda.hotel.booking.Booking;
import com.lokanda.hotel.booking.BookingItem;
import com.lokanda.hotel.booking.BookingStatus;
import com.lokanda.hotel.room.Room;
import com.lokanda.hotel.room.RoomType;

public class ReservationService {

    private List<Room> rooms;
    private List<Booking> reservations;

    // Constructor
    public ReservationService(List<Room> rooms) {
        if (rooms == null || rooms.contains(null)) {
            throw new IllegalArgumentException("Invalid rooms list");
        }

        this.rooms = new ArrayList<>(rooms);
        this.reservations = new ArrayList<>();
    }

    // Get Available Rooms
    public List<Room> getAvailableRooms(LocalDate checkIn,
                                        LocalDate checkOut,
                                        RoomType roomType) {

        validateDates(checkIn, checkOut);

        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {

            if (room.getType() == roomType &&
                    isRoomAvailable(room, checkIn, checkOut, -1)) {

                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // Create Reservation
    public Booking createReservation(Booking booking) {

        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        validateDates(booking.getCheckIn(), booking.getCheckOut());

        if (booking.getItems().isEmpty()) {
            throw new IllegalArgumentException("Booking must contain at least one item");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED ||
                booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Invalid booking status");
        }

        for (Booking existingBooking : reservations) {
            if (existingBooking.getBookingId() == booking.getBookingId()) {
                throw new IllegalArgumentException("Booking ID already exists");
            }
        }

        for (BookingItem item : booking.getItems()) {

            if (!rooms.contains(item.getRoom())) {
                throw new IllegalArgumentException("Room does not belong to this hotel");
            }

            if (!isRoomAvailable(item.getRoom(),
                    booking.getCheckIn(),
                    booking.getCheckOut(), -1)) {

                throw new IllegalStateException("Room is not available");
            }
        }

        reservations.add(booking);

        return booking;
    }

    // Find Reservation
    public Booking findReservation(int bookingId) {

        if (bookingId < 0) {
            throw new IllegalArgumentException("Invalid booking ID");
        }

        for (Booking booking : reservations) {

            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }

        throw new IllegalArgumentException("Reservation not found");
    }

    // Modify Reservation
    public void modifyReservation(int bookingId,
                                  LocalDate newCheckIn,
                                  LocalDate newCheckOut) {

        Booking booking = findReservation(bookingId);

        validateDates(newCheckIn, newCheckOut);

        for (BookingItem item : booking.getItems()) {

            if (!isRoomAvailable(item.getRoom(),
                    newCheckIn, newCheckOut, bookingId)) {

                throw new IllegalStateException("Room is not available for these dates");
            }
        }

        booking.modify(newCheckIn, newCheckOut);
    }

    // Cancel Reservation
    public void cancelReservation(int bookingId) {

        Booking booking = findReservation(bookingId);

        booking.cancel();
    }

    // Check Room Availability
    private boolean isRoomAvailable(Room room,
                                    LocalDate checkIn,
                                    LocalDate checkOut,
                                    int excludedBookingId) {

        for (Booking booking : reservations) {

            if (booking.getBookingId() == excludedBookingId) {
                continue;
            }

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }

            boolean datesOverlap =
                    checkIn.isBefore(booking.getCheckOut()) &&
                    checkOut.isAfter(booking.getCheckIn());

            if (datesOverlap) {

                for (BookingItem item : booking.getItems()) {

                    if (item.getRoom().equals(room)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // Validate Dates
    private void validateDates(LocalDate checkIn, LocalDate checkOut) {

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out");
        }
    }
}
