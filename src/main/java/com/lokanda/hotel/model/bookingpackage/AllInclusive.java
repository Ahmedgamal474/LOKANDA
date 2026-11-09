
package com.lokanda.hotel.model.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.lokanda.hotel.model.room.RoomType;

public class AllInclusive extends BookingPackage {

    public AllInclusive() {
        super("All Inclusive");
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
                pricePerNight = new BigDecimal("50");
                break;

            case DOUBLE:
                pricePerNight = new BigDecimal("80");
                break;

            default:
                pricePerNight = new BigDecimal("120");
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }

    @Override
    public String getDescription() {
        return "Includes meals, drinks, and other services covered by the hotel's all-inclusive plan.";
    }
}

