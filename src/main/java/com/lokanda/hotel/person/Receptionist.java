package com.lokanda.hotel.person;
public class Receptionist extends Person {

    private String employeeId;


    public Receptionist(int personId, String firstName, String lastName,
                        String email, String phone, String employeeId) {

        super(personId, firstName, lastName, email, phone);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
