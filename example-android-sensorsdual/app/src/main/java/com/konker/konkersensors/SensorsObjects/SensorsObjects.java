package com.konker.konkersensors.SensorsObjects;

import android.hardware.Sensor;

import com.konker.konkersensors.Location.AndroidLocation;
import com.konker.konkersensors.conn.ConnPackageObj;

import java.util.List;
import java.util.Map;

/**
 * Created by erico on 17/10/2016.
 */

public class SensorsObjects {
    public ConnPackageObj connection=new ConnPackageObj();
    public SensorsScreenObjects screenObjects=new SensorsScreenObjects();
    public List<Sensor> sensorList;
    public Map<String, float[]> sensorValues;
    public long startTime;
    public AndroidLocation androidLocation=new AndroidLocation();


}
