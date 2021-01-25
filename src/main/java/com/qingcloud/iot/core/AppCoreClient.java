package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkEventData;
import com.qingcloud.iot.common.AppSdkMessageData;
import com.qingcloud.iot.common.CommonConst.*;
import com.qingcloud.iot.common.TopicTypeConvert;
import com.qingcloud.iot.common.TopicTypeConvert.TopicType;
import com.qingcloud.iot.core.codec.*;
import com.qingcloud.iot.core.config.EdgeConfig;
import com.qingcloud.iot.core.message.AppSdkMessage;
import com.qingcloud.iot.core.mqttclient.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;

public class AppCoreClient implements ICore, OnConnectStatusCB {

    private AppSdkRuntimeType appType;
    private Object messageParam;
    private AppSdkMessageCB messageCB;
    private AppSdkEventCB eventCB;

    private OnConnectStatusCB connectStatusCallback;
    private Object eventParam;
    private IoTMqttClient ioTMqttClient;
    private Codec codec;
    private EdgeConfig cfg;

    private String[] serviceIds;
    private String clientId;
    private String url;

    public IoTMqttClient getIoTMqttClient() {
        return ioTMqttClient;
    }

    public Codec getCodec() {
        return codec;
    }

    public EdgeConfig getCfg() {
        return cfg;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUrl() {
        return url;
    }

    public OnConnectStatusCB getConnectStatusCB() {
        return connectStatusCallback;
    }

    public void setConnectStatusCB(OnConnectStatusCB connectStatusCB) {
        this.connectStatusCallback = connectStatusCB;
    }

    public AppCoreClient(AppSdkRuntimeType type,AppSdkMessageCB messageCB,Object messageParam,AppSdkEventCB eventCB,Object eventParam) {
        this.appType = type;
        this.messageCB = messageCB;
        this.messageParam = messageParam;
        this.eventCB = eventCB;
        this.eventParam = eventParam;
        this.clientId = "";
        this.url = "";
    }

    public AppCoreClient() {
        this(null, null, new Object(), null, new Object());
    }

    public AppCoreClient(AppSdkRuntimeType type) {
        this(type, null, new Object(), null, new Object());
    }

    @Override
    public void init() {
        this.cfg = new EdgeConfig();
        try {
            this.cfg.load(appType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.codec = new Codec(cfg.appId, cfg.deviceId, cfg.thingId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cfg.deviceId);
        stringBuilder.append("/");
        stringBuilder.append(cfg.appId);
        this.clientId = stringBuilder.toString();

        stringBuilder = new StringBuilder();
        stringBuilder.append(cfg.protocol);
        stringBuilder.append("://");
        stringBuilder.append(cfg.hubAddr);
        stringBuilder.append(":");
        stringBuilder.append(cfg.hubPort);
        this.url = stringBuilder.toString();

        try {
            this.ioTMqttClient = new IoTMqttClient(clientId,url,this::onConnectStatusCB);
        } catch (MqttException e) {
            this.codec = null;
            this.cfg = null;
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() throws MqttException {
        if (ioTMqttClient != null) {
            ioTMqttClient.stop();
            ioTMqttClient = null;
        } else {
            return;
        }

        if (codec != null) {
            codec = null;
        }

        if (cfg != null) {
            cfg = null;
        }

    }

    @Override
    public void start() throws MqttException {
        if (ioTMqttClient == null || codec == null || cfg == null) {
            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ioTMqttClient.start();
    }

    @Override
    public void stop() throws MqttException {
        ioTMqttClient.stop();
    }

    @Override
    public void onConnectStatusCB(boolean bool, String details) {
        if (bool == true) {
            System.out.println("APP SDK onConnectStatus called, status is connected");

            //Connected
            if (this.ioTMqttClient == null || this.codec == null || this.cfg == null) {
                System.out.println("APP SDK onConnected subscribe topics failed, err: not init");
                return;
            }

            //Callback connected event
            if (this.eventCB != null) {
                AppSdkEventData appSdkEventData = new AppSdkEventData(AppSdkEventType.EventType_Connected);
                this.eventCB.appSdkEventCB(appSdkEventData, eventParam);
            }

            ArrayList<String> arrayList = new ArrayList<>();
            AppSdkMessage appSdkMessage = new AppSdkMessage();
            appSdkMessage = codec.encodeTopic(TopicTypeConvert.TopicType_SubscribeProperty, "+");
            if (appSdkMessage == null || appSdkMessage.error != null) {
                System.out.println("APP SDK onConnected EncodeTopic failed, topicType:" + TopicTypeConvert.TopicType_SubscribeProperty + "error:" + appSdkMessage.error);
            } else {
                arrayList.add(appSdkMessage.topic);
            }

            appSdkMessage = codec.encodeTopic(TopicTypeConvert.TopicType_SubscribeEvent, "+");
            if (appSdkMessage == null || appSdkMessage.error != null) {
                System.out.println("APP SDK onConnected EncodeTopic failed, topicType:" + TopicTypeConvert.TopicType_SubscribeEvent + "error:" + appSdkMessage.error);
            } else {
                arrayList.add(appSdkMessage.topic);
            }

            int size = arrayList.size();
            String[] array = arrayList.toArray(new String[size]);

            System.out.println("topics size:" + array.length);
            for (int i=0;i<size;++i) {
                System.out.println("topics:" + array[i]);
            }

            try {
                ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

                System.out.println("APP SDK onConnected subscribe topics success!");
                ioTMqttClient.subscribeMultiple(array, this::onRecvData);

            } catch (MqttException e) {
                System.out.println("APP SDK onConnected subscribe topics failed, err: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            if (this.eventCB != null) {
                AppSdkEventData appSdkEventData = new AppSdkEventData(AppSdkEventType.EventType_Connected);
                this.eventCB.appSdkEventCB(appSdkEventData, eventParam);
            }
        }
    }

    public Error sendMessage(AppSdkMessageData data) throws Exception {
        if (this.ioTMqttClient == null || this.codec == null || this.cfg == null) {
            return new Error("APP SDK send message failed, err: not init");
        }

        if (data == null) {
            return new Error("APP SDK send message failed, err: invalid arguments");
        }

        TopicType topicType = TopicType.TopicType_Unknown;
        String topicTypeStr = "";
        TopicTypeConvert topicTypeConvert = new TopicTypeConvert();

        switch (data.type) {
            case MessageType_Property:
                topicTypeStr = TopicTypeConvert.TopicType_PublishProperty;
                topicType = TopicType.TopicType_PublishProperty;
                break;
            case MessageType_Event:
                topicTypeStr = TopicTypeConvert.TopicType_PublishEvent;
                topicType = TopicType.TopicType_PublishEvent;
                break;
            case MessageType_ServiceCall:
                topicTypeStr = TopicTypeConvert.TopicType_PublishService;
                topicType = TopicType.TopicType_PublishService;
                break;
            default:
                return new Error("APP SDK send message failed, err: unsupported message type");
        }

        String publishTopic = "";
        byte[] publishData = null;

        if (data.type == AppSdkMessageType.MessageType_Property
                || data.type == AppSdkMessageType.MessageType_Event
                || data.type == AppSdkMessageType.MessageType_ServiceCall) {

            AppSdkMessage appSdkMessage = new AppSdkMessage();
            appSdkMessage = codec.encodeMessage(topicTypeStr,data.payload);

            if (appSdkMessage == null || appSdkMessage.error != null) {
                System.out.println("encodeMessage Error:" + appSdkMessage.error.toString());
            }

            publishTopic = appSdkMessage.topic;
            publishData = appSdkMessage.payload;
        }

        System.out.println("sendMessage publishTopic:" + publishTopic);
        System.out.println("sendMessage publishData:" + new String(publishData));
        ioTMqttClient.publish(publishTopic, 0, publishData);
        return null;
    }

    private void onRecvData(String topic, byte[] payload) {
        if (this.ioTMqttClient == null || this.codec == null || this.cfg == null) {
            System.out.println("APP SDK onRecvData failed, err: not init");
            return;
        }

        AppSdkMessage appSdkMessage = codec.decodeMessage(topic, payload);
        if (appSdkMessage == null || appSdkMessage.error != null) {
            System.out.println("APP SDK onRecvData failed, err: not init" + appSdkMessage.error);
            return;
        }

        AppSdkMessageType messageType;
        TopicType topicType = appSdkMessage.topicType;
        String topicTypeStr = appSdkMessage.topicTypeString;

        switch (topicType) {
            case TopicType_SubscribeProperty:
            case TopicType_PublishProperty:
                messageType = AppSdkMessageType.MessageType_Property;
                break;
            case TopicType_SubscribeEvent:
            case TopicType_PublishEvent:
                messageType = AppSdkMessageType.MessageType_Event;
                break;
            case TopicType_SubscribeService:
                messageType = AppSdkMessageType.MessageType_ServiceCall;
                break;
            default:
                messageType = AppSdkMessageType.MessageType_Unknown;
                break;
        }

        AppSdkMessageData appSdkMessageData = new AppSdkMessageData(messageType, appSdkMessage.payload);
        if (messageCB == null) return;

        messageCB.appSdkMessageCB(appSdkMessageData, appSdkMessage.getPayload());
    }

    public AppSdkRuntimeType getAppType() {
        return appType;
    }

    public void setAppType(AppSdkRuntimeType appType) {
        this.appType = appType;
    }

    public Object getMessageParam() {
        return messageParam;
    }

    public void setMessageParam(Object messageParam) {
        this.messageParam = messageParam;
    }

    public Object getEventParam() {
        return eventParam;
    }

    public void setEventParam(Object eventParam) {
        this.eventParam = eventParam;
    }

    public String[] getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(String[] serviceIds) {
        this.serviceIds = serviceIds;
    }

}
