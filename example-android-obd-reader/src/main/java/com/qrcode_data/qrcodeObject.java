package com.qrcode_data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by erico on 27/10/2016.
 */

public class qrcodeObject {
    public static String username=null;
    public static String password=null;
    public static String urlPubHTTP=null;
    public static String urlPubHTTPS=null;
    public static String urlSubHTTP=null;
    public static String urlSubHTTPS=null;
    public static String urlSubMQTT=null;
    public static String urlSubMQTTS=null;
    public static String urlPubMQTT=null;
    public static String urlPubMQTTS=null;
    public static String jsonString=null;
    public static String userUrl=null;





    public static void qrcodeObject(String qrcode){
        getJSONData(qrcode);
    }

    public static void getJSONData(String qrcode){
        try {
            JSONObject GeneralSettings = new JSONObject(qrcode);
            //{"user": "c8h1bgul093p","pass": "OA5biABMBMSg","uri": "dev-server","http":"8080","https":"443","mqtt":"1883","mqtt-tls":"1883","pub":" pub/c8h1bgul093p","sub":" sub/c8h1bgul093p "}


            jsonString=qrcode;


            username=GeneralSettings.getString("user");
            password=GeneralSettings.getString("pass");



            urlPubHTTP="http://" + GeneralSettings.getString("host") + ":" + GeneralSettings.getString("http")  + GeneralSettings.getString("ctx") + "/" + GeneralSettings.getString("pub");
            urlPubHTTPS="https://" + GeneralSettings.getString("host") + ":" + GeneralSettings.getString("http")  + GeneralSettings.getString("ctx") + "/" + GeneralSettings.getString("pub");
            urlSubHTTP="http://" + GeneralSettings.getString("host") + ":" + GeneralSettings.getString("http") + GeneralSettings.getString("ctx") + "/" + GeneralSettings.getString("sub");
            urlSubHTTPS="https://" + GeneralSettings.getString("host") + ":" + GeneralSettings.getString("http")  + GeneralSettings.getString("ctx") + "/" +  GeneralSettings.getString("sub");
            urlSubMQTT="mqtt://" + GeneralSettings.getString("host-mqtt") + ":" + GeneralSettings.getString("mqtt");
            urlSubMQTTS="mqtts://" + GeneralSettings.getString("host-mqtt") + ":" + GeneralSettings.getString("mqtt-tls");
            urlPubMQTT="mqtt://" + GeneralSettings.getString("host-mqtt") + ":" + GeneralSettings.getString("mqtt");
            urlPubMQTTS="mqtts://" + GeneralSettings.getString("host-mqtt") + ":" + GeneralSettings.getString("mqtt-tls");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateJSONData(String item, String value ){
        if(value==null || jsonString==null){
            return;
        }
        try {
            JSONObject GeneralSettings = new JSONObject(jsonString);
            GeneralSettings.put(item,value);
            jsonString=GeneralSettings.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

