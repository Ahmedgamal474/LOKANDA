package com.lokanda.hotel.room;
public class Room {
      private int roomId;
    private String roomNumber;
    private RoomType type;

    public Room(int roomId, String roomNumber, RoomType type) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.type = type;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }
}
