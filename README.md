# Android Compass

**Author: Alexandre Louisnard alouisnard@gmail.com**  
**Android Java Application**  
**2017** 

## DESCRIPTION
Compass implementation providing azimuth (East of the magnetic North, counterclockwise), pitch (vertical inclination) and roll (horizontal inclination) of the device, in degrees.  

Provides:  
* **Azimuth**: angle of rotation about the -z axis. This value represents the angle between the device's y axis and the magnetic north pole. Counterclockwise, from 0° to 360°.
* **Pitch** (vertical inclination): angle of rotation about the x axis. This value represents the angle between a plane parallel to the device's screen and a plane parallel to the ground. From -180° to 180°.
* **Roll** (horizontal inclination): angle of rotation about the y axis. This value represents the angle between a plane perpendicular to the device's screen and a plane perpendicular to the ground. From -90° to 90°.

This implementation takes into account the orientation (portrait / landscape) of the device and corrects the values accordingly.  

Uses magnetic and accelerometer device sensors.  

## USAGE

### CompassActivity.java
Activity showing device orientation data in a CompassView for the azimuth and in TextView's for the pitch, roll and screen orientation.<br>

### Compass.java:
* **static Compass newInstance(Context context, CompassListener compassListener)** : get a new Compass instance. It will return **null** if your device does not have the required sensors.   
* **interface CompassListener** : the listener must implement it and override the callback method **void onOrientationChanged(float azimuth, float pitch, float roll)** to be notified of the compass azimuth, pitch and roll values changes.  

### CompassView.java:
A view drawing a simple compass: a circle and a line pointing to the azimuth.  
* **void updateAzimuth(float azimuthDegrees)** : update the azimuth displayed in the compass view.  

## CHANGELOG

## BACKLOG
