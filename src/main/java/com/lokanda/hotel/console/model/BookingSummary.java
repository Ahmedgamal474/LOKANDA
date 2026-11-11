package com.lokanda.hotel.console.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Read-only row model used to display bookings in the console. */
public record BookingSummary(
        int bookingId,
        String guestName,
        LocalDate checkIn,
        LocalDate checkOut,
        String status,
        BigDecimal totalBeforeDiscount,
        BigDecimal discount,
        BigDecimal finalTotal
) { }
