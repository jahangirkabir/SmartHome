package com.ngm.smartot;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ngm.smartot.Model.Room;
import com.ngm.smartot.db.DatabaseAdapter;
import com.thalmic.myo.Hub;
import com.thalmic.myo.scanner.ScanActivity;

import java.text.SimpleDateFormat;
import java.util.Vector;

public class MainActivity extends ActionBarActivity {
    Button allDeviceButton;
    Context context;
    GridView gridView;
    public static MainActivity mMainActivity;
    DatabaseAdapter dbAdapter;
    SharedPreferences sharedPref;
    boolean firstRun = true;
    ActionBar actionBar;
    private Menu menu;
    private BroadcastReceiver receiver;

    public static Activity getActivity() {
        return mMainActivity;
    }
    RoomGridAdapter roomGridAdapter;
    int wavein = 0, waveout = 0;
    boolean allDeviceSelected = false, gridSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.smart_home_icon);
        actionBar.setTitle(" Smart OT");

        context = this;
        mMainActivity = this;
        dbAdapter = new DatabaseAdapter(context);

        allDeviceButton = (Button) findViewById(R.id.allDeviceButton);
        gridView = (GridView) findViewById(R.id.gridView);

        sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        firstRun = sharedPref.getBoolean("firstRun", true);

        if (firstRun) {
            if (dbAdapter.addRoom("Bed Room", "bed_room") > 0) {
                Log.i("Added Ssuccessfull ", " <<< Bed Room");
            } else {
                Log.i("Added Unsuccessfull ", " <<< Bed Room");
            }

            if (dbAdapter.addRoom("Dining Room", "dining_room") > 0) {
                Log.i("Added Ssuccessfull ", " <<< Dining Room");
            } else {
                Log.i("Added Unsuccessfull ", " <<< Dining Room");
            }

            if (dbAdapter.addRoom("Living Room", "drawing_room") > 0) {
                Log.i("Added Ssuccessfull ", " <<< Living Room");
            } else {
                Log.i("Added Unsuccessfull ", " <<< Living Room");
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
        }

        AppConstant.rooms = dbAdapter.getAllRoomsData();
        AppConstant.rooms.add(new Room("x", "Add Room", "add_button",false));
        roomGridAdapter = new RoomGridAdapter(context, AppConstant.rooms);
        gridView.setAdapter(roomGridAdapter);

        allDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConstant.room = "All Devices";
                startActivity(new Intent(context, AllDevicesActivity.class));
            }
        });

// First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Next, register for DeviceListener callbacks.
//        hub.addListener(mListener);
//        updateMenuTitles();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long date = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                String cTime = sdf.format(date);
                String type = intent.getStringExtra("type");

                if (type.equalsIgnoreCase("onArmSync")) {
                    MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                    myoMenuItem.setIcon(R.mipmap.myo_green_ic);
                } else if (type.equalsIgnoreCase("onArmUnsync")) {
                    MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                    myoMenuItem.setIcon(R.mipmap.myo_yellow_ic);
                } else if (type.equalsIgnoreCase("poseFIST")) {
                    Toast.makeText(context, "FIST", Toast.LENGTH_SHORT).show();
                    performAction();
                } else if (type.equalsIgnoreCase("poseWAVE_IN")) {
                    wavein++;
                    if (wavein == AppConstant.rooms.size()+3){
                        wavein = AppConstant.rooms.size()+2;
                    }

                    if (wavein == 1){
                        allDeviceSelected = true;
                        gridSelected = false;
                        allDeviceButton.setBackgroundColor(Color.parseColor("#80000000"));
                        for (int i=0; i<AppConstant.rooms.size(); i++) {
                            AppConstant.rooms.get(i).setSelected(false);
                        }
                        roomGridAdapter.updateData(AppConstant.rooms);
                    } else if(wavein > 1){
                        allDeviceSelected = false;
                        allDeviceButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        gridSelected = true;
                        if (wavein < AppConstant.rooms.size()+2){
                            for (int i=0; i<AppConstant.rooms.size(); i++) {
                                AppConstant.rooms.get(i).setSelected(false);
                                AppConstant.rooms.get(wavein-2).setSelected(true);
                            }
                        } else {
                            gridSelected = false;
                            for (int i=0; i<AppConstant.rooms.size(); i++) {
                                AppConstant.rooms.get(i).setSelected(false);
                            }
                        }
                        roomGridAdapter.updateData(AppConstant.rooms);
                    }
                    dbAdapter.addAction("nev", "Wrist Flexion", cTime);
                    showToast("WAVE_IN");
                } else if (type.equalsIgnoreCase("poseWAVE_OUT")) {
                    wavein--;
                    if (wavein == -1){
                        wavein = 0;
                    }
                    if (wavein == 1){
                        gridSelected = false;
                        allDeviceSelected = true;
                        allDeviceButton.setBackgroundColor(Color.parseColor("#80000000"));
                        for (int i=0; i<AppConstant.rooms.size(); i++) {
                            AppConstant.rooms.get(i).setSelected(false);
                        }
                        roomGridAdapter.updateData(AppConstant.rooms);
                    } else if (wavein > 1){
                        allDeviceSelected = false;
                        allDeviceButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        gridSelected = true;
                        if (wavein < AppConstant.rooms.size()+2){
                            for (int i=0; i<AppConstant.rooms.size(); i++) {
                                AppConstant.rooms.get(i).setSelected(false);
                                AppConstant.rooms.get(wavein-2).setSelected(true);
                            }
                        } else {
                            gridSelected = false;
                            for (int i=0; i<AppConstant.rooms.size(); i++) {
                                AppConstant.rooms.get(i).setSelected(false);
                            }
                        }
                        roomGridAdapter.updateData(AppConstant.rooms);
                    }
                    showToast("WAVE_OUT");
                    dbAdapter.addAction("nev", "Wrist Extension", cTime);
                }
            }
        };
        showMyoStatus();
    }

    private void performAction() {
        if (wavein == 1 && allDeviceSelected){
            AppConstant.room = "All Devices";
            Intent i = new Intent(context, AllDevicesActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            if (gridSelected){
                AppConstant.cRoomId = AppConstant.rooms.get(wavein-2).getRoomId();
                AppConstant.room = AppConstant.rooms.get(wavein-2).getRoomName();
                AppConstant.roomIconName = AppConstant.rooms.get(wavein-2).getRoomIcon();
                if (AppConstant.rooms.get(wavein-2).getRoomId().equalsIgnoreCase("x")) {
                    showAddRoomDialog();
                } else {
                    Intent i = new Intent(context, RoomDevicesActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        }
    }
    private Toast mToast;
    String TAG = "SMARTHOME";
    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(BackgroundService.SH_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    private class RoomGridAdapter extends ArrayAdapter<Room> {
        Context context;
        Vector<Room> rooms;

        RoomGridAdapter(Context context, Vector<Room> rooms) {
            super(context, R.layout.room_item, AppConstant.rooms);
            this.context = context;
            this.rooms = rooms;
//            Log.e("ROOMS :  ", ": " + AppConstant.rooms);
        }

        public void updateData(Vector<Room> rooms) {
            this.rooms = rooms;
            notifyDataSetChanged();
        }

        private class ViewHolder {
            TextView titleNameTextView;
            ImageView iconImageView;
            LinearLayout itemLayout;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.room_item, null);
                holder = new ViewHolder();
                holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
                holder.titleNameTextView = (TextView) convertView.findViewById(R.id.titleTextView);
                holder.itemLayout = (LinearLayout) convertView.findViewById(R.id.itemLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.itemLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            if (position < rooms.size()) {
                final Room query = rooms.elementAt(position);
                holder.titleNameTextView.setText("" + query.getRoomName());
                holder.iconImageView.setImageResource(getResourceId(query.getRoomIcon()));
                if (query.isSelected()){
                    holder.itemLayout.setBackgroundColor(Color.parseColor("#80000000"));
                }
//                try {
//                    Picasso.with(con)
//                            .load(BaseURL.HTTP + query.getImage())
//                            .placeholder(R.drawable.licon)
//                            .error(R.drawable.licon)
//                            .into(holder.iconImageView);
//                } catch (final Exception e) {
//                    e.printStackTrace();
//                }

                convertView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.e("data ", ": " + query.getRoomName() + query.getRoomId() + query.getRoomIcon());
                        AppConstant.cRoomId = query.getRoomId();
                        AppConstant.room = query.getRoomName();
                        AppConstant.roomIconName = query.getRoomIcon();
                        if (query.getRoomId().equalsIgnoreCase("x")) {
                            showAddRoomDialog();
                        } else {
                            startActivity(new Intent(context, RoomDevicesActivity.class));
                        }
                    }
                });
            }
            return convertView;

        }
    }

    public int getResourceId(String pVariableName) {
        try {
            return context.getResources().getIdentifier(pVariableName, "mipmap", context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //    int[] values = new int[] { R.drawable.bed_room,R.drawable.dining_room,R.drawable.drawing_room,R.drawable.bed_room,R.drawable.dining_room,R.drawable.drawing_room,R.drawable.bed_room,R.drawable.bed_room };
    String[] values = new String[]{"bed_room", "dining_room", "drawing_room",
            "balcony", "bathroom", "children_room",
            "hallway", "kitchen", "drawing_room"};

    private void showAddRoomDialog() {
        final Dialog dialog = new Dialog(context);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_room_view);
//                LayoutInflater inflater = getLayoutInflater();
//                View view = inflater.inflate(R.layout.add_device_view, null);
//                builder.setView(view);

        TextView saveButton = (Button) dialog.findViewById(R.id.saveButton);
        TextView cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        TextView titleTextView = (TextView) dialog.findViewById(R.id.titleTextView);
        final ImageView imageView = (ImageView) dialog.findViewById(R.id.imageView);
        final EditText titleEditText = (EditText) dialog.findViewById(R.id.titleEditText);
//                titleTextView.setText("Lifx bulb " + AppConstant.cLifx.getDeviceID());
        AppConstant.cRoomImageName = values[0];
        titleTextView.setVisibility(View.GONE);
//        dialog.setTitle("Lifx bulb " + AppConstant.cLifx.getDeviceID());
        dialog.setTitle("Add New Room");
//                titleEditText.setText(cPhoto.getDate());
        GridView gridview = (GridView) dialog.findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(context));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                Toast.makeText(context, "" + position, Toast.LENGTH_SHORT).show();
                imageView.setImageResource(getResourceId(values[position]));
                AppConstant.cRoomImageName = values[position];
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if (title.length() > 0) {
                    dialog.dismiss();
                    if (dbAdapter.addRoom(title, AppConstant.cRoomImageName) > 0) {
                        Toast.makeText(context, "Room Added Successfully", Toast.LENGTH_SHORT).show();
                        reloadGridData();
                    } else {
                        Toast.makeText(context, "opps! Error in Add", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please add title to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void reloadGridData() {
        AppConstant.rooms = dbAdapter.getAllRoomsData();
        AppConstant.rooms.add(new Room("x", "Add Room", "add_button",false));
        roomGridAdapter = new RoomGridAdapter(context, AppConstant.rooms);
        gridView.setAdapter(roomGridAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_myo) {
            startService(new Intent(this, BackgroundService.class));
            onScanActionSelected();

            return true;
        } else if (id == R.id.action_statistics) {
            startActivity(new Intent(this, StatisticsActivity2.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(context, BackgroundService.class));
    }

    @Override
    protected void onResume() {
        reloadGridData();
        showMyoStatus();
        super.onResume();
    }

    private void showMyoStatus() {
        if (menu != null){
            if (AppConstant.myosynced) {
                MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                myoMenuItem.setIcon(R.mipmap.myo_green_ic);
            } else {
                MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                myoMenuItem.setIcon(R.mipmap.myo_yellow_ic);
            }
        }
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return values.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(155, 155));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(10, 10, 10, 10);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(getResourceId(values[position]));
            return imageView;
        }

        // references to our images
//        private Integer[] mThumbIds = {
//                R.drawable.bed_room, R.drawable.dining_room, R.drawable.drawing_room, R.drawable.bed_room, R.drawable.dining_room, R.drawable.drawing_room, R.drawable.bed_room, R.drawable.bed_room
//        };
    }
}
