package com.konker.konkersensors.ActuatorsObjects;

import android.content.Context;

import com.konker.konkersensors.conn.ConnPackageObj;

/**
 * Created by erico on 17/10/2016.
 */

public class ActuatorsObjects {
    public ConnPackageObj connection = new ConnPackageObj();
    public ActuatorsScreenObjects screenObjects = new ActuatorsScreenObjects();
    public Boolean vibrate=false;
    public Boolean alert=false;
    public Boolean ring=false;
    public Boolean photo=false;
    public Context context;
    public String frequency;
    public long startTime;
}
