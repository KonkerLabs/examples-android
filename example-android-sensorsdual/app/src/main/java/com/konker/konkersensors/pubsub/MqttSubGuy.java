package com.konker.konkersensors.pubsub;


import com.konker.konkersensors.conn.ConnResponse.ConnResponse;
import com.konker.konkersensors.conn.ErrorCollector.ErrorCollector;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;


public class MqttSubGuy implements  IPosterGuy, MqttCallback {
    Exception asyncExeption=null;
    private String topicName;
    private MqttClient client;
    private MqttConnectOptions conOpt;
    private IPublishingListener listener;
    private String key;

    public MqttSubGuy(String topicName, MqttClient client, MqttConnectOptions conOpt){
        this.topicName=topicName;
        this.client=client;
        this.conOpt=conOpt;
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
            mqttSubscribe(topicName, client, conOpt);


        }
        catch (Exception e)
        {
            asyncExeption=e;
            ErrorCollector.addError(e.getMessage());
        } finally {
            if (listener != null) {
                listener.publishCompleted(key);
            }
        }
    }



    public void mqttSubscribe(String topicName, MqttClient client, MqttConnectOptions conOpt) throws MqttException{


        // Construct an MQTT blocking mode client
        // MqttClient client = new MqttClient(brokerUrl, userName, null);

        if(!client.isConnected()){
            client.setCallback(this);
            // ConnectSensors to the MQTT server
            client.connect(conOpt);

            // Subscribe to the requested topic
            // The QoS specified is the maximum level that messages will be sent to the client at.
            // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
            // be downgraded to 1 when delivering to the client but messages published at 1 and 0
            // will be received at the same level they were published at.

            client.subscribe(topicName, 0);

            ConnResponse.addResponse("connected");
        }



    }




    @Override
    public void connectionLost(Throwable cause) {

    }

    /**
     *
     * messageArrived
     * This callback is invoked when a message is received on a subscribed topic.
     *
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        ConnResponse.addResponse( new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
