package com.ngm.smartot.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static int DATABASE_VERSION = 1;

	public static String DATABASE_NAME = "SmartHomedb";

	
	public static String TABLE_action = "actionTable";
	public static String TABLE_room = "roomTable";
	public static String TABLE_device = "deviceTable";

    public static String DEVICE_id = "id";
	public static String DEVICE_name = "device_name";
	public static String DEVICE_type = "device_type";
    public static String DEVICE_icon_name = "device_icon_name";
	public static String DEVICE_room_id = "device_room_id";

	public static String ROOM_id = "id";
	public static String ROOM_name = "name";
    public static String ROOM_icon_name = "icon_name";

    public static String action_device = "device";
	public static String action_name = "name";
    public static String action_time = "time";

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	final String CREATE_ACTION_TABLE = "CREATE TABLE " + TABLE_action + "("
			+ action_device + " TEXT,"
			+ action_name + " TEXT,"
            + action_time + " DATETIME " + ")";

    final String CREATE_ROOM_TABLE = "CREATE TABLE " + TABLE_room + "("
			+ ROOM_id + " INTEGER PRIMARY KEY,"
            + ROOM_icon_name + " TEXT , "
			+ ROOM_name + " TEXT " + ")";
	
	final String CREATE_DEVICE_TABLE = "CREATE TABLE " + TABLE_device + "("
			+ DEVICE_id + " TEXT ,"
			+ DEVICE_name + " TEXT , "
			+ DEVICE_type + " TEXT , "
            + DEVICE_icon_name + " TEXT , "
			+ DEVICE_room_id + " TEXT " + ")";
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACTION_TABLE);
		db.execSQL(CREATE_ROOM_TABLE);
		db.execSQL(CREATE_DEVICE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
