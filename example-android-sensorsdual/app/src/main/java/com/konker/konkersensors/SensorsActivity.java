package com.konker.konkersensors;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.konker.konkersensors.Location.AndroidLocation;
import com.konker.konkersensors.SensorsObjects.SensorsObjects;
import com.konker.konkersensors.conn.ConnectSensors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SensorsActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private View view;
    private long lastUpdate;
    WebView usermessageView;
    Button backButton;

    private int DebugSensor=Sensor.TYPE_PROXIMITY;
    public Map<String, float[]> sensorValues = Collections.synchronizedMap(new HashMap<String, float[]>());
    int MaxSensors=18;
    private String username="";
    private String password="";
    private String url ="";
    public Exception activityException;
    public List<Sensor> sensorList = new ArrayList<Sensor>();
    String extraUsername;
    String extraPassword;
    String extraURLPublish;
    Boolean dataBackground=false;
    String method;

    ConnectSensors postSender;
    TextView timerTextView;
    TextView debugViews[];
    AndroidLocation androidLocation;
    private FirebaseAnalytics mFirebaseAnalytics;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "SensorsActivity");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lastUpdate = System.currentTimeMillis();


        backButton=(Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(backButtonClickListener);
        String summary = "<html><body>Connecting to Konker Pataform...</body></html>";
        usermessageView=(WebView)findViewById(R.id.usermessageWebview);
        usermessageView.loadData(summary, "text/html; charset=utf-8", "utf-8");
        usermessageView.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
        activityException=null;

        Bundle b = getIntent().getExtras();
        extraUsername=b.getString("username");
        extraPassword=b.getString("password");
        extraURLPublish=b.getString("puburl");
        dataBackground=b.getBoolean("dataBackground");
        method=b.getString("method");



        setConnParameters(extraUsername, extraPassword, extraURLPublish);

        if(b.getBoolean("lightsensor")){
            try {
                sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(b.getBoolean("accelerationsensor")){
            try{
                sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(b.getBoolean("rotationsensor")){
           try{
                sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(b.getBoolean("proximitysensor")){
            try{
                sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(b.getBoolean("gyroscopesensor")){
            try{
                sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(b.getBoolean("gpssensor")) {
            try {
                androidLocation = new AndroidLocation();
                androidLocation.Initialize(this, 1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


       /// }else{
           // MaxSensors=sensorList.size();
        //}





        SetViews();


        SensorsObjects sensorObj= new SensorsObjects();
        sensorObj.connection.username=this.username;
        sensorObj.connection.password=this.password;
        sensorObj.connection.url =this.url;
        sensorObj.connection.transmitMethod=this.method;

        sensorObj.screenObjects.backButton=backButton;
        sensorObj.screenObjects.usermessageView=usermessageView;
        sensorObj.screenObjects.debugViews=debugViews;
        sensorObj.screenObjects.timerTextView=timerTextView;

        sensorObj.sensorValues=sensorValues;
        sensorObj.sensorList=sensorList;
        sensorObj.startTime=System.currentTimeMillis();
        sensorObj.androidLocation=androidLocation;


        postSender = new ConnectSensors(sensorObj);


    }


public void SetViews(){
    timerTextView=(TextView)findViewById(R.id.DebugText20);
    debugViews= new TextView[]{
            (TextView)findViewById(R.id.DebugText1),
            (TextView)findViewById(R.id.DebugText2),
            (TextView)findViewById(R.id.DebugText3),
            (TextView)findViewById(R.id.DebugText4),
            (TextView)findViewById(R.id.DebugText5),
            (TextView)findViewById(R.id.DebugText6),
            (TextView)findViewById(R.id.DebugText7),
            (TextView)findViewById(R.id.DebugText8),
            (TextView)findViewById(R.id.DebugText9),
            (TextView)findViewById(R.id.DebugText10),
            (TextView)findViewById(R.id.DebugText11),
            (TextView)findViewById(R.id.DebugText12),
            (TextView)findViewById(R.id.DebugText13),
            (TextView)findViewById(R.id.DebugText14),
            (TextView)findViewById(R.id.DebugText15),
            (TextView)findViewById(R.id.DebugText16),
            (TextView)findViewById(R.id.DebugText17),
            (TextView)findViewById(R.id.DebugText18),
            (TextView)findViewById(R.id.DebugText19)};

    }

    @Override
    //any sensor event will trigger this method
    public void onSensorChanged(SensorEvent event) {
        String sensorname = event.sensor.getName().replaceAll("[^0-9A-Za-z]","");
        float values[]=event.values;
        sensorValues.put(sensorname,values);
    }






    //update server parameters
    public void setConnParameters(String username, String password, String url){
        this.username=username;
        this.password=password;
        this.url =url;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    protected void onResume() {
        super.onResume();

        for ( Sensor val : sensorList) {
            try {
                sensorManager.registerListener(this,
                        sensorManager.getDefaultSensor(val.getType()),
                        SensorManager.SENSOR_DELAY_NORMAL);
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }



    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        if (dataBackground==false) {
            sensorManager.unregisterListener(this);
            if(androidLocation!=null){
                androidLocation.stopFusedLocation();
            }

            try {
                postSender.stopAll();
            }catch (Exception e){
                System.out.println("Cant do postSender.stopSendingData. postSender was starded?");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        postSender.stopAll();
    }


    @Override
    public void onBackPressed()
    {

        postSender.stopAll();

        finish();
    }







    private View.OnClickListener backButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(),SensorsMainActivity.class);

            usermessageView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            String summary = "<html><body>1 - Connecting to Konker Pataform...</body></html>";
            usermessageView.loadData(summary, "text/html; charset=utf-8", "utf-8");

            finish();
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    };


}

