
package com.lokanda.hotel.model.booking;

import com.lokanda.hotel.model.room.Room;
import com.lokanda.hotel.model.bookingpackage.BookingPackage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingItem {

    private int itemId;
    private Room room;
    private BookingPackage bookingPackage;
    private BigDecimal priceSnapshot;

    public BookingItem(Room room, BookingPackage bookingPackage, LocalDate checkIn,LocalDate checkOut) {
        DatesValidator.validateDates(checkIn,checkOut);
        setRoom(room);
        setBookingPackage(bookingPackage);
        setPriceSnapshot(this.bookingPackage.calculatePackagePrice(this.room.getType(),checkIn,checkOut));
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        if (itemId < 0) {
            throw new IllegalArgumentException("Item ID cannot be negative");
        }
        this.itemId = itemId;
    }

    public Room getRoom() {
        return room;
    }

    private void setRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        this.room = room;
    }

    public BookingPackage getBookingPackage() {
        return bookingPackage;
    }

    private void setBookingPackage(BookingPackage bookingPackage) {
        if (bookingPackage == null) {
            throw new IllegalArgumentException("Booking package cannot be null");
        }
        this.bookingPackage = bookingPackage;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    private void setPriceSnapshot(BigDecimal priceSnapshot) {
        if (priceSnapshot == null ||
                priceSnapshot.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.priceSnapshot = priceSnapshot;
    }
}



