package com.lokanda.hotel.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lokanda.hotel.model.booking.Booking;
import com.lokanda.hotel.model.booking.BookingItem;
import com.lokanda.hotel.model.booking.BookingStatus;
import com.lokanda.hotel.model.offer.Offer;
import com.lokanda.hotel.model.room.Room;
import com.lokanda.hotel.model.room.RoomType;

public class ReservationService {

    private final List<Room> rooms;
    private final List<Booking> reservations;

    public ReservationService(List<Room> rooms) {
        if (rooms == null || rooms.contains(null)) {
            throw new IllegalArgumentException("Invalid rooms list");
        }

        this.rooms = new ArrayList<>(rooms);
        this.reservations = new ArrayList<>();

        for (int i = 0; i < this.rooms.size(); i++) {
            for (int j = i + 1; j < this.rooms.size(); j++) {
                if (this.rooms.get(i).getRoomId() == this.rooms.get(j).getRoomId()) {
                    throw new IllegalArgumentException("Room IDs must be unique");
                }
            }
        }
    }

    // Find rooms that are available for the requested dates and room type.
    public List<Room> getAvailableRooms(LocalDate checkIn,
                                        LocalDate checkOut,
                                        RoomType roomType) {

        validateDates(checkIn, checkOut);

        if (roomType == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getType() == roomType
                    && isRoomAvailable(room, checkIn, checkOut, -1)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // Add a valid booking to the reservation list.
    public Booking createReservation(Booking booking) {

        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        validateDates(booking.getCheckIn(), booking.getCheckOut());

        if (booking.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Booking must contain at least one item");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Invalid booking status");
        }

        for (Booking existingBooking : reservations) {
            if (existingBooking.getBookingId() == booking.getBookingId()) {
                throw new IllegalArgumentException("Booking ID already exists");
            }
        }

        // A room can appear only once in the same booking.
        List<Integer> selectedRoomIds = new ArrayList<>();

        for (BookingItem item : booking.getItems()) {
            Room selectedRoom = findHotelRoom(item.getRoom());

            if (selectedRoom == null) {
                throw new IllegalArgumentException(
                        "Room does not belong to this hotel");
            }

            if (selectedRoomIds.contains(selectedRoom.getRoomId())) {
                throw new IllegalArgumentException(
                        "The same room cannot be added more than once");
            }

            selectedRoomIds.add(selectedRoom.getRoomId());

            if (!isRoomAvailable(selectedRoom, booking.getCheckIn(),
                    booking.getCheckOut(), -1)) {
                throw new IllegalStateException("Room is not available");
            }
        }

        // Apply the offer, if one was selected.
        Offer offer = booking.getOffer();

        if (offer != null) {
            if (!offer.isValid(booking.getCheckIn())) {
                throw new IllegalArgumentException(
                        "Offer is not valid for the booking check-in date");
            }

            booking.setDiscountAmount(
                    offer.calculateDiscount(booking.calculateTotal()));
        } else {
            // Check that any manually entered discount is valid.
            booking.calculateFinalPrice();
        }

        reservations.add(booking);
        return booking;
    }

    // Find a reservation using its ID.
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

    // Change the dates of an existing reservation.
    public void modifyReservation(int bookingId,
                                  LocalDate newCheckIn,
                                  LocalDate newCheckOut) {

        Booking booking = findReservation(bookingId);
        validateDates(newCheckIn, newCheckOut);

        if (booking.getOffer() != null
                && !booking.getOffer().isValid(newCheckIn)) {
            throw new IllegalArgumentException(
                    "Offer is not valid for the new check-in date");
        }

        for (BookingItem item : booking.getItems()) {
            Room hotelRoom = findHotelRoom(item.getRoom());

            if (hotelRoom == null || !isRoomAvailable(
                    hotelRoom, newCheckIn, newCheckOut, bookingId)) {
                throw new IllegalStateException(
                        "Room is not available for these dates");
            }
        }

        booking.modify(newCheckIn, newCheckOut);
    }

    public void cancelReservation(int bookingId) {
        Booking booking = findReservation(bookingId);
        booking.cancel();
    }

    private Room findHotelRoom(Room room) {
        if (room == null) {
            return null;
        }

        for (Room hotelRoom : rooms) {
            if (hotelRoom.getRoomId() == room.getRoomId()
                    && hotelRoom.getRoomNumber().equals(room.getRoomNumber())
                    && hotelRoom.getType() == room.getType()) {
                return hotelRoom;
            }
        }

        return null;
    }

    private boolean isRoomAvailable(Room room,
                                    LocalDate checkIn,
                                    LocalDate checkOut,
                                    int excludedBookingId) {

        for (Booking booking : reservations) {

            if (booking.getBookingId() == excludedBookingId
                    || booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }

            boolean datesOverlap =
                    checkIn.isBefore(booking.getCheckOut())
                    && checkOut.isAfter(booking.getCheckIn());

            if (datesOverlap) {
                for (BookingItem item : booking.getItems()) {
                    if (item.getRoom().getRoomId() == room.getRoomId()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private void validateDates(LocalDate checkIn, LocalDate checkOut) {

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(
                    "Check-in must be before check-out");
        }
    }
}
