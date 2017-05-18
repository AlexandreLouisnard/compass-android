package com.louisnard.compass;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Main activity showing device orientation data with a {@link CompassView} for the azimuth and text views for the vertical inclination, the horizontal inclination and the device screen orientation.<br>
 *
 * @author Alexandre Louisnard
 */

public class MainActivity extends AppCompatActivity implements Compass.CompassListener {

    // Tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Constants
    // The minimum difference in degrees with the last orientation value for the CompassListener to be notified
    private static final float MIN_AZIMUTH_DIFFERENCE_BETWEEN_COMPASS_UPDATES = 1;
    private static final float MIN_VERTICAL_INCLINATION_DIFFERENCE_BETWEEN_COMPASS_UPDATES = 1;
    private static final float MIN_HORIZONTAL_INCLINATION_DIFFERENCE_BETWEEN_COMPASS_UPDATES = 1;

    // Compass
    private Compass mCompass;

    // Views
    private CompassView mCompassView;
    private TextView mVerticalInclinationTextView;
    private TextView mHorizontalInclinationTextView;
    private TextView mScreenRotationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mCompassView = (CompassView) findViewById(R.id.compass_view);
        mVerticalInclinationTextView = (TextView) findViewById(R.id.horizontal_inclination_text_view);
        mHorizontalInclinationTextView = (TextView) findViewById(R.id.vertical_inclination_text_view);
        mScreenRotationTextView = (TextView) findViewById(R.id.screen_rotation_text_view);

        // Compass
        mCompass = Compass.newInstance(this, this);
        if (mCompass == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "The device does not have the required sensors to use a compass.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCompass != null) mCompass.start(MIN_AZIMUTH_DIFFERENCE_BETWEEN_COMPASS_UPDATES, MIN_VERTICAL_INCLINATION_DIFFERENCE_BETWEEN_COMPASS_UPDATES, MIN_HORIZONTAL_INCLINATION_DIFFERENCE_BETWEEN_COMPASS_UPDATES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCompass != null) mCompass.stop();
    }

    @Override
    public void onOrientationChanged(float azimuth, float verticalInclination, float horizontalInclination) {
        mCompassView.updateAzimuth(azimuth);
        mVerticalInclinationTextView.setText(String.format(getString(R.string.vertical_inclination), verticalInclination));
        mHorizontalInclinationTextView.setText(String.format(getString(R.string.horizontal_inclination), horizontalInclination));

        final int screenRotation = (((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getRotation();
        if (screenRotation == Surface.ROTATION_90) {
            mScreenRotationTextView.setText(String.format(getString(R.string.screen_rotation_value), 90));
        } else if (screenRotation == Surface.ROTATION_180) {
            mScreenRotationTextView.setText(String.format(getString(R.string.screen_rotation_value), 180));
        } else if (screenRotation == Surface.ROTATION_270) {
            mScreenRotationTextView.setText(String.format(getString(R.string.screen_rotation_value), 270));
        } else {
            mScreenRotationTextView.setText(String.format(getString(R.string.screen_rotation_value), 0));
        }
    }
}
