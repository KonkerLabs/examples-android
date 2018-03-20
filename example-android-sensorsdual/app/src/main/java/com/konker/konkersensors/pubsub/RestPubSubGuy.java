package com.konker.konkersensors.pubsub;

import com.konker.konkersensors.conn.ConnResponse.ConnResponse;
import com.konker.konkersensors.conn.ErrorCollector.ErrorCollector;
import com.konker.konkersensors.exceptions.failedProcedureException;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class RestPubSubGuy implements  IPosterGuy {
    Exception asyncExeption=null;
    private String userpassBase64Encoded;
    private String targetURL;
    private String RequestMethod;
    private String sensorName;
    private JSONObject jsonParam;
    private IPublishingListener listener;
    private String key;

    public RestPubSubGuy(String userpassBase64Encoded,
                         String targetURL,
                         String RequestMethod,
                         String sensorName,
                         JSONObject jsonParam){
        this.userpassBase64Encoded=userpassBase64Encoded;
        this.targetURL=targetURL;
        this.RequestMethod=RequestMethod;
        this.sensorName=sensorName;
        this.jsonParam=jsonParam;
    }

    public void setPublishingListener(String key, IPublishingListener listener) {
        this.listener = listener;
        this.key = key;
    }

    @Override
    public void run()
    {
        try
        {
            if (listener != null) {
                listener.publishStarted(key);
            }
            connect(userpassBase64Encoded, targetURL, RequestMethod, sensorName, jsonParam.toString());

        }
        catch (failedProcedureException e)
        {
            asyncExeption=e;
            ErrorCollector.addError(e.getMessage(),e.errorCode);
    } finally {
            if (listener != null) {
                listener.publishCompleted(key);
            }
        }
    }

    public String connect(String userpassBase64Encoded, String targetURL, String RequestMethod, String sensorName, String jsonParam) throws failedProcedureException
    {
        URL url;
        HttpURLConnection connection = null;
        if(targetURL.endsWith("/"))
        {
            targetURL = targetURL.substring(0,targetURL.length() - 1);
        }

        try {
            url = new URL(targetURL + "/" + sensorName.replaceAll("[^a-zA-Z]|\\s+", ""));
            connection = (HttpURLConnection) url.openConnection();
            if (!RequestMethod.toLowerCase().equals("get")){
                connection.setDoOutput(true);
            }
            connection.setRequestMethod(RequestMethod); // hear you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            connection.setRequestProperty("content-type", "application/json");
            connection.setRequestProperty("authorization", "Basic " + userpassBase64Encoded);
            connection.setRequestProperty("cache-control", "no-cache");
            connection.setConnectTimeout(1000);

            connection.connect();

            //Send request
            if (!RequestMethod.toLowerCase().equals("get")) {
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(jsonParam);
                wr.flush();
                wr.close();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder resultOK = new StringBuilder();
            String lineOK = "";
            while(( lineOK = br.readLine()) != null){
                resultOK.append(lineOK);
            }
            String exit = resultOK.toString();

            InputStream is;
            int response = connection.getResponseCode();
            //postResponseCode =response;
            if (response == 200) {

                ConnResponse.addResponse(exit);
                return exit;
            } else {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                ConnResponse.addResponse(result.toString());
                throw new failedProcedureException(result.toString(),response);
            }
            }catch(Exception e){
                throw new failedProcedureException(e.getMessage(),e.hashCode());

        }finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
