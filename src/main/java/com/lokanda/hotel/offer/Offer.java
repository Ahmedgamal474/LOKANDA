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

        this.offerId = offerId;
        this.name = name;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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
        double price = totalPrice.doubleValue();
        double discount = price * discountPercentage / 100;

        return BigDecimal.valueOf(discount);
    }
}
