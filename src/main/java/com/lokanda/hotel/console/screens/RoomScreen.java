package com.lokanda.hotel.console.screens;

import com.lokanda.hotel.DAO.RoomDAO;
import com.lokanda.hotel.console.core.BaseScreen;
import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.model.room.Room;
import com.lokanda.hotel.model.room.RoomType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class RoomScreen extends BaseScreen {
    private final RoomDAO roomDAO = new RoomDAO();

    public RoomScreen(ConsoleInput input) {
        super(input);
    }

    public void showMenu() {
        while (true) {
            ConsoleUI.header("Room Management");
            ConsoleUI.option("1", "Add room");
            ConsoleUI.option("2", "View all rooms");
            ConsoleUI.option("3", "Search room by ID");
            ConsoleUI.option("4", "Update room");
            ConsoleUI.option("5", "Delete room");
            ConsoleUI.option("0", "Back to main menu");
            ConsoleUI.divider();
            int choice = input.readInt("Choose an option:", 0, 5);
            switch (choice) {
                case 1 -> execute(this::addRoom);
                case 2 -> execute(this::listRooms);
                case 3 -> execute(this::searchRoom);
                case 4 -> execute(this::updateRoom);
                case 5 -> execute(this::deleteRoom);
                case 0 -> { return; }
                default -> { }
            }
        }
    }

    private void addRoom() throws SQLException {
        Room room = new Room(0, "TEMP", RoomType.SINGLE);
        readRoomFields(room);
        if (roomDAO.addRoom(room)) {
            ConsoleUI.success("Room added. Room ID: " + room.getRoomId());
        } else {
            ConsoleUI.warning("Room was not added.");
        }
    }

    private void listRooms() throws SQLException {
        List<List<String>> rows = new ArrayList<>();
        for (Room room : roomDAO.getAllRooms()) {
            rows.add(List.of(String.valueOf(room.getRoomId()), room.getRoomNumber(), room.getType().name()));
        }
        ConsoleUI.printTable(List.of("ID", "ROOM NUMBER", "ROOM TYPE"), rows);
    }

    private void searchRoom() throws SQLException {
        Room room = roomDAO.getRoomById(readId("Room ID:"));
        if (room == null) {
            ConsoleUI.warning("Room not found.");
            return;
        }
        printRoom(room);
    }

    private void updateRoom() throws SQLException {
        int id = readId("Room ID to update:");
        Room room = roomDAO.getRoomById(id);
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        readRoomFields(room);
        if (roomDAO.updateRoom(room)) {
            ConsoleUI.success("Room updated.");
        } else {
            ConsoleUI.warning("No room record was updated.");
        }
    }

    private void deleteRoom() throws SQLException {
        int id = readId("Room ID to delete:");
        Room room = roomDAO.getRoomById(id);
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        printRoom(room);
        if (!input.readYesNo("Delete this room permanently?")) {
            ConsoleUI.info("Delete operation cancelled.");
            return;
        }
        if (roomDAO.deleteRoom(id)) {
            ConsoleUI.success("Room deleted.");
        } else {
            ConsoleUI.warning("Room was not deleted.");
        }
    }

    private void readRoomFields(Room room) {
        input.readValidated("Room number:", value -> { room.setRoomNumber(value); return value; });
        room.setType(input.readEnum("Room type (SINGLE / DOUBLE / SUITE):", RoomType.class));
    }

    private void printRoom(Room room) {
        ConsoleUI.section("Room details");
        ConsoleUI.info("ID         : " + room.getRoomId());
        ConsoleUI.info("Room number: " + room.getRoomNumber());
        ConsoleUI.info("Room type  : " + room.getType());
    }
}
