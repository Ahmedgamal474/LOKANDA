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
        this.itemId = itemId;
        this.room = room;
        this.bookingPackage = bookingPackage;
        this.priceSnapshot = priceSnapshot;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Room getRoom() {
        return room;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
    public BookingPackage getBookingPackage() {
        return bookingPackage;
    }

    public void setBookingPackage(BookingPackage bookingPackage) {
        this.bookingPackage = bookingPackage;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }
}
