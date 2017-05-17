package com.louisnard.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import static android.content.Context.SENSOR_SERVICE;


/**
 * Compass implementation for Android providing azimuth, pitch and roll device orientation data, in degrees.
 *
 * Azimuth: angle of rotation about the -z axis. This value represents the angle between the device's y axis and the magnetic north pole.
 * Pitch: angle of rotation about the x axis. This value represents the angle between a plane parallel to the device's screen and a plane parallel to the ground.
 * Roll: angle of rotation about the y axis. This value represents the angle between a plane perpendicular to the device's screen and a plane perpendicular to the ground.
 *
 * Uses magnetic and accelerometer device sensors.
 *
 * @author Alexandre Louisnard
 */

public class Compass implements SensorEventListener {

    // Tag
    private static final String TAG = Compass.class.getSimpleName();

    // Constants
    private static final float GEOMAGNETIC_SMOOTHING_FACTOR = 0.4f;
    private static final float GRAVITY_SMOOTHING_FACTOR = 0.1f;

    // Context
    private Context mContext;

    // Sensors
    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private Sensor mAccelerometer;

    // Orientation
    private float mAzimuthDegrees;
    private float mPitchDegrees;
    private float mRollDegrees;
    private float[] mGeomagnetic = new float[3];
    private float[] mGravity = new float[3];

    // Listener
    private CompassListener mCompassListener;
    // The minimum difference in degrees with the last orientation value for the CompassListener to be notified
    private float mAzimuthSensibility;
    private float mPitchSensibility;
    private float mRollSensibility;
    // The last orientation value sent to the CompassListener
    private float mLastAzimuthDegrees;
    private float mLastPitchDegrees;
    private float mLastRollDegrees;

    /**
     * Interface definition for {@link Compass} callbacks.
     */
    public interface CompassListener {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }

    // Private constructor
    private Compass(Context context, CompassListener compassListener) {
        mContext = context;
        // Sensors
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Listener
        mCompassListener = compassListener;
    }

    /**
     * Returns a new {@link Compass} instance.
     * @param context the current {@link Context}.
     * @param compassListener the listener for this {@link Compass} events.
     * @return a new {@link Compass} instance or <b>null</b> if the device does not have the required sensors.
     */
    @Nullable
    public static Compass newInstance(Context context, CompassListener compassListener) {
        Compass compass = new Compass(context, compassListener);
        if (compass.hasRequiredSensors()) {
            return compass;
        } else {
            if (BuildConfig.DEBUG) Log.d(TAG, "The device does not have the required sensors for Compass.");
            return null;
        }
    }

    // Check that the device has the required sensors
    private boolean hasRequiredSensors() {
        if (mMagnetometer == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "No Magnetic sensor.");
            return false;
        } else if (mAccelerometer == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "No Accelerometer sensor.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Starts the {@link Compass}.
     * Must be called in {@link Activity#onResume()}.
     * @param azimuthSensibility the minimum difference in degrees with the last azimuth measure for the {@link CompassListener} to be notified. Set to 0 (default value) to be notified of the slightest change, set to 360 to never be notified.
     * @param pitchSensibility the minimum difference in degrees with the last pitch measure for the {@link CompassListener} to be notified. Set to 0 (default value) to be notified of the slightest change, set to 360 to never be notified.
     * @param rollSensibility the minimum difference in degrees with the last roll measure for the {@link CompassListener} to be notified. Set to 0 (default value) to be notified of the slightest change, set to 360 to never be notified.
     */
    public void start(float azimuthSensibility, float pitchSensibility, float rollSensibility) {
        mAzimuthSensibility = azimuthSensibility;
        mPitchSensibility = pitchSensibility;
        mRollSensibility = rollSensibility;
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Starts the {@link Compass}.
     * Must be called in {@link Activity#onResume()}.
     */
    public void start() {
        start(0, 0, 0);
    }

    /**
     * Stops the {@link Compass}.
     * Must be called in {@link Activity#onPause()}.
     */
    public void stop() {
        mAzimuthSensibility = 0;
        mPitchSensibility = 0;
        mRollSensibility = 0;
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic = exponentialSmoothing(event.values, mGeomagnetic, GEOMAGNETIC_SMOOTHING_FACTOR);
            }
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                mGravity = exponentialSmoothing(event.values, mGravity, GRAVITY_SMOOTHING_FACTOR);
            }
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                mAzimuthDegrees = (float) Math.toDegrees(orientation[0]);
                mPitchDegrees = (float) Math.toDegrees(orientation[1]);
                mRollDegrees = (float) Math.toDegrees(orientation[2]);
                // Correct azimuth value depending on screen orientation
                final int screenRotation = (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getRotation();
                if (screenRotation == Surface.ROTATION_90) {
                    mAzimuthDegrees += 90f;
                } else if (screenRotation == Surface.ROTATION_180) {
                    mAzimuthDegrees += 180f;
                } else if (screenRotation == Surface.ROTATION_270) {
                    mAzimuthDegrees += 270f;
                }
                // Force azimuth value between 0° and 360°.
                mAzimuthDegrees = (mAzimuthDegrees + 360) % 360;
                // Notify the compass listener
                if (Math.abs(mAzimuthDegrees - mLastAzimuthDegrees) >= mAzimuthSensibility
                        || Math.abs(mPitchDegrees - mLastPitchDegrees) >= mPitchSensibility
                        || Math.abs(mRollDegrees - mLastRollDegrees) >= mRollSensibility
                        || mLastAzimuthDegrees == 0) {
                    mLastAzimuthDegrees = mAzimuthDegrees;
                    mLastPitchDegrees = mPitchDegrees;
                    mLastRollDegrees = mRollDegrees;
                    mCompassListener.onOrientationChanged(mAzimuthDegrees, mPitchDegrees, mRollDegrees);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing to do
    }

    /**
     * Exponential smoothing of data series, acting as a low-pass filter in order to remove high-frequency noise.
     * @param newValue the new data entry.
     * @param lastValue the last data entry.
     * @param alpha the smoothing factor. 0 < alpha < 1. If alpha = 0, the data will never change (lastValue = newValue). If alpha = 1, no smoothing at all will be applied (lastValue = newValue).
     * @return output the new data entry, smoothened.
     */
    private float[] exponentialSmoothing(float[] newValue, float[] lastValue, float alpha) {
        float[] output = new float[newValue.length];
        if (lastValue == null) {
            return newValue;
        }
        for (int i=0; i<newValue.length; i++) {
            output[i] = lastValue[i] + alpha * (newValue[i] - lastValue[i]);
        }
        return output;
    }
}
