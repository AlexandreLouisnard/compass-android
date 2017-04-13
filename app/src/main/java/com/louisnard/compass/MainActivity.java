package com.louisnard.compass;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Main activity showing {@link Compass} data in a {@link CompassView}.
 *
 * @author Alexandre Louisnard
 */

public class MainActivity extends AppCompatActivity implements Compass.CompassListener {

    // Tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Views
    private CompassView mCompassView;

    // Compass
    private Compass mCompass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mCompassView = (CompassView) findViewById(R.id.compass_view);

        // Compass
        mCompass = Compass.newInstance(this, this);
        if (mCompass == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "The device does not have the required sensors to use a compass.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCompass != null) mCompass.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCompass != null) mCompass.stop();
    }

    @Override
    public void onAzimuthChanged(float azimuth) {
        mCompassView.updateAzimuth(azimuth);
    }
}
