package com.ngm.smartot.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ngm.smartot.Model.Action;
import com.ngm.smartot.Model.Device;
import com.ngm.smartot.Model.Room;

import java.util.Vector;

public class DatabaseAdapter {
	SQLiteDatabase database;
	DatabaseOpenHelper dbHelper;
    private String q;

    public DatabaseAdapter(Context context) {
		dbHelper = new DatabaseOpenHelper(context);
	}

	public void open() {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		database.close();
	}

	public long addDevice(String id, String title, String type, String icon, String roomId) {
		open();
		ContentValues cv = new ContentValues();
		cv.put(DatabaseOpenHelper.DEVICE_id, id);
		cv.put(DatabaseOpenHelper.DEVICE_name, title);
		cv.put(DatabaseOpenHelper.DEVICE_type, type);
		cv.put(DatabaseOpenHelper.DEVICE_icon_name, icon);
		cv.put(DatabaseOpenHelper.DEVICE_room_id, roomId);
		long l = database.insert(DatabaseOpenHelper.TABLE_device, "", cv);
		close();
		return l;
	}

    public Device getDeviceById(String did) {
        open();
        Device device = null;
        String[] columns = {DatabaseOpenHelper.DEVICE_id,
                DatabaseOpenHelper.DEVICE_name,
                DatabaseOpenHelper.DEVICE_type,
                DatabaseOpenHelper.DEVICE_icon_name,
                DatabaseOpenHelper.DEVICE_room_id };

        Cursor cursor = database.query(DatabaseOpenHelper.TABLE_device, columns, " id = '"+ did +"'", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_id));
                String title = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_name));
                String type = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_type));
                String iconname = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_icon_name));
                String roomid = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_room_id));

                device = new Device(id,title,iconname,type,roomid,false);
//				photos.add(p);
                cursor.moveToNext();
            }
        }

        cursor.close();
        close();
        return device;
    }

	public Vector<Device> getAllDevicesByRoomId(String rid) {
		open();
		Vector<Device> devices = new Vector<Device>();

		String[] columns = {DatabaseOpenHelper.DEVICE_id,
                DatabaseOpenHelper.DEVICE_name,
                DatabaseOpenHelper.DEVICE_type,
                DatabaseOpenHelper.DEVICE_icon_name,
                DatabaseOpenHelper.DEVICE_room_id };

		Cursor cursor = database.query(DatabaseOpenHelper.TABLE_device, columns, " device_room_id = '"+ rid +"'", null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_id));
                String title = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_name));
                String type = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_type));
                String iconname = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_icon_name));
                String roomid = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.DEVICE_room_id));

                Device p = new Device(id,title,iconname,type,roomid,false);
				devices.add(p);
				cursor.moveToNext();
			}
		}

		cursor.close();
		close();
		return devices;
	}
//
//	public Vector<Photo> getPhotoByFolderID(String fid) {
//		open();
//		Vector<Photo> photos = new Vector<Photo>();
//
//		String[] columns = {DatabaseOpenHelper.DEVICE_id,
//				DatabaseOpenHelper.PHOTO_title,
//				DatabaseOpenHelper.PHOTO_path,
//				DatabaseOpenHelper.PHOTO_date,
//				DatabaseOpenHelper.PHOTO_folderid,
//				DatabaseOpenHelper.PHOTO_favorite };
//
//		Cursor cursor = database.query(DatabaseOpenHelper.TABLE_photo, columns, " fid = '"+ fid +"'", null, null, null, null);
//
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//
//			for (int i = 0; i < cursor.getCount(); i++) {
//				String id = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.DEVICE_id));
//				String title = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_title));
//				String path = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_path));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_date));
//				String folderid = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_folderid));
//				String favorite = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_favorite));
//
//				Photo p = new Photo(id, title, path, date, folderid, favorite);
//				photos.add(p);
//				cursor.moveToNext();
//			}
//		}
//
//		cursor.close();
//		close();
//		return photos;
//	}
//
//	public Vector<Photo> getSearchPhoto(String ptitle, String pdate, String todate, String photoFolderId) {
//		open();
//		Vector<Photo> photos = new Vector<Photo>();
//
//		String[] columns = {DatabaseOpenHelper.DEVICE_id,
//				DatabaseOpenHelper.PHOTO_title,
//				DatabaseOpenHelper.PHOTO_path,
//				DatabaseOpenHelper.PHOTO_date,
//				DatabaseOpenHelper.PHOTO_folderid,
//				DatabaseOpenHelper.PHOTO_favorite };
//
//		String titleQ = "";
//		String dateQ = "";
//		String todateQ = "";
//		String folderQ = "";
//
//		boolean first = true;
//
//		if (ptitle != null && ptitle.length() > 0) {
//			titleQ = "  " + DatabaseOpenHelper.PHOTO_title + " LIKE '%" + ptitle + "%' ";
//			first = false;
//		}
//		if (ptitle != null && ptitle.length() > 0) {
//			if (first) {
//				first = false;
//				dateQ = " " + DatabaseOpenHelper.PHOTO_date + " BETWEEN '" + pdate + "' AND '" + todate + "' ";
//			} else {
//				dateQ = " AND " + DatabaseOpenHelper.PHOTO_date + " BETWEEN '" + pdate + "' AND '" + todate + "' ";
//			}
//		} else {
//			if (pdate != null && pdate.length() > 0) {
//				if (first) {
//					first = false;
//					dateQ = " " + DatabaseOpenHelper.PHOTO_date + " = '" + pdate + "' ";
//				} else {
//					dateQ = " AND " + DatabaseOpenHelper.PHOTO_date + " = '" + pdate + "' ";
//				}
//			}
//		}
//
//
////		if (!cat.equalsIgnoreCase(con.getString(R.string.sCat))) {
////			cq = " AND " + BI_categoryNameEnglish + " = '" + cat + "' ";
////		}
//
//		if (photoFolderId != null && photoFolderId.length() > 0) {
//			if (first) {
//				first = false;
//				folderQ = " " + DatabaseOpenHelper.PHOTO_folderid + " = '" + photoFolderId + "' ";
//			} else {
//				folderQ = " AND " + DatabaseOpenHelper.PHOTO_folderid + " = '" + photoFolderId + "' ";
//			}
//
//		}
//
//		String q = "SELECT * from " + DatabaseOpenHelper.TABLE_photo + " WHERE " + titleQ+dateQ+folderQ;
//
//		Log.i("QUERY : ", q);
//
//		Cursor cursor = database.rawQuery(q, null);
//
//
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//
//			for (int i = 0; i < cursor.getCount(); i++) {
//				String id = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.DEVICE_id));
//				String title = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_title));
//				String path = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_path));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_date));
//				String folderid = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_folderid));
//				String favorite = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_favorite));
//
//				Photo p = new Photo(id, title, path, date, folderid, favorite);
//				photos.add(p);
//				cursor.moveToNext();
//			}
//		}
//
//		cursor.close();
//		close();
//		return photos;
//	}
//
//	public Vector<Photo> getRecentPhoto() {
//		open();
//		Vector<Photo> photos = new Vector<Photo>();
//
//		String[] columns = {DatabaseOpenHelper.DEVICE_id,
//				DatabaseOpenHelper.PHOTO_title,
//				DatabaseOpenHelper.PHOTO_path,
//				DatabaseOpenHelper.PHOTO_date,
//				DatabaseOpenHelper.PHOTO_folderid,
//				DatabaseOpenHelper.PHOTO_favorite };
//
//		final Calendar c = Calendar.getInstance();
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH);
//		int day = c.get(Calendar.DAY_OF_MONTH);
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		int munite = c.get(Calendar.MINUTE);
//		String mymonth = (month+1)+"";
//		String myday = (day)+"";
//		if (mymonth.length() == 1) {
//			mymonth = "0"+mymonth;
//		}
//		if (myday.length() == 1) {
//			myday = "0"+myday;
//		}
//		String cDate = year+ "-" +mymonth + "-" + myday;
//
//		c.add(Calendar.DAY_OF_YEAR, -3);
//		Date newDate = c.getTime();
//		String bDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(newDate);
//
//		Log.i("cDate -- bDate ", cDate+"--"+bDate);
//
//		String where = " date BETWEEN ? AND ?";
//		String[] args = {bDate, cDate };
//
//		Cursor cursor = database.query(DatabaseOpenHelper.TABLE_photo, columns, where, args, null, null, null);
//
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//
//			for (int i = 0; i < cursor.getCount(); i++) {
//				String id = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.DEVICE_id));
//				String title = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_title));
//				String path = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_path));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_date));
//				String folderid = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_folderid));
//				String favorite = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_favorite));
//
//				Photo p = new Photo(id, title, path, date, folderid, favorite);
//				photos.add(p);
//				cursor.moveToNext();
//			}
//		}
//
//		cursor.close();
//		close();
//		return photos;
//	}
//
//	public Vector<Photo> getFavoritePhoto() {
//		open();
//		Vector<Photo> photos = new Vector<Photo>();
//
//		String[] columns = {DatabaseOpenHelper.DEVICE_id,
//				DatabaseOpenHelper.PHOTO_title,
//				DatabaseOpenHelper.PHOTO_path,
//				DatabaseOpenHelper.PHOTO_date,
//				DatabaseOpenHelper.PHOTO_folderid,
//				DatabaseOpenHelper.PHOTO_favorite };
//
//		Cursor cursor = database.query(DatabaseOpenHelper.TABLE_photo, columns, "favorite='1'", null, null, null, null);
//
//		if (cursor.getCount() > 0) {
//			cursor.moveToFirst();
//
//			for (int i = 0; i < cursor.getCount(); i++) {
//				String id = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.DEVICE_id));
//				String title = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_title));
//				String path = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_path));
//				String date = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_date));
//				String folderid = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_folderid));
//				String favorite = cursor.getString(cursor
//						.getColumnIndex(DatabaseOpenHelper.PHOTO_favorite));
//
//				Photo p = new Photo(id, title, path, date, folderid, favorite);
//				photos.add(p);
//				cursor.moveToNext();
//			}
//		}
//
//		cursor.close();
//		close();
//		return photos;
//	}
//
	public long updateDevice(String id, String title, String type, String icon, String roomId) {
		open();
		ContentValues cv = new ContentValues();
        cv.put(DatabaseOpenHelper.DEVICE_name, title);
        cv.put(DatabaseOpenHelper.DEVICE_type, type);
        cv.put(DatabaseOpenHelper.DEVICE_icon_name, icon);
        cv.put(DatabaseOpenHelper.DEVICE_room_id, roomId);
		long l = database.update(DatabaseOpenHelper.TABLE_device, cv, DatabaseOpenHelper.DEVICE_id +"='"+id+"'", null);
		close();
		return l;
	}
//
//	public long updateFavoritePhoto(String id, String favorite) {
//		open();
//		ContentValues cv = new ContentValues();
//		cv.put(DatabaseOpenHelper.PHOTO_favorite, favorite);
//		long l = database.update(DatabaseOpenHelper.TABLE_photo, cv, DatabaseOpenHelper.DEVICE_id +"="+id, null);
//		close();
//		return l;
//	}
//
//	public long deletePhoto(String id) {
//		open();
//		long l = database.delete(DatabaseOpenHelper.TABLE_photo, "id=?", new String[] { id });
//		close();
//		return l;
//	}
//
//	public long deleteAllPhoto() {
//		open();
//		long l = database.delete(DatabaseOpenHelper.TABLE_photo, null, null);
//		close();
//		return l;
//	}
//
	public long addAction(String device, String name, String time) {
		open();
		ContentValues cv = new ContentValues();
		cv.put(DatabaseOpenHelper.action_device, device);
		cv.put(DatabaseOpenHelper.action_name, name);
		cv.put(DatabaseOpenHelper.action_time, time);
		long l = database.insert(DatabaseOpenHelper.TABLE_action, "", cv);
		close();
		return l;
	}

    public Vector<Action> getActionsByDevice(String device) {
        open();
        Vector<Action> actions = new Vector<Action>();

        String[] columns = {DatabaseOpenHelper.action_device,DatabaseOpenHelper.action_name,
                DatabaseOpenHelper.action_time };

        Cursor cursor = database.query(DatabaseOpenHelper.TABLE_action, columns, " device = '"+ device +"'", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String d = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_device));
                String name = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_name));
                String time = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_time));

                Action f = new Action(d,name,time);
                actions.add(f);
                cursor.moveToNext();
            }
        }

        cursor.close();
        close();
        return actions;
    }

    public Vector<Action> getActionsByTime(String t) {
        open();
        Vector<Action> actions = new Vector<Action>();

        String[] columns = {DatabaseOpenHelper.action_device,DatabaseOpenHelper.action_name,
                DatabaseOpenHelper.action_time };

        if (t.equalsIgnoreCase("0")){
            q = "SELECT * from " + DatabaseOpenHelper.TABLE_action;
        } else {
            q = "SELECT * from " + DatabaseOpenHelper.TABLE_action + " WHERE time >= datetime('now','-"+t+" hour')";
        }
		Log.i("QUERY : ", q);
		Cursor cursor = database.rawQuery(q, null);
//        Cursor cursor = database.query(DatabaseOpenHelper.TABLE_action, columns, " device = '"+ t +"'", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String d = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_device));
                String name = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_name));
                String time = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.action_time));

                Action f = new Action(d,name,time);
                actions.add(f);
                cursor.moveToNext();
            }
        }

        cursor.close();
        close();
        return actions;
    }

    public long addRoom(String name, String iconName) {
		open();
		ContentValues cv = new ContentValues();
		cv.put(DatabaseOpenHelper.ROOM_name, name);
		cv.put(DatabaseOpenHelper.ROOM_icon_name, iconName);
		long l = database.insert(DatabaseOpenHelper.TABLE_room, "", cv);
		close();
		return l;
	}

	public Vector<Room> getAllRoomsData() {
		open();
		Vector<Room> rooms = new Vector<Room>();

		String[] columns = {DatabaseOpenHelper.ROOM_id,DatabaseOpenHelper.ROOM_icon_name,
				DatabaseOpenHelper.ROOM_name };

		Cursor cursor = database.query(DatabaseOpenHelper.TABLE_room, columns, null, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			for (int i = 0; i < cursor.getCount(); i++) {
				String id = cursor.getString(cursor
						.getColumnIndex(DatabaseOpenHelper.ROOM_id));
				String name = cursor.getString(cursor
						.getColumnIndex(DatabaseOpenHelper.ROOM_name));
                String icon = cursor.getString(cursor
						.getColumnIndex(DatabaseOpenHelper.ROOM_icon_name));

                Room f = new Room(id,name,icon,false);
				rooms.add(f);
				cursor.moveToNext();
			}
		}

		cursor.close();
		close();
		return rooms;
	}

    public Room getRoomById(String rid) {
        open();
        Room room = null;
        String[] columns = {DatabaseOpenHelper.ROOM_id,
                DatabaseOpenHelper.ROOM_name,
                DatabaseOpenHelper.ROOM_icon_name };

        Cursor cursor = database.query(DatabaseOpenHelper.TABLE_room, columns, " id = '"+ rid +"'", null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String id = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.ROOM_id));
                String title = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.ROOM_name));
                String iconName = cursor.getString(cursor
                        .getColumnIndex(DatabaseOpenHelper.ROOM_icon_name));

                room = new Room(id,title,iconName,false);
//				photos.add(p);
                cursor.moveToNext();
            }
        }

        cursor.close();
        close();
        return room;
    }

//	public long updatePhotoFolder(String id, String title) {
//		open();
//		ContentValues cv = new ContentValues();
//		cv.put(DatabaseOpenHelper.PHOTO_folder_title, title);
//		long l = database.update(DatabaseOpenHelper.TABLE_photoFolder, cv, DatabaseOpenHelper.PHOTO_folder_id+"="+id, null);
//		close();
//		return l;
//	}
//
	public long deleteRoom(String id) {
        deleteDevices(id);
		open();
		long l = database.delete(DatabaseOpenHelper.TABLE_room, "id=?", new String[] { id });
		close();
		return l;
	}

    public long deleteDevices(String id) {
		open();
		long l = database.delete(DatabaseOpenHelper.TABLE_device, "device_room_id=?", new String[] { id });
		close();
		return l;
	}

	public boolean checkIfExist(String tablename, String id) {
		open();
		Cursor cursor = database.query(tablename,
				new String[] { "id" }, "id = ?",
				new String[] { id }, null, null, null, null);

		if (cursor.moveToFirst()) {
			return true;
		} else {
			return false;
		}
	}
	
}
