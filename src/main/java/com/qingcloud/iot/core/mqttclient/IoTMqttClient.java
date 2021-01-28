package com.qingcloud.iot.core.mqttclient;

import com.qingcloud.iot.core.*;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.*;

public class IoTMqttClient {

    public AppCoreClient getAppCoreClient() {
        return appCoreClient;
    }

    public void setAppCoreClient(AppCoreClient appCoreClient) {
        this.appCoreClient = appCoreClient;
    }

    private AppCoreClient appCoreClient;
    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;

    private Timer timer;
    private TimerTask timerTask;
    private IMqttToken token;

    private IMessageCallback messageCallback;
    private IOnConnectedCallback onConnectedCallback;
    private IOnDisconnectedCallback onDisconnectedCallback;

    private String[] topics;

    private static final int DEFAULT_KEEP_ALIVE_DEFAULT = 60;
    private static final int DEFAULT_WAIT_TIMEOUT = 30;
    private static final int DEFAULT_TIMES_DELAY = 1000;
    private static final int DEFAULT_TIMES_PERIOD = 10000;

    public IMqttToken getToken() {
        return token;
    }

    public void setToken(IMqttToken token) {
        this.token = token;
    }

    public IOnConnectedCallback getOnConnectedCallback() {
        return onConnectedCallback;
    }

    public void setOnConnectedCallback(IOnConnectedCallback onConnectedCallback) {
        this.onConnectedCallback = onConnectedCallback;
    }

    public IOnDisconnectedCallback getOnDisconnectedCallback() {
        return onDisconnectedCallback;
    }

    public void setOnDisconnectedCallback(IOnDisconnectedCallback onDisconnectedCallback) {
        this.onDisconnectedCallback = onDisconnectedCallback;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public MqttConnectOptions getMqttConnectOptions() {
        return mqttConnectOptions;
    }

    public void setMqttConnectOptions(MqttConnectOptions mqttConnectOptions) {
        this.mqttConnectOptions = mqttConnectOptions;
    }

    public IMessageCallback getMessageCallback() {
        return messageCallback;
    }

    public void setMessageCallback(IMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public IoTMqttClient(String clientId,String url,OnConnectStatusCB connectStatusCB,IMessageCallback messageCallback,IEventCallback eventCallback) throws MqttException {
        this.messageCallback = messageCallback;
        this.mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{url});
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(DEFAULT_KEEP_ALIVE_DEFAULT);
        mqttConnectOptions.setConnectionTimeout(DEFAULT_WAIT_TIMEOUT);
        this.mqttConnectOptions = mqttConnectOptions;
    }

    public IoTMqttClient(String clientId, String url, OnConnectStatusCB connectStatusCB) throws MqttException {
        this(clientId,url,connectStatusCB,null,null);
    }

    public IoTMqttClient(String clientId, String url) throws MqttException {
        this(clientId,url,null,null,null);
    }

    public void start () throws MqttException {
        this.doConnect();
    }

    public void stop () throws MqttException {
        this.doDisconnect();
    }

    public void subscribe(String topic, int qos, IMessageCallback messageCallback) throws Exception {
        if ((topic == null || qos < 0 || qos > 2))
            throw new Exception("invalid arguments");

        if (this.messageCallback == null ) {
            if (messageCallback != null) {
                this.messageCallback = messageCallback;
            }
        }

        this.mqttClient.subscribe(topic,qos);
    }

    public void subscribeMultiple(String[] topics, IMessageCallback messageCallback) throws MqttException {
        if (this.messageCallback == null ) {
            if (messageCallback != null) {
                this.messageCallback = messageCallback;
            }
        }

        for (int i=0; i<topics.length; i++) {
            if (topics[i] != null && !topics[i].equals("")) {
                String topic = topics[i];
                try {
                    this.subscribe(topic,0,messageCallback);
                    if (this.messageCallback == null ) {
                        if (messageCallback != null) {
                            this.messageCallback = messageCallback;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void unsubscribe(String[] topics) throws MqttException {
        this.mqttClient.unsubscribe(topics);
    }

    public void publish(String topic,int qos,byte[] payload) throws Exception {
        if ((topic == null || qos < 0 || qos > 2)) {
            throw new Exception("invalid arguments");
        }
        this.mqttClient.publish(topic,payload,qos,true);
    }

    public void doConnect() throws MqttException {
        if (this.mqttClient.isConnected()) {
        } else {
            if (mqttConnectOptions == null) {
                this.tryConnect();
            } else {
                this.mqttClient.connectWithResult(mqttConnectOptions);
            }
        }
    }

    public void doDisconnect() throws MqttException {
        this.cancelConnect();
    }

    /*
     * private methods
     */
    private void tryConnect() throws MqttException {
        this.token  = this.mqttClient.connectWithResult(mqttConnectOptions);

        Timer timer = new Timer("com.qingcloud.iot");
        this.timer = timer;

        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (token instanceof IMqttToken && mqttClient.isConnected()) {
                    //调试用，显示Mqttclient是否连接
//                    System.out.println("Connecting ... token:" + token + ", isConnected:" + mqttClient.isConnected());

                    return;
                } else {
                    try {
                        token = mqttClient.connectWithResult(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    //调试用，显示Mqttclient是否重新连接
//                    System.out.println("Reconnecting ... token:" + token + ", isConnected:" + mqttClient.isConnected());
                }
            }
        };

        timerTask = task;
//        timer.schedule(timerTask, DEFAULT_TIMES_DELAY);
        timer.schedule(timerTask, DEFAULT_TIMES_DELAY, DEFAULT_TIMES_PERIOD);
    }

    private void cancelConnect() throws MqttException {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
        if (this.mqttClient.isConnected()) {
            this.mqttClient.disconnect();
        }
    }
}
