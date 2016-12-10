package com.ngm.smartot;/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class BackgroundService extends Service {
    private static final String TAG = "BackgroundService";
    public static final String SH_MESSAGE = "com.smarthome.BackgroundService.MESSAGE";
    public static final String SH_RESULT = "com.smarthome.BackgroundService.REQUEST_PROCESSED";

    private Toast mToast;
    private boolean fist = false, spread;
    boolean firstRoll = false, increasing = false;
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
            showToast(getString(R.string.connected));
            sendResult("onConnect", "connected");
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            showToast(getString(R.string.disconnected));
            sendResult("onDisconnect", "disconnected");
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
            AppConstant.myosynced = true;
            sendResult("onArmSync", (myo.getArm() == Arm.LEFT ? R.string.arm_left : R.string.arm_right)+"");
        }
        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
            AppConstant.myosynced = false;
            sendResult("onArmUnsync", "ArmUnsync");
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
            sendResult("onUnlock", "Unlock");
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
            sendResult("onLock", "Lock");
        }

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            if (fist){
//                Log.i("ROLL", roll+"");
                if (!firstRoll){
                    AppConstant.roll =  (int) roll;
                    firstRoll=true;
                }
                increasing = false;
                if (firstRoll){
                    AppConstant.roll2 = (int) roll;
                    Log.i("ROLL -- ROLL2", AppConstant.roll+"<--->"+AppConstant.roll2);
                    if ((AppConstant.roll + 5) < AppConstant.roll2){
                        Log.i("ROLL", "INCREASING ---> "+roll+"");
                        AppConstant.roll = (int) roll;
                        increasing = true;
//                        AppConstant.powerProgress+=5;
//                        seekBar.setProgress(AppConstant.powerProgress);
                        sendResult("onOrientationData", "INCREASING");
                    }
                    if (!increasing){
                        if ((AppConstant.roll - 5) > AppConstant.roll2) {
                            Log.i("ROLL", "DECREASING ---> "+roll+"");
                            AppConstant.roll = (int) roll;
//                            AppConstant.powerProgress-=5;
//                            seekBar.setProgress(AppConstant.powerProgress);
                            sendResult("onOrientationData", "DECREASING");
                        }
                    }

                }
            }
//            Log.i("YAW", yaw+"");
//            Log.i("PITCH", pitch+"");
            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
            sendResult("setRollData", roll+"");
            sendResult("setPitchData", pitch+"");
            sendResult("setYawData", yaw+"");
//            mTextView.setRotation(roll);
//            mTextView.setRotationX(pitch);
//            mTextView.setRotationY(yaw);
        }

        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
//            showToast(getString(R.string.pose, pose.toString()));
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
            fist = false;
            switch (pose) {
                case UNKNOWN:
                    sendResult("poseUNKNOWN", "poseUNKNOWN");
                    break;
                case REST:
                case DOUBLE_TAP:
                    int restTextId = R.string.app_name;

                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    sendResult("poseDOUBLE_TAP", getString(restTextId) + "");
                    break;
                case FIST:
                    fist = true;
                    sendResult("poseFIST", "");
//                    if( light.getPowerState() == LFXTypes.LFXPowerState.ON)
//                    {
//                        lightSwitch.setChecked(false);
//                    }
                    break;
                case WAVE_IN:
                    sendResult("poseWAVE_IN", "");
                    break;
                case WAVE_OUT:
                    sendResult("poseWAVE_OUT", "");
                    break;
                case FINGERS_SPREAD:
                    sendResult("poseFINGERS_SPREAD", "");
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }
        }
    };
    private LocalBroadcastManager broadcaster;

    public void sendResult(String type, String message) {
        Intent intent = new Intent(SH_RESULT);
        if(message != null)
            intent.putExtra("type", type);
            intent.putExtra("msg", message);
        broadcaster.sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            showToast("Couldn't initialize Hub");
            stopSelf();
            return;
        }

        // Disable standard Myo locking policy. All poses will be delivered.
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        // Finally, scan for Myo devices and connect to the first one found that is very near.
        hub.attachToAdjacentMyo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        Hub.getInstance().shutdown();
    }

    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
