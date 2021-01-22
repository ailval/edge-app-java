package local.test;

import com.alibaba.fastjson.JSON;
import com.qingcloud.iot.common.*;
import com.qingcloud.iot.core.*;
import com.qingcloud.iot.core.mqttclient.IoTMqttClient;
import com.qingcloud.iot.core.topic.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TestAppCoreClient {

    public static void main(String[] args) throws Exception {
        testPublishProperty();
//        testPublishEvent();
    }

    private static void testPublishProperty() throws Exception {

        AppCoreClient app = new AppCoreClient(CommonConst.AppSdkRuntimeType.RuntimeType_Exec);

        //Exec
//        app.setAppType(CommonConst.AppSdkRuntimeType.RuntimeType_Exec);
        //docker
        app.setAppType(CommonConst.AppSdkRuntimeType.RuntimeType_Docker);

        //app run
        app.init();
        app.start();

        System.out.println("app getAppType:" + app.getAppType());
        System.out.println("app getUrl:" + app.getUrl());
        System.out.println("app getCfg:" + app.getCfg());
        System.out.println("app getCodec:" + app.getCodec());
        System.out.println("app getMessageParam:" + app.getMessageParam());
        System.out.println("app getEventParam:" + app.getEventParam());
        System.out.println("app getServiceIds:" + app.getServiceIds());
        System.out.println("app getIoTMqttClient:" + app.getIoTMqttClient());
        System.out.println("app getOnConnectedCallback:" + app.getConnectStatusCB());
        System.out.println("app getCfg getAppId:" + app.getCfg().getAppId());
        System.out.println("app getCfg getDeviceId:" + app.getCfg().getDeviceId());
        System.out.println("app getHubAddr:" + app.getCfg().getHubAddr());
        System.out.println("app getCfg getHubPort:" + app.getCfg().getHubPort());
        System.out.println("app getCfg getProtocol:" + app.getCfg().getProtocol());
        System.out.println("app getCfg getThingId:" + app.getCfg().getThingId());
        System.out.println("app getCodec getAppId:" + app.getCodec().getAppId());
        System.out.println("app getCodec getDeviceId:" + app.getCodec().getDeviceId());
        System.out.println("app getCodec getThingId:" + app.getCodec().getThingId());

        //ioTMqttClient
        //sourceId,entityId,deviceId=iotd-bd4c7c54-496d-487b-bc1d-66e2574c5153
        //modelId,thingId=iott-8p1EKZQLab

        IMessageCallback messageCallback = new IMessageCallback() {
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("messageCallback topic:" + topic + ", payload:" + new String(payload));
            }
        };
        app.getIoTMqttClient().setMessageCallback(messageCallback);

        IEventCallback eventCallback = new IEventCallback() {
            public void eventCallback(AppSdkEventData data, Object obj) {
                System.out.println("eventCallback topic:" + data.type + ", payload:" + obj.toString());
            }
        };
        app.getIoTMqttClient().setEventCallback(eventCallback);


        app.setConnectStatusCB(new OnConnectStatusCB() {
            @Override
            public void onConnectStatusCB(boolean bool,String details) {
                System.out.println("connectStatusCB status:" + bool + ", details:" + details);
            }
        });

        IoTMqttClient ioTMqttClient = app.getIoTMqttClient();
        System.out.println("-- getIoTMqttClient:" + app.getClientId());
        System.out.println("-- getIoTMqttClient:" + ioTMqttClient.getToken().toString());

        String appId = app.getCfg().getAppId();
        String deviceId = app.getCodec().getDeviceId();
        String thingId = app.getCfg().getThingId();
        String identifier = app.getCodec().getThingId();

        Topic topic = new Topic();
        ArrayList<MqttMessage> arrayList = new ArrayList<MqttMessage>();

        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);
        topic.equipSubscribeServiceTopic(appId,identifier);

        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getSubscribeServiceTopic()};

        int i;
        for (i=0; i<topics.length; i++) {
            System.out.println("topics:" + topics[i]);
        }

        ioTMqttClient.subscribeMultiple(topics, messageCallback);
        ioTMqttClient.setTopics(topics);

        app.stop();
        app.cleanup();

        app.init();
        app.start();

        ioTMqttClient = app.getIoTMqttClient();
        ioTMqttClient.subscribeMultiple(topics, messageCallback);

        ioTMqttClient.publish(topic.getPublishPropertyTopic(),0,"hello msg".getBytes());
        ioTMqttClient.publish(topic.getSubscribePropertyTopic(), 0, "hello msg".getBytes());

        ioTMqttClient.publish(topic.getPublishEventTopic(),0,"hello evt".getBytes());
        ioTMqttClient.publish(topic.getSubscribeEventTopic(), 0, "hello evt".getBytes());

        AppSdkMessageData msgData = new AppSdkMessageData();
        msgData.setType(CommonConst.AppSdkMessageType.MessageType_Property);

        AppSdkMsgProperty msgProperty1 = new AppSdkMsgProperty();
        msgProperty1.identifier = "order_no";
        msgProperty1.timestamp = 1111;
        msgProperty1.value = "11111";
        AppSdkMsgProperty msgProperty2 = new AppSdkMsgProperty();
        msgProperty2.identifier = "resource_no";
        msgProperty2.timestamp = 2222;
        msgProperty2.value = "22222";
        AppSdkMsgProperty msgProperty3 = new AppSdkMsgProperty();
        msgProperty3.identifier = "resource_info";
        msgProperty3.timestamp = 2222;
        msgProperty3.value = "3";

        List<AppSdkMsgProperty> list = new ArrayList<AppSdkMsgProperty>();
        list.add(msgProperty1);
        list.add(msgProperty2);
        list.add(msgProperty3);

        String jsonStr = JSON.toJSONString(list);

        List<AppSdkMsgProperty> list1 = JSON.parseArray(jsonStr, AppSdkMsgProperty.class);

        for ( AppSdkMsgProperty msgProperty:list1) {
            System.out.println("identifier:" + msgProperty.getIdentifier());
        }

        System.out.println("jsonStr:" + jsonStr);
        msgData.setPayload(jsonStr.getBytes());

        app.sendMessage(msgData);
    }

    private static void testPublishEvent() throws Exception {
        Object msgObj = new Object();
        Object evtObj = new Object();

        AppCoreClient app = new AppCoreClient(CommonConst.AppSdkRuntimeType.RuntimeType_Exec, null,msgObj,null
                ,evtObj);

        //docker
        app.setAppType(CommonConst.AppSdkRuntimeType.RuntimeType_Docker);

        IMessageCallback messageCallback = new IMessageCallback() {
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("messageCallback topic:" + topic + ", payload:" + new String(payload));
            }
        };
        app.getIoTMqttClient().setMessageCallback(messageCallback);

        IEventCallback eventCallback = new IEventCallback() {
            public void eventCallback(AppSdkEventData data, Object obj) {
                System.out.println("eventCallback topic:" + data.type + ", payload:" + obj.toString());
            }
        };
        app.getIoTMqttClient().setEventCallback(eventCallback);

        app.setConnectStatusCB(new OnConnectStatusCB() {
            @Override
            public void onConnectStatusCB(boolean bool,String details) {
                System.out.println("onConnectStatusCB :" + bool + "," + details);
            }
        });

        //app run
        app.init();
        app.start();

        //ioTMqttClient
        //sourceId,entityId,deviceId=iotd-bd4c7c54-496d-487b-bc1d-66e2574c5153
        //modelId,thingId=iott-8p1EKZQLab

        IoTMqttClient ioTMqttClient = app.getIoTMqttClient();

        String appId = app.getCfg().getAppId();
        String deviceId = app.getCodec().getDeviceId();
        String thingId = app.getCfg().getThingId();
        String identifier = app.getCodec().getThingId();

        Topic topic = new Topic();
        ArrayList<MqttMessage> arrayList = new ArrayList<MqttMessage>();

        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);
        topic.equipSubscribeServiceTopic(appId,identifier);

        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getSubscribeServiceTopic()};

        ioTMqttClient.setTopics(topics);

        int i;
        for (i=0; i<topics.length; i++) {
            System.out.println("topics:" + topics[i]);
        }

        ioTMqttClient.subscribeMultiple(topics, messageCallback);

        ioTMqttClient.publish(topic.getPublishPropertyTopic(),0,"hello".getBytes());
        ioTMqttClient.publish(topic.getSubscribePropertyTopic(), 0, "hello".getBytes());

        AppSdkMessageData msgData = new AppSdkMessageData();
        msgData.setType(CommonConst.AppSdkMessageType.MessageType_Event);

        AppSdkMsgEvent msgEvent = new AppSdkMsgEvent();
        msgEvent.identifier = "subOffline";
        long time = new Date().getTime();
        msgEvent.timestamp = (int) time;

        HashMap<String, Object> hashMap = new HashMap<String,Object>();
        hashMap.put("k1", "v1");
        hashMap.put("k2", "v2");
        msgEvent.setParams(hashMap);

        String jsonStr = JSON.toJSONString(msgEvent);
        AppSdkMsgEvent msgEvent1 = JSON.parseObject(jsonStr, AppSdkMsgEvent.class);

        System.out.println("jsonStr:" + jsonStr);
        msgData.setPayload(jsonStr.getBytes());

        app.sendMessage(msgData);
    }
}
