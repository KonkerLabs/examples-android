package com.konker.konkersensors.pubsub;

/**
 * Created by erico on 08/11/2016.
 */

public interface IPublishingListener {
    void publishCompleted(String key);

    void publishStarted(String key);
}
