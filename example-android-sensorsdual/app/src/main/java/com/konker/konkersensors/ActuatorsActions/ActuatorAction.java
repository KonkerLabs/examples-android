package com.konker.konkersensors.ActuatorsActions;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.LinearLayout;


/**
 * Created by erico on 01/11/2016.
 */

public class ActuatorAction {
    private static Handler handler = new Handler();
    private static Long await=1000L;
    private static LinearLayout alertL;
    private static Ringtone r;
    private static long startMillis=0L;

    public static void vibrate(Context context){
        long millis = System.currentTimeMillis() - startMillis;


        if (System.currentTimeMillis() - startMillis<1000L){
            startMillis=0L;
            return;
        }


        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(await);

        if(startMillis==0L){
            startMillis=System.currentTimeMillis();
        }
    }

    public static void alert(LinearLayout alertLayout){
        if (System.currentTimeMillis() - startMillis<1000L){
            startMillis=0L;
            return;
        }


        alertL=alertLayout;
        doStartLoopAlert();


        if(startMillis==0L){
            startMillis=System.currentTimeMillis();
        }
    }

    public static void ring(Context context){
        if (System.currentTimeMillis() - startMillis<1000L){
            startMillis=0L;
            return;
        }


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        r = RingtoneManager.getRingtone(context, notification);
        doStartLoopRingtone();


        if(startMillis==0L){
            startMillis=System.currentTimeMillis();
        }
    }

    public static void photo(){

    }



    //looop alert
    public static void doStartLoopAlert(){
        alertL.setVisibility(View.VISIBLE);
        handler.postDelayed(runAlert, await);
    }
    public static Runnable runAlert = new Runnable() {
        @Override
        public void run() {

            alertL.setVisibility(View.INVISIBLE);
            //handler.postDelayed(this, await);
        }
    };


    //looopRingtone
    public static void doStartLoopRingtone(){
        r.play();
        handler.postDelayed(runRing, await);
    }
    public static Runnable runRing = new Runnable() {
        @Override
        public void run() {

            r.stop();
            //handler.postDelayed(this, await);
        }
    };


}
