
package com.lokanda.hotel.offer;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Offer {

    private int offerId;
    private String name;
    private String description;
    private double discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public Offer(int offerId, String name, String description,
                 double discountPercentage, LocalDate startDate,
                 LocalDate endDate, boolean active) {

        setOfferId(offerId);
        setName(name);
        setDescription(description);
        setDiscountPercentage(discountPercentage);
        setStartDate(startDate);
        setEndDate(endDate);
        setActive(active);
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        if (offerId < 0) {
            throw new IllegalArgumentException("Offer ID cannot be negative");
        }

        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Offer name cannot be empty");
        }

        String trimmed = name.trim();

        if (trimmed.length() > 100) {
            throw new IllegalArgumentException(
                    "Offer name cannot exceed 100 characters");
        }

        this.name = trimmed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Offer description cannot be empty");
        }

        this.description = description.trim();
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException(
                    "Discount must be between 0 and 100");
        }

        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException(
                    "Start date cannot be null");
        }

        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException(
                    "End date cannot be null");
        }

        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date");
        }

        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isValid(LocalDate date) {
        return active && date != null
                && !date.isBefore(startDate)
                && !date.isAfter(endDate);
    }

    public BigDecimal calculateDiscount(BigDecimal totalPrice) {
        if (totalPrice == null || totalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Total price cannot be null or negative");
        }

        double price = totalPrice.doubleValue();
        double discount = price * discountPercentage / 100;

        return BigDecimal.valueOf(discount);
    }
}
