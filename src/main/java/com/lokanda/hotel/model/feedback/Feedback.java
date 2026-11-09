
package com.lokanda.hotel.model.feedback;

import java.time.LocalDateTime;

public class Feedback {

    private int feedbackId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Feedback() {
    }

    public Feedback(int rating, String comment) {
        setRating(rating);
        setComment(comment);
        this.createdAt = LocalDateTime.now();
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        if (feedbackId < 0) {
            throw new IllegalArgumentException(
                    "Feedback ID cannot be negative");
        }

        this.feedbackId = feedbackId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5");
        }

        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException(
                    "Comment cannot be empty");
        }

        this.comment = comment.trim();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Created date cannot be null");
        }

        this.createdAt = createdAt;
    }

    // A valid rating is between 1 and 5
    public boolean validateRating() {
        return rating >= 1 && rating <= 5;
    }

    public void editComment(String newComment) {
        if (newComment == null || newComment.isBlank()) {
            throw new IllegalArgumentException(
                    "Comment cannot be empty");
        }

        this.comment = newComment.trim();
    }

    @Override
    public String toString() {
        return rating + "/5 - " + comment;
    }
}
