
package com.lokanda.hotel.model.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.lokanda.hotel.model.room.RoomType;

public class BreakfastOnly extends BookingPackage {

    public BreakfastOnly() {
        super("Breakfast Only");
    }

    @Override
    public BigDecimal calculatePackagePrice(
            RoomType roomType, LocalDate checkIn, LocalDate checkOut) {

        if (roomType == null) {
            throw new IllegalArgumentException(
                    "Room type cannot be null");
        }

        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates cannot be null");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException(
                    "Check-out must be after check-in");
        }

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal pricePerNight;

        switch (roomType) {
            case SINGLE:
                pricePerNight = new BigDecimal("10");
                break;

            case DOUBLE:
                pricePerNight = new BigDecimal("15");
                break;

            default:
                pricePerNight = new BigDecimal("25");
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }

    @Override
    public String getDescription() {
        return "Includes breakfast only.";
    }
}
