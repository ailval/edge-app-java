package com.qingcloud.iot.core;

import org.eclipse.paho.client.mqttv3.MqttException;

public interface OnConnectStatusCB {
    public void onConnectStatusCB(boolean isConnected, String details);
}
