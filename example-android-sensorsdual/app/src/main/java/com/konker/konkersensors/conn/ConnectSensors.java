package com.konker.konkersensors.conn;

import android.hardware.Sensor;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.konker.konkersensors.SensorsObjects.SensorsObjects;
import com.konker.konkersensors.conn.ErrorCollector.ErrorCollector;
import com.konker.konkersensors.conn.ErrorCollector.ErrorObject;
import com.konker.konkersensors.exceptions.failedProcedureException;
import com.konker.konkersensors.pubsub.IPosterGuy;
import com.konker.konkersensors.pubsub.IPublishingListener;
import com.konker.konkersensors.pubsub.MqttPubGuy;
import com.konker.konkersensors.pubsub.RestPubSubGuy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConnectSensors {

    private SensorsObjects sensorObj=new SensorsObjects();
    private boolean shouldRun = true;
    public Handler handler;
    private Long await = 1000L;
    private Long awaitorig = await;
    private PubSubManager pubSubManager = new PubSubManager();



    ExecutorService executor=Executors.newFixedThreadPool(5);
    ExecutorService executorScreen;
    MqttClient client;
    MqttConnectOptions conOpt;



    public ConnectSensors(SensorsObjects sensorObj) {
        this.handler = new Handler();
        this.sensorObj.connection = sensorObj.connection;
        this.sensorObj.screenObjects.backButton=sensorObj.screenObjects.backButton;
        this.sensorObj.screenObjects.usermessageView=sensorObj.screenObjects.usermessageView;

        this.sensorObj.sensorList = sensorObj.sensorList;
        this.sensorObj.startTime=sensorObj.startTime;
        this.sensorObj.sensorValues=sensorObj.sensorValues;
        this.sensorObj.screenObjects.debugViews=sensorObj.screenObjects.debugViews;
        this.sensorObj.screenObjects.timerTextView=sensorObj.screenObjects.timerTextView;
        this.sensorObj.androidLocation=sensorObj.androidLocation;
        doStartLoop();
    }


//looop
    public void doStartLoop(){

        handler.postDelayed(runnable, await);
    }

    private class PubSubManager implements IPublishingListener {
        private HashMap<String, IPosterGuy> postersInExecution = new HashMap<String, IPosterGuy>();

        public void publishCompleted(String key) {
            synchronized (postersInExecution) {
                postersInExecution.remove(key);
            }
        }

        public void publishStarted(String key) {
            // do nothing
        }


        public void scheduleJobExecution(String key, IPosterGuy r) {
            synchronized (postersInExecution) {
                if (!postersInExecution.containsKey(key)) {
                    r.setPublishingListener(key, this);
                    executor.submit(r);
                    postersInExecution.put(key, r);
                }
            }
        }
    }




    public Runnable runnable = new Runnable() {
        @Override
        public void run() {

            doUpdateSensorValuesOnScreen();




            try {
                doConnect_And_PostDelayedAgain(this, shouldRun, sensorObj);


                System.out.println("\nFinished all threads");
            } catch (Exception e) {
                setMessageVisible(e.getMessage());
            }
        }
    };


    public Runnable runScreenUpdates = new Runnable() {
        @Override
        public void run() {

            doUpdateSensorValuesOnScreen();

        }
    };




    public void doConnect_And_PostDelayedAgain(Runnable ru, Boolean shouldRun, SensorsObjects sendObj) throws JSONException, MqttException {
        if(shouldRun) {
            try {
                if (sendObj!=null) {
                    setMessageInvisible();

                    sendAll(sendObj);


                }
            } catch (failedProcedureException ie) {
                if(ie.errorCode==401){
                    shouldRun=false;
                }
                setMessageVisible(ie.getMessage());
                ErrorCollector.clear();

            } finally {
                handler.postDelayed(ru, await);
            }
        }
    }



    private void sendAll(SensorsObjects sensorObj ) throws failedProcedureException, JSONException, MqttException {
        Map<String, float[]> s = null;
        synchronized (sensorObj.sensorValues) {
            s = new HashMap<String, float[]>(sensorObj.sensorValues);
        }
        for (String sensorName: s.keySet())  {
            JSONObject json = new JSONObject();

            json.put("timestamp", new Date().toString());

            float[] values = s.get(sensorName);
            for (int i = 0; i < values.length; i++) {
                //populate json with sensor values
                json.put("val" + (i + 1), Double.valueOf(values[i]));
            }

            //encode uswername and password
            String userpass = sensorObj.connection.username + ":" + sensorObj.connection.password;
            String base64userpass = Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);

            if(!shouldRun){
                break;
            }

            if (sensorObj.connection.transmitMethod.equals("rest")){

                RestPubSubGuy restPoster= new RestPubSubGuy(base64userpass, sensorObj.connection.url, "POST", sensorName, json);

                pubSubManager.scheduleJobExecution(sensorName, restPoster);
            }else{

                if(client==null || conOpt==null) {

                    //"ssl://" or "tcp://"
                    String sendURL=sensorObj.connection.url;
                    sendURL=sendURL.replace("mqtt://","tcp://");
                    sendURL=sendURL.replace("mqtts://","ssl://");

                    client = new MqttClient(sendURL, sensorObj.connection.username, null);
                    conOpt = new MqttConnectOptions();
                    conOpt.setCleanSession(true);
                    conOpt.setUserName(sensorObj.connection.username);
                    conOpt.setPassword(sensorObj.connection.password.toCharArray());
                }



                sensorName=sensorName.replaceAll("[^a-zA-Z]|\\s+", "");
                MqttPubGuy mqttPoster= new MqttPubGuy("pub/" + sensorObj.connection.username+ "/"+ sensorName, json.toString(),client, conOpt);
                pubSubManager.scheduleJobExecution(sensorName, mqttPoster);
            }





            if (ErrorCollector.hasError()){
                await=5000L;// wait more time for the next cicle
                ErrorObject err =ErrorCollector.getFirstErrorMessage();
                throw new failedProcedureException(err.message, err.errorCode);
            }else{
                await=awaitorig;
            }
        }


    }




    private void setMessageVisible(String message){
        if (sensorObj.screenObjects.backButton.getVisibility()== View.INVISIBLE){
            sensorObj.screenObjects.backButton.setVisibility(View.VISIBLE);
            sensorObj.screenObjects.usermessageView.setVisibility(View.VISIBLE);
        }
        sensorObj.screenObjects.usermessageView.loadData(message, "text/html; charset=utf-8", "utf-8");

      }

    private void setMessageInvisible(){
        if (sensorObj.screenObjects.backButton.getVisibility()==View.VISIBLE){
            sensorObj.screenObjects.backButton.setVisibility(View.INVISIBLE);
            sensorObj.screenObjects.usermessageView.loadData("", "text/html; charset=utf-8", "utf-8");
            sensorObj.screenObjects.usermessageView.setVisibility(View.INVISIBLE);
        }

    }


    public void updateTimer() {
        long millis = System.currentTimeMillis() - sensorObj.startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;


        sensorObj.screenObjects.timerTextView.setText(String.format("%d:%02d", minutes, seconds));

    }






    public void doUpdateSensorValuesOnScreen() {
        updateTimer();
        int dv = 0;
        if(sensorObj.sensorList!=null){
            if (sensorObj.sensorList.size() > 0) {

                for (Sensor val : sensorObj.sensorList) {
                    if (val != null) {
                        TextView debugTextView = sensorObj.screenObjects.debugViews[dv];
                        String sensorname = val.getName().replaceAll("[^0-9A-Za-z]", "");

                        updateSensorValuesOnScreen(sensorname, debugTextView);


                        dv++;
                    }
                }
            }

            if(sensorObj.androidLocation!=null){
                TextView debugTextViewLocation = sensorObj.screenObjects.debugViews[dv];
                TextView debugTextViewSpeed = sensorObj.screenObjects.debugViews[dv+1];
                TextView debugTextViewAltitude = sensorObj.screenObjects.debugViews[dv+2];

                String lat =sensorObj.androidLocation.getFusedLatitude();
                String lon =sensorObj.androidLocation.getFusedLongitude();
                String speed =sensorObj.androidLocation.getFusedSpeed();
                String altitude =sensorObj.androidLocation.getFusedAltitude();

                if(lat!=null && lon!=null){
                    sensorObj.sensorValues.put("Location", new float[]{Float.parseFloat(lat), Float.parseFloat(lon)});
                    debugTextViewLocation.setText("Location\nlat: " + lat +"\nlon: " + lon);
                }

                if(speed!=null){
                    sensorObj.sensorValues.put("Speed", new float[]{Float.parseFloat(speed)});
                    debugTextViewSpeed.setText("Speed\n" + speed);
                }
                if(altitude!=null){
                    sensorObj.sensorValues.put("Altitude", new float[]{Float.parseFloat(altitude)});
                    debugTextViewAltitude.setText("Altitude\n" + altitude );
                }






            }
        }

    }



    public void stopAll() {
        setMessageInvisible();
        ErrorCollector.clear();
        try{
            sensorObj.androidLocation.stopFusedLocation();
        }catch (Exception e){
            //error sileced
        }

        try {
            client.disconnect();
        }catch (Exception e){
            //error sileced
        }
        try {
            client.close();
        }catch (Exception e){
            //error sileced
        }
        try {
            executor.shutdown();
        }catch (Exception e){
            //error sileced
        }
        try {
            executor.shutdownNow();
        }catch (Exception e){
            //error sileced
        }

    }


    //method to get the sensors data, any exception will be threated in the caller method
    public void updateSensorValuesOnScreen(String sensorName, TextView outputTextView)  {
        float values[]= sensorObj.sensorValues.get(sensorName);


        outputTextView.setText(sensorName  +"\n");


        String debugString= "";

        if(values.length==0 || values==null){
            //no value
            outputTextView.setText("No data!" + "\n");
        }else {
            //loop through all values of the sensor
            int sensorValues = 1;
            for (float val : values) {
                debugString = outputTextView.getText().toString();
                //print on screen the value
                outputTextView.setText(debugString + String.valueOf(val) + "\n");

            }
        }

    }



}
