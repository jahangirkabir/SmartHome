package com.ngm.smartot;

import com.ngm.smartot.Model.Room;

import java.util.Vector;

import lifx.java.android.light.LFXLight;

/**
 * Created by Maruf on 2/24/2015.
 */
public class AppConstant {
    public static String room;
    public static String device;
    public static String deviceid;
    public static float h = 0.1f;
    public static float s = 0.1f;
    public static float b = 0.5f;
    public static int k = 3500;
    public static int powerProgress = 50;
    public static int colorProgress = 0;
    public static float roll;
    public static float roll2;
    public static Vector<Room> rooms;
    public static LFXLight cLifx;
    public static String cRoomId;
    public static String cRoomImageName;
    public static String roomIconName;
    public static boolean myosynced = false;
    public static boolean unknown = true;
}
