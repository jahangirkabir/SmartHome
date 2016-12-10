package com.ngm.smartot;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ngm.smartot.Model.Room;
import com.ngm.smartot.db.DatabaseAdapter;
import com.ngm.smartot.db.DatabaseOpenHelper;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.thalmic.myo.scanner.ScanActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import lifx.java.android.entities.LFXHSBKColor;
import lifx.java.android.network_context.LFXNetworkContext;

public class HueDetailsActivity extends ActionBarActivity {
    Button editButton,button2,button3,button4;
    TextView powerTextView;
    CompoundButton lightSwitch;
    Context context;
    ActionBar actionBar;
    LFXNetworkContext localNetworkContext;
    private WifiManager.MulticastLock ml = null;
//    LFXLight light;
    ProgressDialog pd;
    SeekBar seekBar, colorSeekBar;

    private TextView mLockStateView;
    private TextView therapyTextView;
    private ImageView indicatorImageView;
    private boolean fist = false, spread;
    boolean firstRoll = false, increasing = false;
    private DatabaseAdapter dbAdapter;
    private Vector<Room> rooms;
    private BroadcastReceiver receiver;
    private Toast mToast;
    String TAG = "SMARTHOME";

    private PHHueSDK phHueSDK;
    private static final int MAX_HUE = 65535;
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
//    public DeviceListener mListener = new AbstractDeviceListener() {
//
//        // onConnect() is called whenever a Myo has been connected.
//        @Override
//        public void onConnect(Myo myo, long timestamp) {
//            // Set the text color of the text view to cyan when a Myo connects.
//            mTextView.setTextColor(Color.CYAN);
//        }
//
//        // onDisconnect() is called whenever a Myo has been disconnected.
//        @Override
//        public void onDisconnect(Myo myo, long timestamp) {
//            // Set the text color of the text view to red when a Myo disconnects.
//            mTextView.setTextColor(Color.RED);
//        }
//
//        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
//        // arm. This lets Myo know which arm it's on and which way it's facing.
//        @Override
//        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
//            mTextView.setText(myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right);
//        }
//
//        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
//        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
//        // when Myo is moved around on the arm.
//        @Override
//        public void onArmUnsync(Myo myo, long timestamp) {
//            mTextView.setText(R.string.unsynced);
//        }
//
//        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
//        // policy, that means poses will now be delivered to the listener.
//        @Override
//        public void onUnlock(Myo myo, long timestamp) {
//            mLockStateView.setText(R.string.unlocked);
//        }
//
//        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
//        // policy, that means poses will no longer be delivered to the listener.
//        @Override
//        public void onLock(Myo myo, long timestamp) {
//            mLockStateView.setText(R.string.locked);
//        }
//
//        // onOrientationData() is called whenever a Myo provides its current orientation,
//        // represented as a quaternion.
//        @Override
//        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
//            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
//            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
//            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
//            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
//
//            // Adjust roll and pitch for the orientation of the Myo on the arm.
//            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
//                roll *= -1;
//                pitch *= -1;
//            }
//
//
//
//            if (fist){
////                Log.i("ROLL", roll+"");
//                if (!firstRoll){
//                    AppConstant.roll =  (int) roll;
//                    firstRoll=true;
//                }
//                increasing = false;
//                if (firstRoll){
//                    AppConstant.roll2 = (int) roll;
////                    Log.i("ROLL -- ROLL2", AppConstant.roll+"<--->"+AppConstant.roll2);
//                    if ((AppConstant.roll + 5) < AppConstant.roll2){
////                        Log.i("ROLL", "INCREASING ---> "+roll+"");
//                        AppConstant.roll = (int) roll;
//                        increasing = true;
//                        AppConstant.powerProgress+=5;
//                        seekBar.setProgress(AppConstant.powerProgress);
//                    }
//                    if (!increasing){
//                        if ((AppConstant.roll - 5) > AppConstant.roll2) {
////                            Log.i("ROLL", "DECREASING ---> "+roll+"");
//                            AppConstant.roll = (int) roll;
//                            AppConstant.powerProgress-=5;
//                            seekBar.setProgress(AppConstant.powerProgress);
//                        }
//                    }
//
//                }
//            }
////            Log.i("YAW", yaw+"");
////            Log.i("PITCH", pitch+"");
//            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
//            mTextView.setRotation(roll);
//            mTextView.setRotationX(pitch);
//            mTextView.setRotationY(yaw);
//        }
//
//        // onPose() is called whenever a Myo provides a new pose.
//        @Override
//        public void onPose(Myo myo, long timestamp, Pose pose) {
//            // Handle the cases of the Pose enumeration, and change the text of the text view
//            // based on the pose we receive.
//            fist = false;
//            switch (pose) {
//                case UNKNOWN:
//                    mTextView.setText(getString(R.string.app_name));
//                    break;
//                case REST:
//                case DOUBLE_TAP:
//                    int restTextId = R.string.app_name;
//
//                    switch (myo.getArm()) {
//                        case LEFT:
//                            restTextId = R.string.arm_left;
//                            break;
//                        case RIGHT:
//                            restTextId = R.string.arm_right;
//                            break;
//                    }
//                    mTextView.setText(getString(restTextId));
//                    break;
//                case FIST:
//                    fist = true;
//                    mTextView.setText(getString(R.string.pose_fist));
////                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
////                    {
////                        lightSwitch.setChecked(false);
////                    }
//                    break;
//                case WAVE_IN:
//                    mTextView.setText(getString(R.string.pose_wavein));
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
//                    {
////                        AppConstant.powerProgress+=10;
////                        seekBar.setProgress(AppConstant.powerProgress);
//                        AppConstant.colorProgress+=10;
//                        colorSeekBar.setProgress(AppConstant.colorProgress);
//                    }
//                    break;
//                case WAVE_OUT:
//                    mTextView.setText(getString(R.string.pose_waveout));
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
//                    {
////                        AppConstant.powerProgress-=10;
////                        Log.i("powerProgress",""+AppConstant.powerProgress);
////                        seekBar.setProgress(AppConstant.powerProgress);
//                        AppConstant.colorProgress-=10;
//                        colorSeekBar.setProgress(AppConstant.colorProgress);
//                    }
//                    break;
//                case FINGERS_SPREAD:
//                    mTextView.setText(getString(R.string.pose_fingersspread));
////                    if (light != null){
//                        if( light.getPowerState() == LFXTypes.LFXPowerState.OFF)
//                        {
//                            lightSwitch.setChecked(true);
//                        } else {
//                            lightSwitch.setChecked(false);
//                        }
////                    }
//                    break;
//            }
//
//            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
//                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
//                // hold the poses without the Myo becoming locked.
//                myo.unlock(Myo.UnlockType.HOLD);
//
//                // Notify the Myo that the pose has resulted in an action, in this case changing
//                // the text on the screen. The Myo will vibrate.
//                myo.notifyUserAction();
//            } else {
//                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
//                // stay unlocked while poses are being performed, but lock after inactivity.
//                myo.unlock(Myo.UnlockType.TIMED);
//            }
//        }
//    };

    private boolean lighton = false;

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
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
//        Hub.getInstance().removeListener(mListener);

//        if (isFinishing()) {
//            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
//            Hub.getInstance().shutdown();
//        }
    }

    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {

        @Override
        public void onSuccess() {
        }

        @Override
        public void onStateUpdate(Map<String, String> arg0, List<PHHueError> arg1) {
            Log.w(TAG, "Light has updated");
        }

        @Override
        public void onError(int arg0, String arg1) {}

        @Override
        public void onReceivingLightDetails(PHLight arg0) {}

        @Override
        public void onReceivingLights(List<PHBridgeResource> arg0) {}

        @Override
        public void onSearchComplete() {}
    };

    int no = 0;
    int wavein = 0;
    boolean bSelected = false, hSelected = false;
    LinearLayout brightnessLayout, hueLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifx_details);
        context = this;
        actionBar = getSupportActionBar();
        actionBar.setTitle(AppConstant.device);
        actionBar.setIcon(R.mipmap.smart_home_icon);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dbAdapter = new DatabaseAdapter(context);

        phHueSDK = PHHueSDK.create();
        final PHBridge bridge = phHueSDK.getSelectedBridge();
        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();

        for (PHLight phLight : allLights) {
            Log.i("phLight",phLight.getModelNumber() + "-----" +phLight.getVersionNumber()+ "-----" +phLight.getName());
        }

        if (AppConstant.unknown){
            String n = AppConstant.device.substring(AppConstant.device.length() - 1, AppConstant.device.length());
            no = Integer.parseInt(n);
        } else {
            String n = AppConstant.deviceid.substring(AppConstant.deviceid.length() - 1, AppConstant.deviceid.length());
            no = Integer.parseInt(n);
        }
//        String n = AppConstant.device.substring(AppConstant.device.length() - 1, AppConstant.device.length());
//        no = Integer.parseInt(n);
//        final List<PHLight> allLights = bridge.getResourceCache().getAllLights();
//        if (AppConstant.cLifx != null)
//            light = AppConstant.cLifx;
//        else finish();

        lightSwitch = (CompoundButton)findViewById(R.id.lightSwitch);
        powerTextView = (TextView)findViewById(R.id.powerTextView);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
//        editButton = (Button)findViewById(R.id.editButton);
        colorSeekBar = (SeekBar)findViewById(R.id.colorSeekBar);

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    showToast("Light On");
                    powerTextView.setText("ON");
//                    resetLight();
                    PHLightState lightState = new PHLightState();
                    lightState.setOn(true);
                    lightState.setHue(1);
                    lightState.setBrightness(100);
                    bridge.updateLightState(allLights.get(no-1), lightState, listener);
//                    for (PHLight allLight : allLights) {
//                        bridge.updateLightState(allLight, lightState, listener);
//                    }
                } else {
                    showToast("Light Off");
                    powerTextView.setText("OFF");
                    PHLightState lightState = new PHLightState();
                    lightState.setOn(false);
                    bridge.updateLightState(allLights.get(no-1), lightState, listener);
//                    for (PHLight allLight : allLights) {
//                        bridge.updateLightState(allLight, lightState, listener);
//                    }
                }
            }
        });

        seekBar.setMax(100);
        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                AppConstant.powerProgress = progress;
//                Toast.makeText(context, progress+"%", Toast.LENGTH_SHORT).show();
                showToast(progress + "%");
//                Log.i("AppConstant.b Bri",""+AppConstant.b);
                AppConstant.b = (float) progress / 100;
//                Log.i("NOW Bri",""+AppConstant.b);
                PHLightState lightState = new PHLightState();
//                lightState.setOn(true);
                lightState.setBrightness(progress*2);
                if (progress < 2){
                    lightState.setBrightness(progress);
                }
//                for (PHLight allLight : allLights) {
//                    bridge.updateLightState(allLight, lightState, listener);
//                }
                bridge.updateLightState(allLights.get(no-1), lightState, listener);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(0, 0, colorSeekBar.getWidth(), 0,
                        new int[] {0xFFFF0000, 0xFFFFFF00, 0xFF00FFFF, 0xFF00FF00, 0xFF00FFFF, 0xFFFF00FF, 0xFFFF0000}, //substitute the correct colors for these
                        null,
                        Shader.TileMode.REPEAT);
                return lg;
            }
        };
        PaintDrawable p = new PaintDrawable();
        p.setShape(new RectShape());
        p.setCornerRadius(50);
        p.setShaderFactory(sf);

        colorSeekBar.setProgressDrawable((Drawable)p);
        colorSeekBar.setMax(360);
        colorSeekBar.setProgress(0);
        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Toast.makeText(context, progress+"%", Toast.LENGTH_SHORT).show();
                AppConstant.colorProgress = progress;
//                Log.i("AppConstant.h ",""+AppConstant.h);
                AppConstant.h = (float)progress;
                AppConstant.s = (float)progress;

                PHLightState lightState = new PHLightState();
//                lightState.setBrightness(AppConstant.b);
                lightState.setHue(progress*180);
//                for (PHLight allLight : allLights) {
//                    bridge.updateLightState(allLight, lightState, listener);
//                }
                bridge.updateLightState(allLights.get(no-1), lightState, listener);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        indicatorImageView = (ImageView) findViewById(R.id.indicatorImageview);
        therapyTextView = (TextView) findViewById(R.id.therapyTextView);
        brightnessLayout = (LinearLayout) findViewById(R.id.brightnessLayout);
        hueLayout = (LinearLayout) findViewById(R.id.hueLayout);

        if (AppConstant.myosynced){
//            mTextView.setTextColor(Color.GREEN);
            indicatorImageView.setImageResource(R.drawable.hand_normal);
        } else {
//            mTextView.setTextColor(Color.RED);
            indicatorImageView.setImageResource(R.drawable.hand_normal);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long date = System.currentTimeMillis();
//                SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
//                Mon Jan 5, 2009 4:55 PM
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                String cTime = sdf.format(date);
                String type = intent.getStringExtra("type");
                if (type.equalsIgnoreCase("onConnect")) {
//                    mTextView.setTextColor(Color.GREEN);
                    indicatorImageView.setImageResource(R.drawable.hand_normal);
                } else if (type.equalsIgnoreCase("onDisconnect")) {
//                    mTextView.setTextColor(Color.RED);
                    indicatorImageView.setImageResource(R.drawable.hand_normal_off);
                } else if (type.equalsIgnoreCase("onArmSync")) {
//                    mTextView.setTextColor(Color.GREEN);
                    indicatorImageView.setImageResource(R.drawable.hand_normal);
//                    mTextView.setText(intent.getStringExtra("msg"));
                } else if (type.equalsIgnoreCase("onArmUnsync")) {
//                    mTextView.setTextColor(Color.RED);
                    indicatorImageView.setImageResource(R.drawable.hand_normal_off);
//                    mTextView.setText(R.string.unsynced);
                } else if (type.equalsIgnoreCase("onUnlock")) {
                    mLockStateView.setText(R.string.unlocked);
                } else if (type.equalsIgnoreCase("onLock")) {
                    mLockStateView.setText(R.string.locked);
                } else if (type.equalsIgnoreCase("onOrientationData")) {

                    if (bSelected){
                        if (intent.getStringExtra("msg").equalsIgnoreCase("INCREASING")){
                            dbAdapter.addAction("lifx", "Hand Supination", cTime);
                            therapyTextView.setText(getString(R.string.handsupination));
                            indicatorImageView.setImageResource(R.drawable.hand_supination);
                            AppConstant.powerProgress+=8;
                        } else if (intent.getStringExtra("msg").equalsIgnoreCase("DECREASING")){
                            dbAdapter.addAction("lifx", "Hand Pronation", cTime);
                            therapyTextView.setText(getString(R.string.handpronation));
                            indicatorImageView.setImageResource(R.drawable.hand_pronation);
                            AppConstant.powerProgress-=8;
                        }
                        seekBar.setProgress(AppConstant.powerProgress);
                    } else if (hSelected){
                        if (intent.getStringExtra("msg").equalsIgnoreCase("INCREASING")){
                            dbAdapter.addAction("lifx", "Hand Supination", cTime);
                            therapyTextView.setText(getString(R.string.handsupination));
                            indicatorImageView.setImageResource(R.drawable.hand_supination);
                            AppConstant.colorProgress+=20;
                        } else if (intent.getStringExtra("msg").equalsIgnoreCase("DECREASING")){
                            dbAdapter.addAction("lifx", "Hand Pronation", cTime);
                            therapyTextView.setText(getString(R.string.handpronation));
                            indicatorImageView.setImageResource(R.drawable.hand_pronation);
                            AppConstant.colorProgress-=20;
                        }
                        colorSeekBar.setProgress(AppConstant.colorProgress);
                    }

//                    seekBar.setProgress(AppConstant.powerProgress);
//                    if (intent.getStringExtra("msg").equalsIgnoreCase("INCREASING")){
//                        dbAdapter.addAction("hue", "Hand Supination", cTime);
//                        therapyTextView.setText(getString(R.string.handsupination));
//                    } else if (intent.getStringExtra("msg").equalsIgnoreCase("DECREASING")){
//                        dbAdapter.addAction("hue", "Hand Pronation", cTime);
//                        therapyTextView.setText(getString(R.string.handpronation));
//                    }
                } else if (type.equalsIgnoreCase("setRollData")) {
//                    indicatorImageView.setRotation(Float.parseFloat(intent.getStringExtra("msg")));
                } else if (type.equalsIgnoreCase("setPitchData")) {
//                    indicatorImageView.setRotationX(Float.parseFloat(intent.getStringExtra("msg")));
                } else if (type.equalsIgnoreCase("setYawData")) {
//                    indicatorImageView.setRotationY(Float.parseFloat(intent.getStringExtra("msg")));
                } else if (type.equalsIgnoreCase("poseUNKNOWN")) {
//                    mTextView.setText(getString(R.string.app_name));
                    indicatorImageView.setImageResource(R.drawable.hand_normal);
                } else if (type.equalsIgnoreCase("poseDOUBLE_TAP")) {
//                    mTextView.setText(intent.getStringExtra("msg"));
                    indicatorImageView.setImageResource(R.drawable.hand_finger_tap);
                } else if (type.equalsIgnoreCase("poseFIST")) {
//                    mTextView.setText(getString(R.string.pose_fist));
                    indicatorImageView.setImageResource(R.drawable.hand_fist);
                    therapyTextView.setText(getString(R.string.fingerflexion));
                } else if (type.equalsIgnoreCase("poseWAVE_IN")) {
                    wavein++;
                    if (wavein >= 3){
                        wavein = 3;
                        bSelected = false;
                        hSelected = false;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        hueLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else if (wavein == 1){
                        bSelected = true;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        hSelected = false;
                        hueLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else if (wavein == 2){
                        hSelected = true;
                        hueLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        bSelected = false;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    }
                    dbAdapter.addAction("hue", "Wrist Flexion", cTime);
                    therapyTextView.setText(getString(R.string.wristflexion));
//                    mTextView.setText(getString(R.string.pose_wavein));
                    indicatorImageView.setImageResource(R.drawable.hand_flexion);
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
//                    {
//                        AppConstant.powerProgress+=10;
//                        seekBar.setProgress(AppConstant.powerProgress);
//                        AppConstant.colorProgress+=10;
//                        colorSeekBar.setProgress(AppConstant.colorProgress);
//                    }
                } else if (type.equalsIgnoreCase("poseWAVE_OUT")) {
                    wavein--;
                    if (wavein <= 0){
                        wavein = 0;
                        bSelected = false;
                        hSelected = false;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        hueLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else if (wavein == 1){
                        bSelected = true;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        hSelected = false;
                        hueLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    } else if (wavein == 2){
                        hSelected = true;
                        hueLayout.setBackgroundColor(Color.parseColor("#80000000"));
                        bSelected = false;
                        brightnessLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    }
                    dbAdapter.addAction("hue", "Wrist Extension", cTime);
                    therapyTextView.setText(getString(R.string.wristextension));
//                    mTextView.setText(getString(R.string.pose_waveout));
                    indicatorImageView.setImageResource(R.drawable.hand_extension);
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
//                    {
//                        AppConstant.powerProgress-=10;
//                        Log.i("powerProgress",""+AppConstant.powerProgress);
//                        seekBar.setProgress(AppConstant.powerProgress);
//                        AppConstant.colorProgress-=10;
//                        colorSeekBar.setProgress(AppConstant.colorProgress);
//                    }
                } else if (type.equalsIgnoreCase("poseFINGERS_SPREAD")) {
                    dbAdapter.addAction("hue", "Finger Extension", cTime);
                    therapyTextView.setText(getString(R.string.fingeextension));
//                    mTextView.setText(getString(R.string.pose_fingersspread));
                    indicatorImageView.setImageResource(R.drawable.hand_finger_spread);
                    if (lighton){
//                        dbAdapter.addAction("hue", "off", cTime);
                        lighton = false;
                        Log.e("LIGHT ON", "turning off");
                        lightSwitch.setChecked(false);
                    } else {
                        lighton = true;
//                        dbAdapter.addAction("hue", "on", cTime);
                        Log.e("LIGHT OFF", "turning on");
                        lightSwitch.setChecked(true);
                    }
//                    if (light != null){
//                        Log.e("LIGHT not null ", "not null");
//                    }
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.OFF)
//                    {
//                        Log.e("LIGHT OFF", "turning on");
//                        lightSwitch.setChecked(true);
//                    } else {
//                        Log.e("LIGHT ON", "turning off");
//                        lightSwitch.setChecked(false);
//                    }
                }
            }
        };

        // First, we initialize the Hub singleton with an application identifier.
//        Hub hub = Hub.getInstance();
//        if (!hub.init(this, getPackageName())) {
//            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
//            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }

        // Next, register for DeviceListener callbacks.
//        hub.addListener(mListener);

    }

    private void resetLight() {
        seekBar.setProgress(50);
        colorSeekBar.setProgress(0);
        AppConstant.h = 0.001f;
        AppConstant.s = 0.01f;
        AppConstant.b = 0.5f;
        AppConstant.k = 3500;
        LFXHSBKColor color = LFXHSBKColor.getColor(AppConstant.h, AppConstant.s, AppConstant.b, AppConstant.k);
//        light.setColor( color);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_details, menu);
        if (AppConstant.myosynced){
            MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
            myoMenuItem.setIcon(R.mipmap.myo_green_ic);
        } else {
            MenuItem myoMenuItem = menu.findItem(R.id.action_myo);
            myoMenuItem.setIcon(R.mipmap.myo_yellow_ic);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
//            onScanActionSelected();
            showEditDialog();
            return true;
        } else if (id == R.id.action_myo) {
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

    private void showEditDialog() {
        final Dialog dialog = new Dialog(context);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.add_device_view);
//                LayoutInflater inflater = getLayoutInflater();
//                View view = inflater.inflate(R.layout.add_device_view, null);
//                builder.setView(view);

        TextView saveButton = (Button) dialog.findViewById(R.id.saveButton);
        TextView cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        TextView titleTextView = (TextView) dialog.findViewById(R.id.titleTextView);
        final EditText titleEditText = (EditText) dialog.findViewById(R.id.titleEditText);
        Spinner roomSpinner = (Spinner) dialog.findViewById(R.id.roomSpinner);
//                titleTextView.setText("Lifx bulb " + AppConstant.cLifx.getDeviceID());
        titleTextView.setVisibility(View.GONE);
//        dialog.setTitle("Lifx bulb " + AppConstant.cLifx.getDeviceID());
        dialog.setTitle(AppConstant.device);
//                titleEditText.setText(cPhoto.getDate());
        rooms = dbAdapter.getAllRoomsData();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, rooms);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        roomSpinner.setAdapter(spinnerAdapter);
        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
                AppConstant.cRoomId = rooms.get(pos).getRoomId();
//                        Toast.makeText(context, rooms.get(pos).getRoomId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString();
                if (title.length() > 0){
                    dialog.dismiss();
                    if (dbAdapter.checkIfExist(DatabaseOpenHelper.TABLE_device, AppConstant.deviceid)){
                        if (dbAdapter.updateDevice(AppConstant.deviceid, title, "hue", "x", AppConstant.cRoomId) > 0){
                            Toast.makeText(context, "Device Updated Successfully", Toast.LENGTH_SHORT).show();
                            actionBar.setTitle(title);
                            AppConstant.device = title;
                        } else {
                            Toast.makeText(context, "opps! Error in Update", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (dbAdapter.addDevice(AppConstant.deviceid, title, "hue", "x", AppConstant.cRoomId) > 0){
                            Toast.makeText(context, "Device Added Successfully", Toast.LENGTH_SHORT).show();
                            actionBar.setTitle(title);
                            AppConstant.device = title;
                        } else {
                            Toast.makeText(context, "opps! Error in Add", Toast.LENGTH_SHORT).show();
                        }
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
//                int p = 0;
//                for (int i = 0; i < folders.size(); i++) {
//                    if (folders.get(i).getId().equalsIgnoreCase(cPhoto.getId())) {
//                        p = i;
//                        break;
//                    }
//                }
//                folderSpinner.setSelection(p);
        // Add action buttons
//                            builder.setPositiveButton("Done",
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.dismiss();
//                                        }
//                                    }).setNegativeButton("Cancel",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.dismiss();
//                                        }
//                                    });
        dialog.show();
    }
}
