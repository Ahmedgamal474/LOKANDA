package com.lokanda.hotel.feedback;

import java.time.LocalDateTime;
public class Feedback {
    private int feedbackId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public Feedback() {
    }

    public Feedback(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // A valid rating is between 1 and 5
    public boolean validateRating() {
        return rating >= 1 && rating <= 5;
    }

//    public void editRating(int newRating) {
//        if (newRating < 1 || newRating > 5) {
//            throw new IllegalArgumentException("Rating must be between 1 and 5");
//        }
//        this.rating = newRating;
//    }
    public void editComment(String newComment) {
        if (newComment == null || newComment.isBlank()) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        this.comment = newComment.trim();
    }

    @Override
    public String toString() {
        return rating + "/5 - " + comment;
    }
}