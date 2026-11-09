
package com.lokanda.hotel.model.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.lokanda.hotel.model.room.RoomType;

public abstract class BookingPackage {

    protected String name;

    public BookingPackage(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Package name cannot be empty");
        }

        String trimmed = name.trim();

        if (trimmed.length() > 100) {
            throw new IllegalArgumentException(
                    "Package name cannot exceed 100 characters");
        }

        this.name = trimmed;
    }

    public abstract BigDecimal calculatePackagePrice(
            RoomType roomType, LocalDate checkIn, LocalDate checkOut);

    public abstract String getDescription();
}

