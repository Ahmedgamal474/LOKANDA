package com.lokanda.hotel.person;

public class Guest extends Person {

    private String nationalId;

    public Guest(int personId, String firstName, String lastName,
                 String email, String phone, String nationalId) {

        super(personId, firstName, lastName, email, phone);
        this.nationalId = nationalId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
}
