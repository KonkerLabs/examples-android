package com.konker.konkersensors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konker.konkersensors.ActuatorsObjects.ActuatorsObjects;
import com.konker.konkersensors.conn.ConnectActuators;


public class ActuatorsActivity extends Activity  {
    private long lastUpdate;
    WebView usermessageView;
    Button backButton;


    private String username="";
    private String password="";
    private String url ="";
    public Exception activityException;
    String extraUsername;
    String extraPassword;
    String extraURL;
    String channel;
    Boolean dataBackground=false;
    String method;
    ConnectActuators postSender;
    TextView timerTextView;
    TextView debugTextView;
    Boolean vibrate;
    Boolean alert;
    Boolean ring;
    Boolean photo;
    LinearLayout alertLayout;
    String frequency;




    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actuators);


        lastUpdate = System.currentTimeMillis();



        backButton=(Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(backButtonClickListener);

        alertLayout=(LinearLayout) findViewById(R.id.alertLayout);


        String summary = "<html><body>Connecting to Konker Pataform...</body></html>";
        usermessageView=(WebView)findViewById(R.id.usermessageWebview);
        usermessageView.loadData(summary, "text/html; charset=utf-8", "utf-8");
        usermessageView.setVisibility(View.INVISIBLE);
        backButton.setVisibility(View.INVISIBLE);
        alertLayout.setVisibility(View.INVISIBLE);
        activityException=null;

        Bundle b = getIntent().getExtras();
        extraUsername=b.getString("username");
        extraPassword=b.getString("password");
        extraURL =b.getString("suburl");
        channel =b.getString("channel");
        dataBackground=b.getBoolean("dataBackground");
        vibrate=b.getBoolean("vibrate");
        alert=b.getBoolean("alert");
        ring=b.getBoolean("ring");
        photo=b.getBoolean("photo");
        method=b.getString("method");
        frequency=b.getString("frequency");



        setConnParameters(extraUsername, extraPassword, extraURL);

        SetViews();

        ActuatorsObjects actuatorObj= new ActuatorsObjects();
        actuatorObj.connection.username=this.username;
        actuatorObj.connection.password=this.password;
        actuatorObj.connection.url =this.url;
        actuatorObj.connection.channel =this.channel;
        actuatorObj.connection.transmitMethod=this.method;

        actuatorObj.screenObjects.backButton=backButton;
        actuatorObj.screenObjects.usermessageView=usermessageView;
        actuatorObj.screenObjects.timerTextView=timerTextView;
        actuatorObj.screenObjects.debugTextView=debugTextView;
        actuatorObj.screenObjects.alertLayout=alertLayout;

        actuatorObj.vibrate=vibrate;
        actuatorObj.alert=alert;
        actuatorObj.ring=ring;
        actuatorObj.photo=photo;
        actuatorObj.frequency=frequency;

        actuatorObj.startTime=System.currentTimeMillis();
        actuatorObj.context=this;

        postSender = new ConnectActuators(actuatorObj);


    }

    public void SetViews(){
        timerTextView=(TextView)findViewById(R.id.DebugText20);
        debugTextView= (TextView)findViewById(R.id.DebugText1);

    }



    //update server parameters
    public void setConnParameters(String username, String password, String url){
        this.username=username;
        this.password=password;
        this.url =url;
    }




    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        if (dataBackground==false) {

            try {
                postSender.stopAll();
            }catch (Exception e){
                System.out.println("Cant do postListener.stopListeningData. postListener was starded?");
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
            startActivity(i);
        }
    };


}

