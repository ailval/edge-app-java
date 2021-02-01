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

public class AppCoreClient implements ICore, OnRecvData {

    private AppCoreCallback coreCallback;

    private AppSdkRuntimeType appType;
    private Object messageParam;

    private AppSdkMessageCB messageCB;
    private AppSdkEventCB eventCB;

    private Object eventParam;
    private IoTMqttClient ioTMqttClient;
    private Codec codec;
    private EdgeConfig cfg;

    private String[] serviceIds;
    private String clientId;
    private String url;

    //api sdk内使用
    protected String appId;
    protected String identifier;
    protected String deviceId;

    public AppCoreCallback getCoreCallback() {
        return coreCallback;
    }

    public void setCoreCallback(AppCoreCallback coreCallback) {
        this.coreCallback = coreCallback;
    }

    public void setMessageCB(AppSdkMessageCB messageCB) {
        this.messageCB = messageCB;
    }

    public void setEventCB(AppSdkEventCB eventCB) {
        this.eventCB = eventCB;
    }

    protected AppSdkMessageCB getMessageCB() {
        return messageCB;
    }

    protected AppSdkEventCB getEventCB() {
        return eventCB;
    }

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

    public AppCoreClient(AppSdkRuntimeType type,AppSdkMessageCB messageCB,AppSdkEventCB eventCB,String[] serviceIds) {
        this.appType = type;
        this.messageCB = messageCB;
        this.messageParam = new Object();
        this.eventCB = eventCB;
        this.eventParam = new Object();
        this.clientId = "";
        this.url = "";
        this.serviceIds = serviceIds;
    }

    public AppCoreClient(AppSdkRuntimeType type,AppSdkMessageCB messageCB,AppSdkEventCB eventCB) {
        this(type,messageCB,eventCB,null);
    }

    public AppCoreClient(AppSdkRuntimeType type) {
        this(type, null, null, null);
    }

    public AppCoreClient() {
        this(AppSdkRuntimeType.RuntimeType_Docker, null, null, null);
    }

    @Override
    public void init() {
        this.setCoreCallback(new AppCoreCallback(this));

        this.cfg = new EdgeConfig();
        try {
            this.cfg.load(appType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.codec = new Codec(cfg.appId, cfg.deviceId, cfg.thingId);
        this.appId = this.codec.getAppId();
        this.deviceId = this.codec.getDeviceId();

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
            this.ioTMqttClient.setAppCoreClient(this);
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
                topicTypeStr = TopicTypeConvert.TOPIC_TYPE_PUB_PROPERTY;
                topicType = TopicType.TopicType_PublishProperty;
                break;
            case MessageType_Event:
                topicTypeStr = TopicTypeConvert.TOPIC_TYPE_PUB_EVENT;
                topicType = TopicType.TopicType_PublishEvent;
                break;
            case MessageType_ServiceCall:
                topicTypeStr = TopicTypeConvert.TOPIC_TYPE_PUB_SERVICE;
                topicType = TopicType.TopicType_PublishService;
                break;
            case MessageType_ServiceReply:
                topicTypeStr = TopicTypeConvert.TOPIC_TYPE_PUB_SERVICE_REPLY;
                topicType = TopicType.TopicType_PublishServiceReply;
            default:
                return new Error("APP SDK send message failed, err: unsupported message type");
        }

        String publishTopic = "";
        byte[] publishData = null;

        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage = codec.encodeMessage(topicTypeStr,data.payload);

        if (appSdkMessage == null || appSdkMessage.error != null) {
            System.out.println("encodeMessage Error:" + appSdkMessage.error.toString());
        }

        publishTopic = appSdkMessage.topic;
        publishData = appSdkMessage.payload;

        ioTMqttClient.publish(publishTopic, 0, publishData);
        return null;
    }

    public void onConnectStatusCB(boolean bool, String details) {
        if (bool == true) {
            System.out.println("APP SDK onConnectStatus called, status:" + true + details);

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

            if (this.identifier == null || this.identifier.equals("")) {
                System.out.println("APP SDK onConnected topics init failed, err: not init");
            }

            appSdkMessage = codec.encodeTopic(TopicTypeConvert.TOPIC_TYPE_SUB_PROPERTY, this.identifier);
            if (appSdkMessage == null || appSdkMessage.error != null) {
                System.out.println("APP SDK onConnected EncodeTopic failed, topicType:" + TopicTypeConvert.TOPIC_TYPE_SUB_PROPERTY + "error:" + appSdkMessage.error);
            } else {
                if (appSdkMessage.topic != null && !appSdkMessage.topic.equals("")) {
                    arrayList.add(appSdkMessage.topic);
                    System.out.println("TopicType_SubscribeProperty topic:" + appSdkMessage.topic);
                }
            }

            appSdkMessage = codec.encodeTopic(TopicTypeConvert.TOPIC_TYPE_SUB_EVENT, this.identifier);
            if (appSdkMessage == null || appSdkMessage.error != null) {
                System.out.println("APP SDK onConnected EncodeTopic failed, topicType:" + TopicTypeConvert.TOPIC_TYPE_SUB_EVENT + "error:" + appSdkMessage.error);
            } else {
                if (appSdkMessage.topic != null && !appSdkMessage.topic.equals("")) {
                    arrayList.add(appSdkMessage.topic);
                    System.out.println("TopicType_SubscribeEvent topic:" + appSdkMessage.topic);
                }
            }

            for (String srvId : serviceIds) {
                appSdkMessage = codec.encodeTopic(TopicTypeConvert.TOPIC_TYPE_SUB_SERVICE, srvId);

                if (appSdkMessage == null || appSdkMessage.error != null) {
                    System.out.println("APP SDK onConnected EncodeTopic failed, topicType:" + TopicTypeConvert.TOPIC_TYPE_SUB_SERVICE + "error:" + appSdkMessage.error);
                } else {
                    if (appSdkMessage.topic != null && !appSdkMessage.topic.equals("")) {
                        arrayList.add(appSdkMessage.topic);
                        System.out.println("TopicType_SubscribeEvent topic:" + appSdkMessage.topic);
                    }
                }
            }

            //SubscribeMultiple
            int size = arrayList.size();
            String[] topics = arrayList.toArray(new String[size]);

            for (int i=0;i<topics.length;++i) {
                System.out.println("onConnectStatusCB topic:" + topics[i]);
            }
            ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));
            try {
                ioTMqttClient.subscribeMultiple(topics,this::onRecvData);
            } catch (MqttException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("APP SDK onConnectStatus called, status:" + false + details);
            if (this.eventCB != null) {
                AppSdkEventData appSdkEventData = new AppSdkEventData(AppSdkEventType.EventType_Disconnected);
                this.eventCB.appSdkEventCB(appSdkEventData, eventParam);
            }
        }
    }

    @Override
    public void onRecvData(String topic, byte[] payload) {
        if (this.ioTMqttClient == null || this.codec == null || this.cfg == null) {
            System.out.println("APP SDK onRecvData failed, err: not init");
            return;
        }

        AppSdkMessage appSdkMessage = codec.decodeMessage(topic, payload);
        appSdkMessage.topic = topic;

        if (appSdkMessage.error != null) {
            System.out.println("APP SDK onRecvData failed, err: not init" + appSdkMessage.error);
            return;
        }

        AppSdkMessageType messageType;
        TopicType topicType = appSdkMessage.topicType;
        String topicTypeStr = appSdkMessage.topicTypeString;

        switch (topicType) {
            case TopicType_SubscribeProperty:
//            case TopicType_PublishProperty:
                messageType = AppSdkMessageType.MessageType_Property;
                break;
            case TopicType_SubscribeEvent:
//            case TopicType_PublishEvent:
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
