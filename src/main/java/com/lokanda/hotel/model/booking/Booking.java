
package com.lokanda.hotel.model.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.lokanda.hotel.model.person.Guest;
import com.lokanda.hotel.model.person.Receptionist;
import com.lokanda.hotel.model.offer.Offer;
import com.lokanda.hotel.model.feedback.Feedback;
import com.lokanda.hotel.model.payment.Payment;
import com.lokanda.hotel.model.payment.PaymentStatus;
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
    private List<Payment> payments;
    private Feedback feedback;
    // Default Constructor
    public Booking() {
        this.items = new ArrayList<>();
        this.payments = new ArrayList<>();
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

        List<BookingItem> newItems = new ArrayList<>(items);
        BigDecimal newTotal = BigDecimal.ZERO;

        for (BookingItem item : newItems) {
            newTotal = newTotal.add(item.getPriceSnapshot());
        }

        if (discountAmount.compareTo(newTotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed total price");
        }

        this.items = newItems;
    }

    public List<BookingItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback cannot be null");
        }

        if (status != BookingStatus.COMPLETED) {
            throw new IllegalStateException("Feedback is allowed only for completed bookings");
        }

        if (this.feedback != null) {
            throw new IllegalStateException("This booking already has feedback");
        }

        this.feedback = feedback;
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

        BigDecimal newTotal = calculateTotal().add(item.getPriceSnapshot());

        if (discountAmount.compareTo(newTotal) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed total price");
        }

        items.add(item);
    }
    //Add payment
    public void addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot add payment to a cancelled booking");
        }

        payments.add(payment);
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
    //Calculate Total Paid
    public BigDecimal getTotalPaid() {
        BigDecimal total = BigDecimal.ZERO;

        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.COMPLETED) {
                total = total.add(payment.getAmount());
            }
        }

        return total;
    }
    //Is it Fully Paid
    public boolean isFullyPaid() {
        return getTotalPaid().compareTo(calculateFinalPrice()) >= 0;
    }
    //Get the remaining Balance
    public BigDecimal getRemainingBalance() {
        return calculateFinalPrice().subtract(getTotalPaid());
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
//    public void confirm() {
//        if (status != BookingStatus.PENDING) {
//            throw new IllegalStateException("Only pending bookings can be confirmed");
//        }
//
//        this.status = BookingStatus.CONFIRMED;
//    }
//
//    public void complete() {
//        if (status != BookingStatus.CONFIRMED) {
//            throw new IllegalStateException("Only confirmed bookings can be completed");
//        }
//
//        this.status = BookingStatus.COMPLETED;
//    }
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
