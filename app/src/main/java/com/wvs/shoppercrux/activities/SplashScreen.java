package com.wvs.shoppercrux.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wvs.shoppercrux.R;

public class SplashScreen extends Activity {

    ImageView splash;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.w("Shoppercrux", "onCreate() method excecuted");

        layout = (RelativeLayout) findViewById(R.id.layout);
        splash = (ImageView) findViewById(R.id.splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(SplashScreen.this, LoginActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, 5000);

//        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash);
//        splash.setAnimation(anim);
//
//        anim.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                finish();
//                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

//        Timer timer = new Timer();

        //Create a task which the timer will execute.  This should be an implementation of the TimerTask interface.
        //I have created an inner class below which fits the bill.
//        MyTimer mt = new MyTimer();

        //We schedule the timer task to run after 1000 ms and continue to run every 1000 ms.
//        timer.schedule(mt, 1000, 1000);
    }


    //An inner class which is an implementation of the TImerTask interface to be used by the Timer.
//    class MyTimer extends TimerTask {
//
//        public void run() {
//
//            //This runs in a background thread.
//            //We cannot call the UI from this thread, so we must call the main UI thread and pass a runnable
//            runOnUiThread(new Runnable() {
//
//                public void run() {
//                    Random rand = new Random();
//                    //The random generator creates values between [0,256) for use as RGB values used below to create a random color
//                    //We call the RelativeLayout object and we change the color.  The first parameter in argb() is the alpha.
//                    layout.setBackgroundColor(Color.argb(255, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
//                }
//            });
//        }
//    }


}

