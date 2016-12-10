package com.ngm.smartot.Model;

/**
 * Created by Kabir on 3/10/2015.
 */
public class Room {
    String roomId, roomName, roomIcon;
    boolean isSelected;

    public Room(String roomId, String roomName, String roomIcon, boolean isSelected) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomIcon = roomIcon;
        this.isSelected = isSelected;
    }

    public Room() {

    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomIcon() {
        return roomIcon;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setRoomIcon(String roomIcon) {
        this.roomIcon = roomIcon;
    }

    @Override
    public String toString() {
        return roomName;
    }
}
