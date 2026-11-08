package com.lokanda.hotel.bookingpackage;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.lokanda.hotel.room.RoomType;
import java.time.temporal.ChronoUnit;
public class AllInclusive extends BookingPackage {
    public AllInclusive() {
        super("All Inclusive");
    }

    @Override
    public BigDecimal calculatePackagePrice(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
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
