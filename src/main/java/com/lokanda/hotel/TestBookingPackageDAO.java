package com.lokanda.hotel;

import com.lokanda.hotel.DAO.BookingPackageDAO;
import com.lokanda.hotel.model.bookingpackage.BookingPackage;

import java.util.List;

public class TestBookingPackageDAO {

    public static void main(String[] args) {

        try {

            BookingPackageDAO dao = new BookingPackageDAO();

            List<BookingPackage> packages = dao.getAllPackages();

            System.out.println("Packages Retrieved Successfully!");
            System.out.println("Total Packages: " + packages.size());

            for (BookingPackage p : packages) {
                System.out.println("--------------------");
                System.out.println("Name: " + p.getName());
                System.out.println("Description: " + p.getDescription());
            }

            if (packages.size() == 3) {
                System.out.println("BookingPackage DAO Test Passed!");
            } else {
                System.out.println("Check the packages in database!");
            }

        } catch (Exception e) {

            System.out.println("BookingPackage DAO Test Failed!");
            e.printStackTrace();

        }
    }
}