package com.qb.toolbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.qb.toolbox.R;

public class SplashscreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashscreenActivity";
    /** Splash screen duration time in milliseconds */
    private static final int DELAY = 500;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // Jump to SensorsActivity after DELAY milliseconds
        //activity SplashscreenActivity---->FeaturesActivity
//        new Handler().postDelayed(() -> {
//            final Intent newIntent = new Intent(SplashscreenActivity.this, FeaturesActivity.class);
//            newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//            startActivity(newIntent);
//            finish();
//        }, DELAY);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent newIntent = new Intent(SplashscreenActivity.this, FeaturesActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(newIntent);
                finish();
            }
        }, DELAY);
    }

    @Override
    public void onBackPressed() {
        // do nothing. Protect from exiting the application when splash screen is shown
        Log.i(TAG, "onBackPressed!");
    }
}
