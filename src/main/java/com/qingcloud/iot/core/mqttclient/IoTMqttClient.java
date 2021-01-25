package com.qingcloud.iot.core.mqttclient;

import com.qingcloud.iot.core.*;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.*;

public class IoTMqttClient {
    private MqttClient mqttClient;
    private MqttConnectOptions mqttConnectOptions;

    private Timer timer;
    private TimerTask timerTask;

    public IMqttToken getToken() {
        return token;
    }

    private IMqttToken token;

    public void setToken(IMqttToken token) {
        this.token = token;
    }

    private IMessageCallback messageCallback;
    private IEventCallback eventCallback;
    private OnConnectStatusCB onConnectStatusCallback;

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

    private IOnConnectedCallback onConnectedCallback;
    private IOnDisconnectedCallback onDisconnectedCallback;

    private String[] topics;

    private static final int DEFAULT_KEEP_ALIVE_DEFAULT = 60;
    private static final int DEFAULT_WAIT_TIMEOUT = 30;
    private static final int DEFAULT_TIMES_DELAY = 1000;
    private static final int DEFAULT_TIMES_PERIOD = 10000;

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public IEventCallback getEventCallback() {
        return eventCallback;
    }

    public void setEventCallback(IEventCallback eventCallback) {
        this.eventCallback = eventCallback;
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

    public OnConnectStatusCB getConnectStatusCB() {
        return onConnectStatusCallback;
    }

    public void setConnectStatusCB(OnConnectStatusCB connectStatusCB) {
        this.onConnectStatusCallback = connectStatusCB;
    }

    public IMessageCallback getMessageCallback() {
        return messageCallback;
    }

    public void setMessageCallback(IMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public IoTMqttClient(String clientId,String url,OnConnectStatusCB connectStatusCB,IMessageCallback messageCallback,IEventCallback eventCallback) throws MqttException {
        this.onConnectStatusCallback = connectStatusCB;
        this.messageCallback = messageCallback;
        this.eventCallback = eventCallback;
        this.mqttClient = new MqttClient(url, clientId, new MemoryPersistence());
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{url});
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(false);
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
        this.tryConnect();
    }

    public void stop () throws MqttException {
        this.cancelConnect();
    }

    public void subscribe(String topic, int qos, IMessageCallback messageCallback) throws Exception {
        if ((topic == null || qos < 0 || qos > 2))
            throw new Exception("invalid arguments");

        this.mqttClient.subscribe(topic,qos,new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic,MqttMessage message) throws Exception {
                messageCallback.messageCallback(topic, message.getPayload());
            }
        });
    }

    public void subscribeMultiple(String[] topics, IMessageCallback messageCallback) throws MqttException {
        this.setMessageCallback(messageCallback);
        ArrayList<IMqttMessageListener> arrayList = new ArrayList<>();

        for (int i=0; i<topics.length; i++) {
            if (topics[i] != null && !topics[i].equals("")) {
                String topic = topics[i];
                try {
                    this.subscribe(topic,0,messageCallback);
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
            return;
        } else {
            if (mqttConnectOptions == null) {
                tryConnect();
            } else {
                this.mqttClient.connectWithResult(mqttConnectOptions);
            }
        }
    }

    public void doDisconnect() throws MqttException {
        if (this.mqttClient.isConnected()) {
            this.mqttClient.disconnect();
        } else {
            return;
        }
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
                    System.out.println("tryConnect token:" + token + ", isConnected:" + mqttClient.isConnected());

                    return;
                } else {
                    try {
                        token = mqttClient.connectWithResult(mqttConnectOptions);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    System.out.println("tryConnect new token:" + token + ", isConnected:" + mqttClient.isConnected());
                }
            }
        };

        timerTask = task;
//        timer.schedule(timerTask, DEFAULT_TIMES_DELAY);
        timer.schedule(timerTask, DEFAULT_TIMES_DELAY, DEFAULT_TIMES_PERIOD);
    }

    private void cancelConnect() throws MqttException {
        if (this.timer == null) {
            return;
        } else {
            this.timer.cancel();
            this.timer.purge();
        }

        this.doDisconnect();
    }

    protected void onConnect(MqttClient mqttClient,Boolean reconnect,String uri) throws MqttException {
        this.mqttClient = mqttClient;
        System.out.println("onConnect topics:" + topics);

        if (onConnectStatusCallback == null) {
            return;
        } else {
            onConnectStatusCallback.onConnectStatusCB(reconnect, uri);
        }
    }

    protected void onReConnect(MqttClient mqttClient,Boolean reconnect,String uri) throws MqttException {
        this.mqttClient = mqttClient;
        if (onConnectStatusCallback == null) {
            return;
        } else {
            onConnectStatusCallback.onConnectStatusCB(true, uri);
        }
    }

    protected void onDisconnect(MqttClient mqttClient, Throwable cause) throws MqttException {
        this.mqttClient = mqttClient;

        try {
            this.setToken(mqttClient.connectWithResult(this.getMqttConnectOptions()));
        } catch (MqttException e) {
            e.printStackTrace();
        }

        if (onConnectStatusCallback == null) {
            return;
        } else {
            onConnectStatusCallback.onConnectStatusCB(false, cause.getLocalizedMessage());
        }
    }

    protected void onMessageArrived(String topic,MqttMessage message) {
        if (messageCallback == null) {
            return;
        } else {
            messageCallback.messageCallback(topic, message.getPayload());
        }
    }
}
