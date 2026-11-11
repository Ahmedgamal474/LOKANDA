package com.lokanda.hotel.tests;

import com.lokanda.hotel.DAO.RoomDAO;
import com.lokanda.hotel.model.room.Room;

import java.util.List;

public class TestRoomDAO {

    public static void main(String[] args) {

        try {

            RoomDAO roomDAO = new RoomDAO();

            List<Room> rooms = roomDAO.getAllRooms();

            System.out.println("Rooms Retrieved Successfully!");
            System.out.println("Total Rooms: " + rooms.size());

            if (rooms.isEmpty()) {
                System.out.println("No rooms found in database.");
            } else {
                System.out.println("Room DAO Test Passed!");
            }

        } catch (Exception e) {

            System.out.println("Room DAO Test Failed!");
            e.printStackTrace();

        }
    }
}