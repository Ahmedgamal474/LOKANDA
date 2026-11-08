package com.lokanda.hotel.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.lokanda.hotel.room.RoomType;
public abstract class BookingPackage {
    protected String name;

    public BookingPackage(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract BigDecimal calculatePackagePrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut);

    public abstract String getDescription();
}
