package com.ngm.smartot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.VideoView;

public class SplashActivity extends Activity {

	private final Handler mHandler = new Handler();
	ImageView imageView;
	Context con;
    private MediaPlayer splashSound;
    private boolean timing;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_splash);
		con = this;

        try {
            splashPlayer();
        } catch (Exception ex) {
            jumpMain();
        }
	}

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    public void splashPlayer() {
        getWindow().setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        VideoView videoHolder = new VideoView(this);
        setContentView(videoHolder);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_mp4);
        videoHolder.setVideoURI(video);
        videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                jumpMain();
            }

        });
        videoHolder.start();
        videoHolder.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((VideoView) v).stopPlayback();
                jumpMain();
                return true;
            }
        });
    }

	/*
	 *
	 * runnable to handle the splash screen finish
	 */
//	private final Runnable mPendingLauncherRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//            final Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
//            startActivity(myIntent);
//            SplashActivity.this.finish();
//		}
//	};

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private synchronized void jumpMain() {
        Log.i("JUMP MAIN","CALLED");
        final Intent myIntent = new Intent(SplashActivity.this, MainActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(myIntent);
        finish();
    }

//	private boolean isMyServiceRunning(Class<?> serviceClass) {
//	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//	        if (serviceClass.getName().equals(service.service.getClassName())) {
//	            return true;
//	        }
//	    }
//	    return false;
//	}
}
