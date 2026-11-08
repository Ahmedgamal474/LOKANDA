package com.lokanda.hotel.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import com.lokanda.hotel.room.RoomType;
public class BreakfastOnly extends BookingPackage {

    public BreakfastOnly() {
        super("Breakfast Only");
    }

    @Override
    public BigDecimal calculatePackagePrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
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
