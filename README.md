# Android Compass
A compass implementation for Android, providing azimuth to the magnetic North, pitch and roll of the device. It shows the azimuth in a simple compass view and the pitch and roll in text views.  
Uses the magnetic and accelerometer device sensors.

## Classes:

### Compass.java:
Use the static method **Compass.getInstance(Context context, CompassListener compassListener)** to get a new Compass instance. It will return **null** if your device does not have the required sensors.  
The listener must implement the **CompassListener** interface and override the callback method **void onOrientationChanged(float azimuth, float pitch, float roll)** to be notified of the compass azimuth, pitch and roll values changes.  

### CompassView.java:
A view drawing a simple compass: a circle and a line pointing to the azimuth.  
Use the method **updateAzimuth(float azimuthDegrees)** to update the azimuth shown by the compass view.  
