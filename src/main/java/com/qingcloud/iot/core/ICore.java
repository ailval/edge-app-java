package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkMessageData;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface ICore {
    public void init();
    public void cleanup() throws MqttException;
    public void start() throws MqttException;
    public void stop() throws MqttException;
    public Error sendMessage(AppSdkMessageData data) throws Exception;
}
