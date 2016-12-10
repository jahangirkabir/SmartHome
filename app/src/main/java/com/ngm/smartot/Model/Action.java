package com.ngm.smartot.Model;

/**
 * Created by Kabir on 3/10/2015.
 */
public class Action {
    String device, action, time;

    public Action() {
    }

    public Action(String device, String action, String time) {
        this.device = device;
        this.action = action;
        this.time = time;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Action{" +
                "device='" + device + '\'' +
                ", action='" + action + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
