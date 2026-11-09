
package com.lokanda.hotel.person;

public class Guest extends Person {

    private String nationalId;

    public Guest() {
    }

    public Guest(int personId, String firstName, String lastName,
                 String email, String phone, String nationalId) {
        super(personId, firstName, lastName, email, phone);
        setNationalId(nationalId);
    }

    public Guest(String firstName, String lastName, String email,
                 String phone, String nationalId) {
        this(0, firstName, lastName, email, phone, nationalId);
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            throw new IllegalArgumentException("National ID cannot be empty");
        }

        String trimmed = nationalId.trim();

        if (trimmed.length() != 14) {
            throw new IllegalArgumentException(
                    "National ID must be 14 digits");
        }

        if (trimmed.charAt(0) != '2' && trimmed.charAt(0) != '3') {
            throw new IllegalArgumentException(
                    "National ID must start with 2 or 3");
        }

        for (int i = 0; i < trimmed.length(); i++) {
            if (!Character.isDigit(trimmed.charAt(i))) {
                throw new IllegalArgumentException(
                        "National ID must contain only digits");
            }
        }

        int month = Integer.parseInt(trimmed.substring(3, 5));
        int day = Integer.parseInt(trimmed.substring(5, 7));

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "National ID month must be between 1 and 12");
        }

        if (day < 1 || day > 31) {
            throw new IllegalArgumentException(
                    "National ID day must be between 1 and 31");
        }

        this.nationalId = trimmed;
    }
}

