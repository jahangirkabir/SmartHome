package com.ngm.smartot;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ngm.smartot.Hue.HueSharedPreferences;
import com.ngm.smartot.Hue.PHPushlinkActivity;
import com.ngm.smartot.Hue.PHWizardAlertDialog;
import com.ngm.smartot.Model.Device;
import com.ngm.smartot.Model.Room;
import com.ngm.smartot.db.DatabaseAdapter;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.thalmic.myo.scanner.ScanActivity;

import java.util.ArrayList;
import java.util.List;

import lifx.java.android.light.LFXLight;


public class HueListActivity extends ActionBarActivity {
    Context context;
    ActionBar actionBar;
    ProgressDialog pd;
    private WifiManager.MulticastLock ml = null;
    ListView deviceListView;
    DevicesListAdapter devicesListAdapter;

    private PHHueSDK phHueSDK;
    public static final String TAG = "SMARTOT";
    private HueSharedPreferences prefs;
//    private AccessPointListAdapter adapter;

    private boolean lastSearchWasIPScan = false;
    ArrayList<Device> devices;

    List<PHAccessPoint> allAccessPoint;
    private Menu menu;
    private BroadcastReceiver receiver;
    DatabaseAdapter dbAdapter;
    ArrayList<LFXLight> allLifxs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alldevice);
        context = this;
        devices = new ArrayList<Device>();

        actionBar = getSupportActionBar();
        actionBar.setTitle(AppConstant.room);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.smart_home_icon);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        deviceListView = (ListView) findViewById(R.id.deviceListView);

        // Gets an instance of the Hue SDK.
        phHueSDK = PHHueSDK.create();

        // Set the allDevices Name (name of your app). This will be stored in your bridge whitelist entry.
        phHueSDK.setAppName("SmartOT");
        phHueSDK.setDeviceName(android.os.Build.MODEL);

        // Register the PHSDKListener to receive callbacks from the bridge.
        phHueSDK.getNotificationManager().registerSDKListener(listener);

//        adapter = new AccessPointListAdapter(getApplicationContext(), phHueSDK.getAccessPointsFound());
//
//        ListView accessPointList = (ListView) findViewById(R.id.bridge_list);

//        deviceListView.setAdapter(devicesListAdapter);


        // Try to automatically connect to the last known bridge.  For first time use this will be empty so a bridge search is automatically started.
        prefs = HueSharedPreferences.getInstance(getApplicationContext());
        String lastIpAddress   = prefs.getLastConnectedIPAddress();
        String lastUsername    = prefs.getUsername();

        // Automatically try to connect to the last connected IP Address.  For multiple bridge support a different implementation is required.
//        if (lastIpAddress !=null && !lastIpAddress.equals("")) {
//            PHAccessPoint lastAccessPoint = new PHAccessPoint();
//            lastAccessPoint.setIpAddress(lastIpAddress);
//            lastAccessPoint.setUsername(lastUsername);
//
//            if (!phHueSDK.isAccessPointConnected(lastAccessPoint)) {
//                PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, context);
//                phHueSDK.connect(lastAccessPoint);
//            }
//        }
//        else {  // First time use, so perform a bridge search.
//            doBridgeSearch();
//        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String type = intent.getStringExtra("type");
                if (type.equalsIgnoreCase("onArmSync")) {
                    AppConstant.myosynced = true;
                    MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                    myoMenuItem.setIcon(R.mipmap.myo_green_ic);
                } else if (type.equalsIgnoreCase("onArmUnsync")) {
                    AppConstant.myosynced = false;
                    MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
                    myoMenuItem.setIcon(R.mipmap.myo_yellow_ic);
                }
            }
        };
        showMyoStatus();
    }

    @Override
    protected void onResume() {
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

    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {

            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }

            phHueSDK.disconnect(bridge);
        }super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alldevice, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_myo) {
            startService(new Intent(this, BackgroundService.class));
            onScanActionSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

//    private void updateStateFromLIFX()
//
//    {
//        doBridgeSearch();
//        allLifxs = localNetworkContext.getAllLightsCollection().getLights();
//        for (LFXLight allLight : allLifxs) {
//            Device device = new Device();
//            device.setDeviceId(allLight.getDeviceID());
//            device.setDeviceType("lifx");
//            devices.add(device);
//        }
////        Log.i("updateStateFromLIFX",devices.toString());
//        devicesListAdapter = new DevicesListAdapter(context, devices);
//        deviceListView.setAdapter(devicesListAdapter);
//    }

    PHBridge bridge;
    List<PHLight> phLights;
    // Local SDK Listener
    private PHSDKListener listener = new PHSDKListener() {

        @Override
        public void onAccessPointsFound(List<PHAccessPoint> accessPoint) {
            Log.w(TAG, "Access Points Found # " + accessPoint.size());
//            allAccessPoint = accessPoint;
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            if (accessPoint != null && accessPoint.size() > 0) {
                phHueSDK.getAccessPointsFound().clear();
                phHueSDK.getAccessPointsFound().addAll(accessPoint);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        adapter.updateData(phHueSDK.getAccessPointsFound());
                        for (PHAccessPoint phAccessPoint : phHueSDK.getAccessPointsFound()) {
                            devices.add(new Device(phAccessPoint.getMacAddress(), "","","hue","",false));
                        }
//                        bridge = phHueSDK.getSelectedBridge();
//                        phLights = bridge.getResourceCache().getAllLights();
//                        for (PHLight phLight : phLights) {
//                            Log.i("phLight",phLight.getModelNumber() + "-----" +phLight.getVersionNumber()+ "-----" +phLight.getName());
//                        }
                        Log.i("onAccessPointsFound",devices.toString());
//                        devicesListAdapter.updateData(devices);
                        devicesListAdapter = new DevicesListAdapter(context, devices);
                        deviceListView.setAdapter(devicesListAdapter);
                    }
                });

            }

        }

        @Override
        public void onCacheUpdated(List<Integer> arg0, PHBridge bridge) {
            Log.w(TAG, "On CacheUpdated");

        }

        @Override
        public void onBridgeConnected(PHBridge b) {
            phHueSDK.setSelectedBridge(b);
            phHueSDK.enableHeartbeat(b, PHHueSDK.HB_INTERVAL);
            phHueSDK.getLastHeartbeat().put(b.getResourceCache().getBridgeConfiguration() .getIpAddress(), System.currentTimeMillis());
            prefs.setLastConnectedIPAddress(b.getResourceCache().getBridgeConfiguration().getIpAddress());
            prefs.setUsername(prefs.getUsername());
            PHWizardAlertDialog.getInstance().closeProgressDialog();
            startMainActivity();
        }

        @Override
        public void onAuthenticationRequired(PHAccessPoint accessPoint) {
            Log.w(TAG, "Authentication Required.");
            phHueSDK.startPushlinkAuthentication(accessPoint);
            startActivity(new Intent(HueListActivity.this, PHPushlinkActivity.class));

        }

        @Override
        public void onConnectionResumed(PHBridge bridge) {
            if (HueListActivity.this.isFinishing())
                return;

            Log.v(TAG, "onConnectionResumed" + bridge.getResourceCache().getBridgeConfiguration().getIpAddress());
            phHueSDK.getLastHeartbeat().put(bridge.getResourceCache().getBridgeConfiguration().getIpAddress(),  System.currentTimeMillis());
            for (int i = 0; i < phHueSDK.getDisconnectedAccessPoint().size(); i++) {

                if (phHueSDK.getDisconnectedAccessPoint().get(i).getIpAddress().equals(bridge.getResourceCache().getBridgeConfiguration().getIpAddress())) {
                    phHueSDK.getDisconnectedAccessPoint().remove(i);
                }
            }

        }

        @Override
        public void onConnectionLost(PHAccessPoint accessPoint) {
            Log.v(TAG, "onConnectionLost : " + accessPoint.getIpAddress());
            if (!phHueSDK.getDisconnectedAccessPoint().contains(accessPoint)) {
                phHueSDK.getDisconnectedAccessPoint().add(accessPoint);
            }
        }

        @Override
        public void onError(int code, final String message) {
            Log.e(TAG, "on Error Called : " + code + ":" + message);
//            devicesListAdapter = new DevicesListAdapter(context, devices);
//            deviceListView.setAdapter(devicesListAdapter);
            if (code == PHHueError.NO_CONNECTION) {
                Log.w(TAG, "On No Connection");
            }
            else if (code == PHHueError.AUTHENTICATION_FAILED || code==1158) {
                PHWizardAlertDialog.getInstance().closeProgressDialog();
            }
            else if (code == PHHueError.BRIDGE_NOT_RESPONDING) {
                Log.w(TAG, "Bridge Not Responding . . . ");
                PHWizardAlertDialog.getInstance().closeProgressDialog();
                HueListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PHWizardAlertDialog.showErrorDialog(HueListActivity.this, message, R.string.btn_ok);
                    }
                });

            }
            else if (code == PHMessageType.BRIDGE_NOT_FOUND) {

                if (!lastSearchWasIPScan) {  // Perform an IP Scan (backup mechanism) if UPNP and Portal Search fails.
                    phHueSDK = PHHueSDK.getInstance();
                    PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
                    sm.search(false, false, true);
                    lastSearchWasIPScan=true;
                }
                else {
                    PHWizardAlertDialog.getInstance().closeProgressDialog();
                    HueListActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PHWizardAlertDialog.showErrorDialog(HueListActivity.this, message, R.string.btn_ok);
                        }
                    });
                }


            }
        }

        @Override
        public void onParsingErrors(List<PHHueParsingError> parsingErrorsList) {
            for (PHHueParsingError parsingError: parsingErrorsList) {
                Log.e(TAG, "ParsingError : " + parsingError.getMessage());
            }
        }
    };

    // Starting the main activity this way, prevents the PushLink Activity being shown when pressing the back button.
    public void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), HueDetailsActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            intent.addFlags(0x8000); // equal to Intent.FLAG_ACTIVITY_CLEAR_TASK which is only available from API level 11
        startActivity(intent);
    }

    public void doBridgeSearch() {
        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.search_progress, HueListActivity.this);
        PHBridgeSearchManager sm = (PHBridgeSearchManager) phHueSDK.getSDKService(PHHueSDK.SEARCH_BRIDGE);
        // Start the UPNP Searching of local bridges.
        sm.search(true, true);
    }

    private class DevicesListAdapter extends ArrayAdapter<Device> {
        Context context;
        ArrayList<Device> allDevices;
        private boolean unknown;

        DevicesListAdapter(Context context, ArrayList<Device> allLights) {
            super(context, R.layout.device_item, allLights);
            this.context = context;
            this.allDevices = allLights;
//            Log.e("ROOMS :  ", ": " + AppConstant.rooms);
        }

        private class ViewHolder {
            TextView titleNameTextView;
            TextView locationTextView;
            ImageView iconImageView;
        }

        public void updateData(ArrayList<Device> allLights) {
            this.allDevices = allLights;
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.device_item, null);
                holder = new ViewHolder();
                holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
                holder.titleNameTextView = (TextView) convertView.findViewById(R.id.titleTextView);
                holder.locationTextView = (TextView) convertView.findViewById(R.id.locationTextView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            unknown = true;
//            final LFXLight query = allDevices.get(position);
            final Device device = allDevices.get(position);

            if (device.getDeviceType().equalsIgnoreCase("lifx")){
//                holder.titleNameTextView.setText("Lifx " + device.getDeviceId().substring(6));
                holder.titleNameTextView.setText("Lifx " + device.getDeviceId());
            } else if(device.getDeviceType().equalsIgnoreCase("hue")){
                holder.titleNameTextView.setText("Philips Hue " + device.getDeviceId());
            }

            holder.locationTextView.setVisibility(View.GONE);
            final Device d = dbAdapter.getDeviceById(device.getDeviceId());
            if (d != null){
                unknown = false;
                holder.titleNameTextView.setText(d.getDeviceName());
                AppConstant.device = d.getDeviceName();
                Room r = dbAdapter.getRoomById(d.getDeviceRoomID());
                holder.locationTextView.setVisibility(View.VISIBLE);
                holder.locationTextView.setText(r.getRoomName());
            }


            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.e("D ID ", " " + device.getDeviceId());
                    unknown = true;
                    if (d != null){
                        unknown = false;
                    }
                    if (device.getDeviceType().equalsIgnoreCase("lifx")){
                        if (unknown){
//                            AppConstant.device = "Lifx " + device.getDeviceId().substring(6);
                            AppConstant.device = "Lifx " + device.getDeviceId();
                        } else{
                            AppConstant.device = d.getDeviceName();
                        }
                        AppConstant.deviceid = "" + device.getDeviceId();
                        for (LFXLight allLifx : allLifxs) {
                            if (device.getDeviceId().equalsIgnoreCase(allLifx.getDeviceID())){
                                AppConstant.cLifx = allLifx;
                                break;
                            }
                        }
                        startActivity(new Intent(context, LifxDetailsActivity.class));
                    } else if(device.getDeviceType().equalsIgnoreCase("hue")){
                        if (unknown){
                            AppConstant.device = "Philips Hue " + device.getDeviceId();
                        }else{
                            AppConstant.device = d.getDeviceName();
                        }
                        AppConstant.deviceid = "" + device.getDeviceId();

                        HueSharedPreferences prefs = HueSharedPreferences.getInstance(getApplicationContext());
                        PHAccessPoint accessPoint = null;
                        for (PHAccessPoint ap : phHueSDK.getAccessPointsFound()) {
                            if (ap.getMacAddress().equalsIgnoreCase(device.getDeviceId())){
                                accessPoint = ap;
                                break;
                            }
                        }

//                            PHAccessPoint accessPoint = phHueSDK.getAccessPointsFound().get(position);
                        accessPoint.setUsername(prefs.getUsername());

                        PHBridge connectedBridge = phHueSDK.getSelectedBridge();

                        if (connectedBridge != null) {
                            String connectedIP = connectedBridge.getResourceCache().getBridgeConfiguration().getIpAddress();
                            if (connectedIP != null) {   // We are already connected here:-
                                phHueSDK.disableHeartbeat(connectedBridge);
                                phHueSDK.disconnect(connectedBridge);
                            }
                        }
                        PHWizardAlertDialog.getInstance().showProgressDialog(R.string.connecting, HueListActivity.this);
                        phHueSDK.connect(accessPoint);
//                            startActivity(new Intent(context, HueDetailsActivity.class));
                    }
                }
            });

            return convertView;

        }
    }

}
