
package com.lokanda.hotel.booking;

import java.math.BigDecimal;
import com.lokanda.hotel.room.Room;
import com.lokanda.hotel.bookingpackage.BookingPackage;

public class BookingItem {

    private int itemId;
    private Room room;
    private BookingPackage bookingPackage;
    private BigDecimal priceSnapshot;

    public BookingItem(int itemId, Room room, BookingPackage bookingPackage,
                       BigDecimal priceSnapshot) {

        setItemId(itemId);
        setRoom(room);
        setBookingPackage(bookingPackage);
        setPriceSnapshot(priceSnapshot);
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

    public void setRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        this.room = room;
    }

    public BookingPackage getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(BookingPackage bookingPackage) {
        if (bookingPackage == null) {
            throw new IllegalArgumentException("Booking package cannot be null");
        }
        this.bookingPackage = bookingPackage;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        if (priceSnapshot == null ||
                priceSnapshot.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        this.priceSnapshot = priceSnapshot;
    }
}

