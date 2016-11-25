package com.konker.konkersensors.conn;


import android.os.Handler;
import android.util.Base64;
import android.view.View;

import com.konker.konkersensors.ActuatorsActions.ActuatorAction;
import com.konker.konkersensors.ActuatorsObjects.ActuatorsObjects;
import com.konker.konkersensors.conn.ConnResponse.ConnResponse;
import com.konker.konkersensors.conn.ErrorCollector.ErrorCollector;
import com.konker.konkersensors.conn.ErrorCollector.ErrorObject;
import com.konker.konkersensors.exceptions.failedProcedureException;
import com.konker.konkersensors.pubsub.MqttPubGuy;
import com.konker.konkersensors.pubsub.MqttSubGuy;
import com.konker.konkersensors.pubsub.RestPubSubGuy;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectActuators {

    private ActuatorsObjects actuatorObj= new ActuatorsObjects();
    private boolean shouldRun = true;
    public Handler handler;
    private Long await = 1000L;
    private Long awaitorig = await;
    private String lastResponse="";


    ExecutorService executor = Executors.newSingleThreadExecutor();
    MqttClient client;
    MqttConnectOptions conOpt;



    public ConnectActuators(ActuatorsObjects actuatorObj) {
        this.handler = new Handler();
        this.actuatorObj.connection = actuatorObj.connection;
        this.actuatorObj.screenObjects.backButton=actuatorObj.screenObjects.backButton;
        this.actuatorObj.screenObjects.usermessageView=actuatorObj.screenObjects.usermessageView;

        this.actuatorObj.vibrate=actuatorObj.vibrate;
        this.actuatorObj.alert=actuatorObj.alert;
        this.actuatorObj.ring=actuatorObj.ring;
        this.actuatorObj.photo=actuatorObj.photo;


        this.actuatorObj.startTime=actuatorObj.startTime;
        this.actuatorObj.screenObjects.timerTextView=actuatorObj.screenObjects.timerTextView;
        this.actuatorObj.screenObjects.debugTextView=actuatorObj.screenObjects.debugTextView;
        this.actuatorObj.screenObjects.alertLayout=actuatorObj.screenObjects.alertLayout;
        this.actuatorObj.context=actuatorObj.context;
        this.actuatorObj.frequency=actuatorObj.frequency;

        setFrequency(actuatorObj.frequency);
        doStartLoop();
    }

    public void setFrequency(String frequency) {
        frequency=frequency.toLowerCase();
        switch (frequency) {
            case "each second":
                await= 1000L;
                break;
            case "each minute":
                await= 60*1000L;
                break;
            case "each 5 minutes":
                await= 5*60*1000L;
                break;
            case "each 30 minutes":
                await= 30*60*1000L;
                break;
            case "each hour":
                await= 60*60*1000L;
                break;
            case "each day":
                await= 24*60*60*1000L;
                break;
            default:
                await= 1000L;
                break;
        }

    }




//looop
    public void doStartLoop(){

        handler.postDelayed(runnable, await);
    }
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {


            //callActuators
            callActuators(actuatorObj);

            try {
                doConnect_And_PostDelayedAgain(this, shouldRun, actuatorObj);
            } catch (Exception e) {
                setMessageVisible(e.getMessage());
            }
        }
    };



    public void callActuators(ActuatorsObjects actuatorObj){
        if(ConnResponse.lastResponse==null){
            return;
        }
        String res = ConnResponse.lastResponse;
        Boolean updated=false;

        if(lastResponse.equals(res)){
            actuatorObj.screenObjects.debugTextView.setText("no updates");
        }else if(actuatorObj.screenObjects.debugTextView.getText().equals("")){
            lastResponse=res;
         }else{
            actuatorObj.screenObjects.debugTextView.setText(res);
            lastResponse=res;
            updated=true;
        }

        updateTimer();

        if(res.toLowerCase().indexOf("start")>-1 && (updated || !actuatorObj.connection.transmitMethod.equals("rest"))){
           if(!actuatorObj.connection.transmitMethod.equals("rest")){
                ConnResponse.clear();
            }
            if(actuatorObj.vibrate){
                ActuatorAction.vibrate(actuatorObj.context);
            }
            if(actuatorObj.alert){
                ActuatorAction.alert(actuatorObj.screenObjects.alertLayout);
            }
            if(actuatorObj.ring){
                ActuatorAction.ring(actuatorObj.context);
            }
            if(actuatorObj.photo){
                ActuatorAction.photo();
            }

        }else{
            if(actuatorObj.connection.transmitMethod.equals("rest")){
                stopActuators();
            }
        }

    }


public  void stopActuators(){

}



    public void updateTimer() {
        long millis = System.currentTimeMillis() - actuatorObj.startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;


        actuatorObj.screenObjects.timerTextView.setText(String.format("%d:%02d", minutes, seconds));

    }





    //SEND ALL
    public void doConnect_And_PostDelayedAgain(Runnable ru, Boolean shouldRun, ActuatorsObjects actuatorObj) throws JSONException, MqttException {
        if(shouldRun) {
            try {
                if (actuatorObj!=null) {
                    setMessageInvisible();
                    subAll(actuatorObj);


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




    private void subAll(ActuatorsObjects actuatorObj ) throws failedProcedureException, JSONException, MqttException {


        //encode uswername and password
        String userpass = actuatorObj.connection.username + ":" + actuatorObj.connection.password;
        String base64userpass = Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);


        JSONObject json = new JSONObject();

        json.put("timestamp", new Date().toString());


        if (actuatorObj.connection.transmitMethod.equals("rest")){

            RestPubSubGuy restPoster= new RestPubSubGuy(base64userpass, actuatorObj.connection.url, "GET", actuatorObj.connection.channel, json);
            executor.execute(restPoster);

        }else{

            if(client==null || conOpt==null) {

                //"ssl://" or "tcp://"
                String sendURL=actuatorObj.connection.url;
                sendURL=sendURL.replace("mqtt://","tcp://");
                sendURL=sendURL.replace("mqtts://","ssl://");

                client = new MqttClient(sendURL, actuatorObj.connection.username, null);
                conOpt = new MqttConnectOptions();
                conOpt.setCleanSession(true);
                conOpt.setUserName(actuatorObj.connection.username);
                conOpt.setPassword(actuatorObj.connection.password.toCharArray());
            }




            MqttSubGuy mqttSubs= new MqttSubGuy("sub/" + actuatorObj.connection.username+ "/" + actuatorObj.connection.channel, client, conOpt);
            executor.execute(mqttSubs);
        }


        if (ErrorCollector.hasError()){
            await=5000L;// wait more time for the next cicle
            ErrorObject err =ErrorCollector.getFirstErrorMessage();
            throw new failedProcedureException(err.message, err.errorCode);
        }else{
            await=awaitorig;
        }

    }




    private void setMessageVisible(String message){
        if (actuatorObj.screenObjects.backButton.getVisibility()== View.INVISIBLE){
            actuatorObj.screenObjects.backButton.setVisibility(View.VISIBLE);
            actuatorObj.screenObjects.usermessageView.setVisibility(View.VISIBLE);
        }
        actuatorObj.screenObjects.usermessageView.loadData(message, "text/html; charset=utf-8", "utf-8");

      }

    private void setMessageInvisible(){
        if (actuatorObj.screenObjects.backButton.getVisibility()==View.VISIBLE){
            actuatorObj.screenObjects.backButton.setVisibility(View.INVISIBLE);
            actuatorObj.screenObjects.usermessageView.loadData("", "text/html; charset=utf-8", "utf-8");
            actuatorObj.screenObjects.usermessageView.setVisibility(View.INVISIBLE);
        }

    }






    public void stopAll() {
        setMessageInvisible();
        ErrorCollector.clear();
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




}
