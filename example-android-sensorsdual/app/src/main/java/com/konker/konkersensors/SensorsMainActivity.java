package com.konker.konkersensors;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.konker.konkersensors.jsondata.QrcodeItems;
import com.konker.konkersensors.jsondata.qrcodeObject;

public class SensorsMainActivity extends Activity  {
    EditText nameEditText;
    EditText passwordEditText;
    Switch backgroundSwitch;
    EditText URLeditText;
    Button loginButton;
    Boolean nameEdited=false;
    Boolean passwordEdited=false;
    String userName;
    String password;
    String url;
    boolean dataBackground;
    Switch lightSwitch;
    Switch accelerationSwitch;
    Switch rotationSwitch;
    Switch proximitySwitch;
    Switch gyroscopeSwitch;
    Switch gpsSwitch;
    RadioButton radioRest;
    RadioButton radioMQTT;
    String radioMethod;
    Button buttonGetQR;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    /* create a full screen window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_sensorsmain);


        //get APP version
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView versionTextView  = (TextView) findViewById(R.id.versionTextView);
            versionTextView.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



        nameEditText  = (EditText) findViewById(R.id.nameEditText);
        passwordEditText  = (EditText) findViewById(R.id.passwordEditText);
        backgroundSwitch= (Switch) findViewById(R.id.switch1);
        URLeditText = (EditText) findViewById(R.id.URLeditText);

        lightSwitch=(Switch) findViewById(R.id.lightSwitch);
        accelerationSwitch=(Switch) findViewById(R.id.accelerationSwitch);
        rotationSwitch=(Switch) findViewById(R.id.rotationSwitch);
        proximitySwitch =(Switch) findViewById(R.id.proximitySwitch);
        gyroscopeSwitch=(Switch) findViewById(R.id.gyroscopeSwitch);
        gpsSwitch=(Switch) findViewById(R.id.gpsSwitch);
        radioRest=(RadioButton) findViewById(R.id.radioRest);
        radioMQTT=(RadioButton) findViewById(R.id.radioMQTT);



        Bundle b = getIntent().getExtras();
        if (b!=null ){
            String qrcode=b.getString("qrcode");
            getPreferencesValues(qrcode);
        }else {
            getPreferencesValues(null);
        }

        if(radioMethod.equals("rest")) {
            radioRest.setChecked(true);
            radioMQTT.setChecked(false);
        }else{
            radioRest.setChecked(false);
            radioMQTT.setChecked(true);
        }

        nameEditText.setText(userName);
        URLeditText.setText(url);

        backgroundSwitch.setChecked(dataBackground);

        nameEditText.setOnClickListener(nameClickListener);
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(url.indexOf(nameEditText.getText().toString())==-1){
                        if(radioMethod.equals("rest")) {
                            URLeditText.setText(getResources().getString(R.string.app_urlPubHTTP) + nameEditText.getText().toString() + "/");
                        }
                    }
                }
            }
        });




        passwordEditText.setOnClickListener(passwordClickListener);
        passwordEditText.setText(password);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(loginButtonClickListener);

        buttonGetQR = (Button) findViewById(R.id.buttonGetQR);
        buttonGetQR.setOnClickListener(buttonGetQRClickListener);

        radioRest.setOnClickListener(radioRestClickListener);
        radioMQTT.setOnClickListener(radioMQTTClickListener);







      /* adapt the image to the size of the display */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(),R.drawable.fundologin),668,667,true);

    /* fill the background ImageView with the resized image */
        ImageView iv_background = (ImageView) findViewById(R.id.fundo);
        iv_background.setImageBitmap(bmp);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions( this, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    0);
        }



    }


    private View.OnClickListener nameClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            if(nameEdited==false) {
                nameEdited = true;
                nameEditText.setText("");
            }
        }
    };


    //
    private View.OnClickListener passwordClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            if(passwordEdited==false) {
                passwordEdited = true;
                passwordEditText.setText("");
            }
        }
    };











    private View.OnClickListener radioRestClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            radioMQTT.setChecked(false);
            radioMethod="rest";

            SharedPreferences mPrefs = getSharedPreferences("label", 0);
            if(!mPrefs.getString("method", "rest").equals("rest")){
                url = qrcodeObject.urlPubHTTP ;
            }


            URLeditText.setText(url);
            savePreferences();
            //getPreferencesValues(null);


        }
    };



    private View.OnClickListener radioMQTTClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            radioRest.setChecked(false);
            radioMethod="mqtt";

            SharedPreferences mPrefs = getSharedPreferences("label", 0);
            if(mPrefs.getString("method", "rest").equals("rest")){
                url = qrcodeObject.urlPubMQTT ;
            }


            URLeditText.setText(url);
            savePreferences();
        }
    };



















    private View.OnClickListener loginButtonClickListener= new View.OnClickListener() {
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(),SensorsActivity.class);
            Bundle b = new Bundle();
            b.putString("username", nameEditText.getText().toString());
            b.putString("password", passwordEditText.getText().toString());
            b.putString("puburl", URLeditText.getText().toString());
            b.putBoolean("dataBackground",backgroundSwitch.isChecked());
            b.putBoolean("lightsensor",lightSwitch.isChecked());
            b.putBoolean("accelerationsensor",accelerationSwitch.isChecked());
            b.putBoolean("rotationsensor",rotationSwitch.isChecked());
            b.putBoolean("proximitysensor", proximitySwitch.isChecked());
            b.putBoolean("gyroscopesensor",gyroscopeSwitch.isChecked());
            b.putBoolean("gpssensor",gpsSwitch.isChecked());
            b.putString("method",radioMethod);
            i.putExtras(b); //Put your id to your next Intent

            savePreferences();


            startActivity(i);


        }
    };


    private View.OnClickListener buttonGetQRClickListener= new View.OnClickListener() {
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(),MainBarActivity.class);
            Bundle b = new Bundle();
            b.putString("returnactivity", "SensorsMain");
            i.putExtras(b); //Put your id to your next Intent


            startActivity(i);


        }
    };


    private void savePreferences(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();

        mEditor.putString("username",  nameEditText.getText().toString()).commit();
        qrcodeObject.updateJSONData(QrcodeItems.user, nameEditText.getText().toString());

        mEditor.putString("password", passwordEditText.getText().toString()).commit();
        qrcodeObject.updateJSONData(QrcodeItems.pass, passwordEditText.getText().toString());

        mEditor.putString("puburl", URLeditText.getText().toString()).commit();
        qrcodeObject.userUrl= URLeditText.getText().toString();


        mEditor.putString("endpointsJSON",qrcodeObject.jsonString).commit();


        mEditor.putBoolean("lightsensor",lightSwitch.isChecked()).commit();
        mEditor.putBoolean("accelerationsensor", accelerationSwitch.isChecked()).commit();
        mEditor.putBoolean("rotationsensor", rotationSwitch.isChecked()).commit();
        mEditor.putBoolean("proximitysensor", proximitySwitch.isChecked()).commit();
        mEditor.putBoolean("gyroscopesensor",gyroscopeSwitch.isChecked()).commit();
        mEditor.putBoolean("gpssensor",gpsSwitch.isChecked()).commit();

        mEditor.putString("method", radioMethod).commit();

    }


    private void getPreferencesValues(String qrcode){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        if(qrcode==null){
            qrcode=mPrefs.getString("endpointsJSON", null);
            qrcodeObject.userUrl=mPrefs.getString("puburl", null);
        }else{
            qrcodeObject.userUrl=null;
        }

        if (qrcode!=null ){
            qrcodeObject.getJSONData(qrcode);
            userName =qrcodeObject.username;
            password =qrcodeObject.password;
            radioMethod=mPrefs.getString("method", null);
            radioMethod= radioMethod==null?getResources().getString(R.string.app_method):radioMethod;


            if (radioMethod.equals("rest")) {
                url = qrcodeObject.userUrl==null ? qrcodeObject.urlPubHTTP : qrcodeObject.userUrl;
            } else {
                url = qrcodeObject.userUrl==null ? qrcodeObject.urlPubMQTT : qrcodeObject.userUrl;
            }

        }else {
            setDefaults();
        }



        lightSwitch.setChecked(mPrefs.getBoolean("lightsensor", true));
        accelerationSwitch.setChecked(mPrefs.getBoolean("accelerationsensor", false));
        rotationSwitch.setChecked(mPrefs.getBoolean("rotationsensor", false));
        proximitySwitch.setChecked(mPrefs.getBoolean("proximitysensor", false));
        gyroscopeSwitch.setChecked(mPrefs.getBoolean("gyroscopesensor", false));
        gpsSwitch.setChecked(mPrefs.getBoolean("gpssensor", false));

    }





    public void setDefaults(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        userName = getResources().getString(R.string.app_username);
        password = getResources().getString(R.string.app_password);
        radioMethod= radioMethod==null?getResources().getString(R.string.app_method):radioMethod;

        if (radioMethod.equals("rest")) {
            url =mPrefs.getString("puburl", null)==null ? getResources().getString(R.string.app_urlPubHTTP) : mPrefs.getString("puburl", null);
        } else {
            url = mPrefs.getString("puburl", null)==null ? getResources().getString(R.string.app_urlPubMQTT):mPrefs.getString("puburl", null);
        }
    }


}
