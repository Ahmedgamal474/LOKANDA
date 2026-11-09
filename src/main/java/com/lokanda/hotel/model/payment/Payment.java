
package com.lokanda.hotel.model.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private int paymentId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;

    public Payment() {
    }

    public Payment(BigDecimal amount, PaymentMethod method) {
        setAmount(amount);
        setMethod(method);
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        if (paymentId < 0) {
            throw new IllegalArgumentException(
                    "Payment ID cannot be negative");
        }

        this.paymentId = paymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        if (paymentDate == null) {
            throw new IllegalArgumentException(
                    "Payment date cannot be null");
        }

        this.paymentDate = paymentDate;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        if (method == null) {
            throw new IllegalArgumentException(
                    "Payment method cannot be null");
        }

        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Payment status cannot be null");
        }

        this.status = status;
    }

    public boolean processPayment() {
        if (status == PaymentStatus.COMPLETED) {
            return true;
        }

        if (amount != null && amount.signum() > 0 && method != null) {
            status = PaymentStatus.COMPLETED;
            paymentDate = LocalDateTime.now();
            return true;
        }

        status = PaymentStatus.FAILED;
        return false;
    }

    @Override
    public String toString() {
        return amount + " via " + method + " [" + status + "]";
    }
}
