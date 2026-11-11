package com.lokanda.hotel;

import com.lokanda.hotel.DAO.DatabaseConnection;

import java.sql.Connection;

public class TestDatabaseConnection {

    public static void main(String[] args) {

        try {
            Connection connection = DatabaseConnection.getConnection();

            if (connection != null && !connection.isClosed()) {
                System.out.println("Database Connected Successfully!");
            }

        } catch (Exception e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }
    }
}