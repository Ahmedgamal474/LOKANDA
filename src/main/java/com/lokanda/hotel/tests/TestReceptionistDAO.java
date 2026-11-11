package com.lokanda.hotel.tests;


import com.lokanda.hotel.DAO.ReceptionistDAO;
import com.lokanda.hotel.model.person.Receptionist;

import java.sql.SQLException;
import java.util.List;

public class TestReceptionistDAO {

    public static void main(String[] args) {

        ReceptionistDAO receptionistDAO = new ReceptionistDAO();

        try {

            List<Receptionist> receptionists =
                    receptionistDAO.getAllReceptionists();

            System.out.println("Total Receptionists: "
                    + receptionists.size());

            if (receptionists.isEmpty()) {
                System.out.println("No receptionists found.");
                return;
            }

            for (Receptionist receptionist : receptionists) {

                System.out.println("\n----------------------");

                System.out.println("Person ID: "
                        + receptionist.getPersonId());

                System.out.println("Name: "
                        + receptionist.getFirstName() + " "
                        + receptionist.getLastName());

                System.out.println("Email: "
                        + receptionist.getEmail());

                System.out.println("Phone: "
                        + receptionist.getPhone());

                System.out.println("Employee ID: "
                        + receptionist.getEmployeeId());

                Receptionist byId =
                        receptionistDAO.getReceptionistById(
                                receptionist.getPersonId());

                Receptionist byEmployeeId =
                        receptionistDAO.getReceptionistByEmployeeId(
                                receptionist.getEmployeeId());

                if (byId == null) {
                    throw new RuntimeException(
                            "Get Receptionist By ID failed!");
                }

                if (byEmployeeId == null) {
                    throw new RuntimeException(
                            "Get Receptionist By Employee ID failed!");
                }

                if (byId.getPersonId()
                        != byEmployeeId.getPersonId()) {
                    throw new RuntimeException(
                            "Receptionist data mismatch!");
                }

                System.out.println("Get By ID: Passed");
                System.out.println("Get By Employee ID: Passed");
            }

            System.out.println("\nReceptionistDAO Test Passed!");

        } catch (SQLException e) {

            System.out.println("Database Error:");
            e.printStackTrace();

        } catch (RuntimeException e) {

            System.out.println("Test Failed:");
            e.printStackTrace();
        }
    }
}
