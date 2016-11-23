package com.konker.konkersensors.pubsub;

/**
 * Created by erico on 08/11/2016.
 */

public interface IPosterGuy extends Runnable {
    void setPublishingListener(String key, IPublishingListener listener);
}
