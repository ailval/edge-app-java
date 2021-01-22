package com.qingcloud.iot.core.mqttclient;

import com.qingcloud.iot.core.IEventCallback;
import com.qingcloud.iot.core.IMessageCallback;
import com.qingcloud.iot.core.IOnConnectedCallback;
import com.qingcloud.iot.core.OnConnectStatusCB;
import org.eclipse.paho.client.mqttv3.*;
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
    private IMessageCallback messageCallback;
    private IEventCallback eventCallback;
    private OnConnectStatusCB connectStatusCB;

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
        return connectStatusCB;
    }

    public void setConnectStatusCB(OnConnectStatusCB connectStatusCB) {
        this.connectStatusCB = connectStatusCB;
    }

    public IMessageCallback getMessageCallback() {
        return messageCallback;
    }

    public void setMessageCallback(IMessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public IoTMqttClient(String clientId,String url,OnConnectStatusCB connectStatusCB,IMessageCallback messageCallback,IEventCallback eventCallback) throws MqttException {
        this.connectStatusCB = connectStatusCB;
        this.messageCallback = messageCallback;
        this.eventCallback = eventCallback;
        this.mqttClient = new MqttClient(url, clientId);
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{url});
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setKeepAliveInterval(DEFAULT_KEEP_ALIVE_DEFAULT);
        mqttConnectOptions.setConnectionTimeout(DEFAULT_WAIT_TIMEOUT);
        this.mqttConnectOptions = mqttConnectOptions;
        this.setClientCallback(this);
    }

    private void setClientCallback(IoTMqttClient client) {
        if (client != null && (client instanceof IoTMqttClient)) {
            client.mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect,String serverURI) {
                    System.out.println("IoTMqttClient connectComplete reconnect:" + reconnect + ", serverURI:" + serverURI);

                    //重新订阅主题，如果 cleanSession 设置为 false ,则可以不用重新订阅
//                    if (!reconnect){
//                        try {
//                            mqttClient.subscribe(client.topics);
//                        } catch (MqttException e) {
//                            e.printStackTrace();
//                        }
//                    }

//                    if (reconnect) {
//                        try {
//                            String[] topics = client.getTopics();
//                            System.out.println("IoTMqttClient connectComplete topics:" + topics);
//                            int[] qos = new int[topics.length];
//                            for (int i=0;i<topics.length;++i) {
//                                qos[i] = 0;
//                            }
//                            client.getMqttClient().subscribe(getTopics(),qos);
//                        } catch (MqttException e) {
//                            e.printStackTrace();
//                        }
//                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("IoTMqttClient connectionLost cause:" + cause.toString());
                    while(true) {
                        try {
                            Thread.sleep(1000);
                            if(null != client && !client.mqttClient.isConnected()) {
                                // 重新连接
                                client.mqttClient.connectWithResult(getMqttConnectOptions());
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

                @Override
                public void messageArrived(String topic,MqttMessage message) throws Exception {
                    System.out.println("IoTMqttClient messageArrived topic:" + topic + ", message:" + message.getPayload().toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("IoTMqttClient deliveryComplete token:" + token);
                }
            });
        }
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
        ArrayList<IMqttMessageListener> arrayList = new ArrayList<>();

        for (int i=0; i<topics.length; i++) {
            if (topics[i] != null && !topics[i].equals("")) {
                arrayList.add(new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic,MqttMessage message) throws Exception {
                        messageCallback.messageCallback(topic, message.getPayload());
                    }
                });
            }
        }

        int size = arrayList.size();
        IMqttMessageListener[] messageListeners = arrayList.toArray(new IMqttMessageListener[size]);

        this.mqttClient.subscribe(topics, messageListeners);
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
                        System.out.println("tryConnect new token:" + token + ", isConnected:" + mqttClient.isConnected());
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        timerTask = task;
        timer.schedule(timerTask, DEFAULT_TIMES_DELAY);
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

    private void onConnect(MqttClient mqttClient) {
        if (connectStatusCB == null) {
            return;
        } else {
            connectStatusCB.onConnectStatusCB(true, null);
        }
    }

    private void onDisconnect(MqttClient mqttClient, Throwable cause) {
        if (connectStatusCB == null) {
            return;
        } else {
            connectStatusCB.onConnectStatusCB(false, cause.getLocalizedMessage());
        }
    }

}
