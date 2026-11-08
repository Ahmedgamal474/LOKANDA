package com.lokanda.hotel.bookingpackage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.lokanda.hotel.room.RoomType;
public class HalfBoard extends BookingPackage {

    public HalfBoard() {
        super("Half Board");
    }

    @Override
    public BigDecimal calculatePackagePrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal pricePerNight;

        switch (roomType) {
            case SINGLE:
                pricePerNight = new BigDecimal("25");
                break;
            case DOUBLE:
                pricePerNight = new BigDecimal("40");
                break;
            default:
                pricePerNight = new BigDecimal("60");
        }

        return pricePerNight.multiply(BigDecimal.valueOf(nights));
    }

    @Override
    public String getDescription() {
        return "Includes breakfast and one additional main meal per day.";
    }
}
