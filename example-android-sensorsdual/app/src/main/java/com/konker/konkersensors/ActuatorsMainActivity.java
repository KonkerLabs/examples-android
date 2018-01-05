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
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.konker.konkersensors.jsondata.QrcodeItems;
import com.konker.konkersensors.jsondata.qrcodeObject;


public class ActuatorsMainActivity extends Activity  {
    EditText nameEditText;
    EditText passwordEditText;
    Switch backgroundSwitch;
    EditText URLeditText;
    EditText channelEditText;
    Button startButton;
    Boolean nameEdited=false;
    Boolean passwordEdited=false;
    String userName;
    String password;
    String url;
    boolean dataBackground;
    RadioButton radioRest;
    RadioButton radioMQTT;
    String radioMethod;
    Button buttonGetQR;
    CheckBox checkBoxVibrate;
    CheckBox checkBoxAlert;
    CheckBox checkBoxRing;
    CheckBox checkBoxPhoto;
    Spinner spinnerFrequency;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ActuatorsMainActivity");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);




        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    /* create a full screen window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_actuatorsmain);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerFrequency);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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
        channelEditText = (EditText) findViewById(R.id.channelEditText);
        radioRest=(RadioButton) findViewById(R.id.radioRest);
        radioMQTT=(RadioButton) findViewById(R.id.radioMQTT);

        checkBoxVibrate=(CheckBox)findViewById(R.id.checkBoxVibrate);
        checkBoxAlert=(CheckBox)findViewById(R.id.checkBoxAlert);
        checkBoxRing=(CheckBox)findViewById(R.id.checkBoxRing);
        //checkBoxPhoto=(CheckBox)findViewById(R.id.checkBoxPhoto);
        spinnerFrequency=(Spinner) findViewById(R.id.spinnerFrequency);


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
                            URLeditText.setText(getResources().getString(R.string.app_urlSubHTTP) + nameEditText.getText().toString() + "/");
                        }
                    }
                }
            }
        });



        passwordEditText.setOnClickListener(passwordClickListener);
        passwordEditText.setText(password);

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(startButtonClickListener);

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


    }



    private View.OnClickListener nameClickListener= new View.OnClickListener() {
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "nameField");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "passwordField");
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                passwordEdited = true;
                passwordEditText.setText("");
            }
        }
    };



    private View.OnClickListener radioRestClickListener = new View.OnClickListener(){
        public void onClick(View v) {
            radioMQTT.setChecked(false);
            radioMethod="rest";

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "radioRest");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            SharedPreferences mPrefs = getSharedPreferences("label", 0);
            if(!mPrefs.getString("method", "rest").equals("rest")){
                url = qrcodeObject.urlSubHTTP ;
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


            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "radioMQTT");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            SharedPreferences mPrefs = getSharedPreferences("label", 0);
            if(mPrefs.getString("method", "rest").equals("rest")){
                url = qrcodeObject.urlSubMQTT ;
            }


            URLeditText.setText(url);
            savePreferences();
        }
    };






    private View.OnClickListener startButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(),ActuatorsActivity.class);
            Bundle b = new Bundle();
            b.putString("username", nameEditText.getText().toString());
            b.putString("password", passwordEditText.getText().toString());
            b.putString("suburl", URLeditText.getText().toString());
            b.putString("channel", channelEditText.getText().toString());
            b.putString("method",radioMethod);

            b.putBoolean("vibrate",checkBoxVibrate.isChecked());
            b.putBoolean("alert",checkBoxAlert.isChecked());
            b.putBoolean("ring",checkBoxRing.isChecked());
            //b.putBoolean("photo",checkBoxPhoto.isChecked());
            b.putBoolean("photo",false);
            b.putString("frequency",spinnerFrequency.getSelectedItem().toString());

            i.putExtras(b); //Put your id to your next Intent

            Bundle bundle;
            bundle=b;
            bundle.putString("password", "*****");
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "startButton");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            savePreferences();

            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);


        }
    };


    private View.OnClickListener buttonGetQRClickListener= new View.OnClickListener() {
        public void onClick(View v) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "buttonGetQR");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent("function", bundle);

            Intent i = new Intent(getApplicationContext(),MainBarActivity.class);
            Bundle b = new Bundle();
            b.putString("returnActivityClassName", this.getClass().getEnclosingClass().getName().toString());
            i.putExtras(b); //Put your id to your next Intent
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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

        mEditor.putString("suburl", URLeditText.getText().toString()).commit();
        qrcodeObject.userUrl= URLeditText.getText().toString();


        mEditor.putString("endpointsJSON",qrcodeObject.jsonString).commit();



        mEditor.putString("method", radioMethod).commit();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "savePreferences");
        bundle.putString("puburl", mPrefs.getString("puburl",null)==null ? "" : mPrefs.getString("puburl",null).toString());
        bundle.putString("suburl", mPrefs.getString("suburl",null)==null ? "" : mPrefs.getString("suburl",null).toString());
        mFirebaseAnalytics.logEvent("function", bundle);

    }


    private void getPreferencesValues(String qrcode){

        SharedPreferences mPrefs = getSharedPreferences("label", 0);
        if(qrcode==null){
            qrcode=mPrefs.getString("endpointsJSON", null);
            qrcodeObject.userUrl=mPrefs.getString("suburl", null);
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
                url = qrcodeObject.userUrl==null ? qrcodeObject.urlSubHTTP : qrcodeObject.userUrl;
            } else {
                url = qrcodeObject.userUrl==null ? qrcodeObject.urlSubMQTT : qrcodeObject.userUrl;
            }

        }else {
            setDefaults();
        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "getPreferencesValues");
        bundle.putString(FirebaseAnalytics.Param.VALUE, qrcode==null? "" : qrcode.replace(password, "*****"));
        mFirebaseAnalytics.logEvent("function", bundle);

    }




    public void setDefaults(){
        SharedPreferences mPrefs = getSharedPreferences("label", 0);

        userName = getResources().getString(R.string.app_username);
        password = getResources().getString(R.string.app_password);
        radioMethod= radioMethod==null?getResources().getString(R.string.app_method):radioMethod;

        if (radioMethod.equals("rest")) {
            url =mPrefs.getString("suburl", null)==null ? getResources().getString(R.string.app_urlSubHTTP) : mPrefs.getString("suburl", null);
        } else {
            url = mPrefs.getString("suburl", null)==null ? getResources().getString(R.string.app_urlSubMQTT):mPrefs.getString("suburl", null);
        }
    }




}
