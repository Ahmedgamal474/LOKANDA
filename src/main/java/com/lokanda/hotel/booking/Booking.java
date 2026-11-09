
package com.lokanda.hotel.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.lokanda.hotel.person.Guest;
import com.lokanda.hotel.person.Receptionist;
import com.lokanda.hotel.offer.Offer;

public class Booking {

    private int bookingId;
    private Guest guest;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Receptionist receptionist;
    private Offer offer;
    private BigDecimal discountAmount;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private List<BookingItem> items;

    // Default Constructor
    public Booking() {
        this.items = new ArrayList<>();
        this.discountAmount = BigDecimal.ZERO;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Parameterized Constructor
    public Booking(Guest guest, LocalDate checkIn, LocalDate checkOut,
                   Receptionist receptionist) {
        this();

        if (guest == null || receptionist == null) {
            throw new IllegalArgumentException("Guest and Receptionist cannot be null");
        }

        validateDates(checkIn, checkOut);

        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.receptionist = receptionist;
    }

    // Setters & Getters
    public void setBookingId(int bookingId) {
        if (bookingId < 0) {
            throw new IllegalArgumentException("Booking ID cannot be negative");
        }
        this.bookingId = bookingId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public Guest getGuest() {
        return guest;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Receptionist getReceptionist() {
        return receptionist;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        if (discountAmount == null ||
                discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Invalid discount amount");
        }

        if (discountAmount.compareTo(calculateTotal()) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed total price");
        }

        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return calculateTotal();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("Created date cannot be null");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setItems(List<BookingItem> items) {
        if (items == null || items.contains(null)) {
            throw new IllegalArgumentException("Invalid booking items");
        }

        if (status == BookingStatus.CANCELLED ||
                status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify this booking");
        }

        this.items = new ArrayList<>(items);

        if (discountAmount.compareTo(calculateTotal()) > 0) {
            this.items.clear();
            throw new IllegalArgumentException("Discount cannot exceed total price");
        }
    }

    public List<BookingItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // Add a Booking Item
    public void addItem(BookingItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (status == BookingStatus.CANCELLED ||
                status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify this booking");
        }

        items.add(item);

        if (discountAmount.compareTo(calculateTotal()) > 0) {
            items.remove(items.size() - 1);
            throw new IllegalArgumentException("Discount cannot exceed total price");
        }
    }

    // Calculate Total Price Before Discount
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (BookingItem item : items) {
            total = total.add(item.getPriceSnapshot());
        }

        return total;
    }

    // Calculate Final Price After Discount
    public BigDecimal calculateFinalPrice() {
        BigDecimal total = calculateTotal();

        if (discountAmount.compareTo(total) > 0) {
            throw new IllegalStateException("Discount cannot exceed total price");
        }

        return total.subtract(discountAmount);
    }

    // Modify Booking Dates
    public void modify(LocalDate checkIn, LocalDate checkOut) {
        if (status == BookingStatus.CANCELLED ||
                status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify this booking");
        }

        validateDates(checkIn, checkOut);

        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    // Cancel Booking
    public void cancel() {
        if (status == BookingStatus.CANCELLED ||
                status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel this booking");
        }

        this.status = BookingStatus.CANCELLED;
    }

    // Validation
    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in must be before check-out");
        }
    }
}
