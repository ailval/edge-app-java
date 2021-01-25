package com.qingcloud.iot.core.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class IoTMqttCallback implements MqttCallbackExtended {
    private IoTMqttClient ioTMqttClient;

    public IoTMqttClient getIoTMqttClient() {
        return ioTMqttClient;
    }

    public IoTMqttCallback(IoTMqttClient ioTMqttClient) {
        this.ioTMqttClient = ioTMqttClient;
    }

    @Override
    public void connectComplete(boolean reconnect,String serverURI) {
        System.out.println("IoTMqttCallback connectComplete:" + reconnect + ", serverURI:" + serverURI);

        if (ioTMqttClient.getOnConnectedCallback() != null) {
            ioTMqttClient.getOnConnectedCallback().onConnectedCallback(reconnect,serverURI);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
        System.out.println("IoTMqttCallback connectionLost:" + cause.getLocalizedMessage());

        if (ioTMqttClient.getOnDisconnectedCallback() != null) {
            ioTMqttClient.getOnDisconnectedCallback().onDisconnectedCallback(true, cause);
        }
    }

    @Override
    public void messageArrived(String topic,MqttMessage message) {
        System.out.println("IoTMqttCallback messageArrived:" + topic + ", message:" + new String(message.getPayload()));
        ioTMqttClient.onMessageArrived(topic, message);
        if (ioTMqttClient.getMessageCallback() != null) {
            ioTMqttClient.getMessageCallback().messageCallback(topic, message.getPayload());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            System.out.println("IoTMqttCallback deliveryComplete:" + new String(token.getMessage().getPayload()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
