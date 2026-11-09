
package com.lokanda.hotel.person;

public abstract class Person {

    protected int personId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phone;

    protected Person() {
    }

    protected Person(int personId, String firstName, String lastName, String email, String phone) {
        setPersonId(personId);
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPhone(phone);
    }

    public int getPersonId() {
        return personId;
    }

    public final void setPersonId(int personId) {
        if (personId < 0) {
            throw new IllegalArgumentException("Person ID cannot be negative");
        }
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(String firstName) {
        this.firstName = validateName(firstName, "First name");
    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(String lastName) {
        this.lastName = validateName(lastName, "Last name");
    }

    public String getEmail() {
        return email;
    }

    public final void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        String trimmed = email.trim();

        if (trimmed.length() > 100 || !isValidEmail(trimmed)) {
            throw new IllegalArgumentException(
                    "Email is not valid (example: name@mail.com)");
        }

        this.email = trimmed;
    }

    public String getPhone() {
        return phone;
    }

    public final void setPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }

        String trimmed = phone.trim();

        if (!isValidPhone(trimmed)) {
            throw new IllegalArgumentException(
                    "Phone must be 11 digits and start with 010, 011, 012 or 015");
        }

        this.phone = trimmed;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    private static String validateName(String name, String label) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(label + " cannot be empty");
        }

        String trimmed = name.trim();

        if (trimmed.length() < 2 || trimmed.length() > 30) {
            throw new IllegalArgumentException(
                    label + " must be 2-30 characters");
        }

        return trimmed;
    }

    private static boolean isValidPhone(String phone) {

        if (phone.length() != 11) {
            return false;
        }

        if (!phone.startsWith("010")
                && !phone.startsWith("011")
                && !phone.startsWith("012")
                && !phone.startsWith("015")) {
            return false;
        }

        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidEmail(String email) {

        int atIndex = email.indexOf('@');
        int dotIndex = email.lastIndexOf('.');

        if (atIndex <= 0 || dotIndex <= atIndex + 1) {
            return false;
        }

        if (dotIndex == email.length() - 1) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getFullName() + " <" + email + ">";
    }
}

