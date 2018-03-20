package com.konker.konkersensors;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends Activity  {
    Button sensorColectorButton;
    Button actuatorButton;
    private FirebaseAnalytics mFirebaseAnalytics;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "MainActivity");
        //bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "activity");
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    /* create a full screen window */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

         super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);



        //get APP version
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView versionTextView  = (TextView) findViewById(R.id.versionTextView);
            versionTextView.setText("Version: " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }





        sensorColectorButton = (Button) findViewById(R.id.sensorColectorButton);
        sensorColectorButton.setOnClickListener(sensorColectorButtonOnClickListener);


//        actuatorButton = (Button) findViewById(R.id.actuatorButton);
//        actuatorButton.setOnClickListener(actuatorButtonOnClickListener);





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




    private View.OnClickListener sensorColectorButtonOnClickListener= new View.OnClickListener() {
        public void onClick(View v) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "sensorColectorButton");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Intent i = new Intent(getApplicationContext(),SensorsMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        }
    };


    private View.OnClickListener actuatorButtonOnClickListener= new View.OnClickListener() {
        public void onClick(View v) {

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "actuatorButton");
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "click");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Intent i = new Intent(getApplicationContext(),ActuatorsMainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);


        }
    };

}
