package com.lokanda.hotel.payment;

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
        this.amount = amount;
        this.method = method;
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public PaymentMethod getMethod() { return method; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

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