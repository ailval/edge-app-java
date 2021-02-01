package com.qingcloud.iot.core.mqttclient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
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
        if (ioTMqttClient.getOnConnectedCallback() != null) {
            ioTMqttClient.getOnConnectedCallback().onConnectedCallback(reconnect,serverURI);
        }

        if (ioTMqttClient.getOnConnectStatusCB() != null) {
            ioTMqttClient.getOnConnectStatusCB().onConnectStatusCB(reconnect, serverURI);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();

        if (ioTMqttClient.getOnDisconnectedCallback() != null) {
            ioTMqttClient.getOnDisconnectedCallback().onDisconnectedCallback(true, cause);
        }

        if (ioTMqttClient.getOnConnectStatusCB() != null) {
            ioTMqttClient.getOnConnectStatusCB().onConnectStatusCB(false, cause.getLocalizedMessage());
        }
    }

    @Override
    public void messageArrived(String topic,MqttMessage message) {
        System.out.println("IoTMqttCallback messageArrived:" + topic + ", message:" + new String(message.getPayload()));
        if (ioTMqttClient.getOnRecvData() != null) {
            ioTMqttClient.getOnRecvData().onRecvData(topic,message.getPayload());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //需确认消息推送成功时，可设置此方法的回调函数
    }
}
