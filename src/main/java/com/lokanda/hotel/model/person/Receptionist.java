package com.lokanda.hotel.model.person;

public class Receptionist extends Person {

    private String employeeId;

    public Receptionist(int personId, String firstName, String lastName,
                        String email, String phone, String employeeId) {

        super(personId, firstName, lastName, email, phone);
        setEmployeeId(employeeId);
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new IllegalArgumentException("Employee ID cannot be empty");
        }

        String trimmed = employeeId.trim();

        if (trimmed.length() < 3 || trimmed.length() > 20) {
            throw new IllegalArgumentException(
                    "Employee ID must be between 3 and 20 characters");
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char current = trimmed.charAt(i);

            if (!Character.isLetterOrDigit(current) && current != '-') {
                throw new IllegalArgumentException(
                        "Employee ID can contain only letters, digits and hyphens");
            }
        }

        this.employeeId = trimmed;
    }
}
