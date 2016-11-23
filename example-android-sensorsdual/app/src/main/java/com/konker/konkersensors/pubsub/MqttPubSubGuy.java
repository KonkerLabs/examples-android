package com.konker.konkersensors.pubsub;


import com.konker.konkersensors.conn.ErrorCollector.ErrorCollector;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MqttPubSubGuy implements  IPosterGuy {
    Exception asyncExeption=null;
    private String topicName;
    private String payload;
    private MqttClient client;
    private MqttConnectOptions conOpt;
    private IPublishingListener listener;
    private String key;

    public MqttPubSubGuy(String topicName, String payload, MqttClient client, MqttConnectOptions conOpt){
        this.topicName=topicName;
        this.payload=payload;
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
            mqttPublish(topicName, payload,client, conOpt);

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


    public void mqttPublish(String topicName, String payload, MqttClient client, MqttConnectOptions conOpt) throws MqttException{


        // Construct an MQTT blocking mode client
       // MqttClient client = new MqttClient(brokerUrl, userName, null);

        if(!client.isConnected()){

            // ConnectSensors to the MQTT server
            client.connect(conOpt);
        }

        // Create and configure a message
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(0);

        client.publish(topicName, message); // Blocking publish

        // Subscribe to the requested topic
        // The QoS specified is the maximum level that messages will be sent to the client at.
        // For instance if QoS 1 is specified, any messages originally published at QoS 2 will
        // be downgraded to 1 when delivering to the client but messages published at 1 and 0
        // will be received at the same level they were published at.

        //client.subscribe(topicName, 0);


    }


}
