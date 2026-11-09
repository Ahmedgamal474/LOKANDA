
package com.lokanda.hotel.room;

public class Room {

    private int roomId;
    private String roomNumber;
    private RoomType type;

    public Room(int roomId, String roomNumber, RoomType type) {
        setRoomId(roomId);
        setRoomNumber(roomNumber);
        setType(type);
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        if (roomId < 0) {
            throw new IllegalArgumentException("Room ID cannot be negative");
        }

        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.isBlank()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }

        String trimmed = roomNumber.trim();

        if (trimmed.length() > 10) {
            throw new IllegalArgumentException(
                    "Room number cannot exceed 10 characters");
        }

        this.roomNumber = trimmed;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        if (type == null) {
            throw new IllegalArgumentException("Room type cannot be null");
        }

        this.type = type;
    }
}

