# android-compass
A compass implementation for Android, using the magnetic and accelerometer device sensors to show the magnetic North on a circle.

## Classes:
### Compass.java:
Use the static method **Compass.getInstance(Context context, CompassListener compassListener)** to get a new Compass instance. it will return **null** if your device does not have the required sensors.  
The listener must implement the **CompassListener** interface to be notified of the compass azimuth changes.  

### CompassView.java:
A view drawing a simple compass: a circle and a line pointing to the azimuth.  
Use the method **updateAzimuth(float azimuthDegrees)** to update the azimuth shown by the compass view.  
