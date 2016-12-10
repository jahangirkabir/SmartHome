package com.ngm.smartot.Model;

/**
 * Created by Kabir on 3/10/2015.
 */
public class Device {

    String deviceId, deviceName, deviceIcon, deviceType, deviceRoomID;
    boolean isSelected;
    public Device() {
    }

    public Device(String deviceId, String deviceName, String deviceIcon, String deviceType, String deviceRoomID, boolean isSelected) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceIcon = deviceIcon;
        this.deviceType = deviceType;
        this.deviceRoomID = deviceRoomID;
        this.isSelected = isSelected;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIcon() {
        return deviceIcon;
    }

    public void setDeviceIcon(String deviceIcon) {
        this.deviceIcon = deviceIcon;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceRoomID() {
        return deviceRoomID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setDeviceRoomID(String deviceRoomID) {
        this.deviceRoomID = deviceRoomID;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceIcon='" + deviceIcon + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceRoomID='" + deviceRoomID + '\'' +
                '}';
    }
}