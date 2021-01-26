package local.test;

import com.alibaba.fastjson.JSON;
import com.qingcloud.iot.common.*;
import com.qingcloud.iot.core.*;
import com.qingcloud.iot.core.mqttclient.IoTMqttCallback;
import com.qingcloud.iot.core.mqttclient.IoTMqttClient;
import com.qingcloud.iot.core.topic.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.*;

public class TestAppCoreClient {

    public static void main(String[] args) throws Exception {

        //订阅
        TestAppCoreClient appCoreClient = new TestAppCoreClient();
        appCoreClient.testPubProperties();

        //推送消息
//        testPublishProperty();

        //事件decode
//        testPublishEvent();
    }

    private void testPubProperties() throws Exception {
        AppCoreClient app;
        IoTMqttClient ioTMqttClient;
        String appId;
        String identifier;
        Topic topic;
        String[] topics;

        //构造启动方式
        app = new AppCoreClient(CommonConst.AppSdkRuntimeType.RuntimeType_Docker);

        //app初始化，启动
        app.init();
        app.start();

        appId = app.getCfg().getAppId();
        identifier = app.getCodec().getThingId(); //仅仅是测试，实际为模型的字段identifier

        //初始化完成后，可获取MQTT客户端的APP ID
        ioTMqttClient = app.getIoTMqttClient();
        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        //多个topic，便于统一订阅
        topic = new Topic();
        //设置topic
        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);

        topics = new String[]{topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getSubscribeServiceTopic()};

        int size = topics.length;
        System.out.println("testPubProperties topics size:" + size);
        for (String s : topics) {
            System.out.println("testPubProperties topics:" + s);
        }

        ioTMqttClient.setMessageCallback(new IMessageCallback() {
            @Override
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("testPubProperties messageCallback topic1:" + topic + ", payload:" + new String(payload));
            }
        });

        ioTMqttClient.subscribeMultiple(topics,ioTMqttClient.getMessageCallback());

        ioTMqttClient.setOnConnectedCallback(new IOnConnectedCallback() {
            @Override
            public void onConnectedCallback(Boolean bool,String uri) {
                System.out.println("testPubProperties onConnectedCallback:" + bool + ", URI:" + uri);

                try {
                    subscribeTopics(ioTMqttClient, appId, identifier);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        ioTMqttClient.setOnDisconnectedCallback(new IOnDisconnectedCallback() {
            @Override
            public void onDisconnectedCallback(Boolean bool,Throwable cause) {
                System.out.println("testPubProperties onDisconnectedCallback:" + bool + ", cause:" + cause.getLocalizedMessage());
            }
        });

        ioTMqttClient.publish(topics[0],0,"Hello Mqtt".getBytes());
    }

    private void subscribeTopics(IoTMqttClient ioTMqttClient, String appId, String identifier) throws MqttException {
        //多个topic，便于统一订阅
        Topic topic = new Topic();
        //设置topic
        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);

        //多个topic，便于统一订阅
        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getSubscribeServiceTopic()};

        int size = topics.length;
        System.out.println("testPubProperties topics size:" + size);
        for (int i=0;i<size;++i) {
            System.out.println("testPubProperties topics:" + topics[i]);
        }

        ioTMqttClient.subscribeMultiple(topics,ioTMqttClient.getMessageCallback());
    }

    /**
     * 测试消息属性
     * @throws Exception
     */
    private static void testPublishProperty() throws Exception {
        //构造启动方式
        AppCoreClient app = new AppCoreClient(CommonConst.AppSdkRuntimeType.RuntimeType_Exec);

        //设置启动方式docker（如果构造时已经指定，这里不用设置）
        app.setAppType(CommonConst.AppSdkRuntimeType.RuntimeType_Docker);

        //app初始化，启动
        app.init();
        app.start();

        //获取调试用信息
        System.out.println("app getAppType:" + app.getAppType());
        System.out.println("app getUrl:" + app.getUrl());
        System.out.println("app getCfg:" + app.getCfg());
        System.out.println("app getCodec:" + app.getCodec());
        System.out.println("app getMessageParam:" + app.getMessageParam());
        System.out.println("app getEventParam:" + app.getEventParam());
        System.out.println("app getServiceIds:" + Arrays.toString(app.getServiceIds()));
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
        System.out.println("app getCodec getIdentifier:" + app.getCodec().getThingId());

        //对应的设备ID，模型ID和APP ID
        //ioTMqttClient
        //sourceId,entityId,deviceId=iotd-xxxxx
        //modelId,thingId=iott-xxxxx

        //消息回调
        IMessageCallback messageCallback = new IMessageCallback() {
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("testPublishProperty messageCallback topic:" + topic + ", payload:" + new String(payload));
            }
        };
        app.getIoTMqttClient().setMessageCallback(messageCallback);

        //消息回调
        IEventCallback eventCallback = new IEventCallback() {
            public void eventCallback(AppSdkEventData data, Object obj) {
                System.out.println("testPublishProperty eventCallback topic:" + data.type + ", payload:" + obj.toString());
            }
        };
        app.getIoTMqttClient().setEventCallback(eventCallback);

        String appId = app.getCfg().getAppId();
        String identifier = app.getCodec().getThingId(); //仅仅是测试，实际为模型的字段identifier

        Topic topic = new Topic();
        //设置topic
        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);

        //初始化完成后，可获取MQTT客户端的APP ID
        IoTMqttClient ioTMqttClient = app.getIoTMqttClient();
        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        //建立连接时回调
        IoTMqttClient finalIoTMqttClient = ioTMqttClient;

        //多个topic，便于统一订阅
        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getSubscribeServiceTopic()};

        int i;
        for (i=0; i<topics.length; i++) {
            System.out.println("testPublishProperty topics:" + topics[i]);
        }

        //测试app停止
        app.stop();
        app.cleanup();

        //测试 重新初始化和启动边缘APP
        app.init();
        app.start();

        //获取建立连接的MQTT客户端
        ioTMqttClient = app.getIoTMqttClient();

        //订阅topics（可单独订阅，也可统一订阅）
        ioTMqttClient.subscribeMultiple(topics, messageCallback);

        //推送消息
        ioTMqttClient.publish(topic.getPublishPropertyTopic(),0,"hello msg".getBytes());
        ioTMqttClient.publish(topic.getSubscribePropertyTopic(), 0, "hello msg".getBytes());

        ioTMqttClient.publish(topic.getPublishEventTopic(),0,"hello evt".getBytes());
        ioTMqttClient.publish(topic.getSubscribeEventTopic(), 0, "hello evt".getBytes());

        //设置边缘应用消息的数据
        List list = getListData();

        //内部暂时借助fastjson进行序列化
        String jsonStr = JSON.toJSONString(list);
        List<AppSdkMsgProperty> list1 = JSON.parseArray(jsonStr, AppSdkMsgProperty.class);
        for ( AppSdkMsgProperty msgProperty:list1) {
            System.out.println("identifier:" + msgProperty.getIdentifier());
        }

        AppSdkMessageData msgData = new AppSdkMessageData();
        msgData.setType(CommonConst.AppSdkMessageType.MessageType_Property);
        msgData.setPayload(jsonStr.getBytes());
        System.out.println("jsonStr:" + jsonStr);

        List listData2 = getListData2();
        String jsonStr2 = JSON.toJSONString(listData2);
        AppSdkMessageData msgData2 = new AppSdkMessageData();
        msgData2.setType(CommonConst.AppSdkMessageType.MessageType_Property);
        msgData2.setPayload(jsonStr2.getBytes());

        System.out.println("jsonStr2:" + jsonStr2);
        //发送消息，注意数据格式
        app.sendMessage(msgData);

        int size = topics.length;
        System.out.println("testPubProperties topics size:" + size);
        for (String s : topics) {
            System.out.println("testPubProperties topics:" + s);
        }

        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        ioTMqttClient.setMessageCallback(new IMessageCallback() {
            @Override
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("testPubProperties messageCallback topic2:" + topic + ", payload:" + new String(payload));
            }
        });

        ioTMqttClient.subscribeMultiple(topics,ioTMqttClient.getMessageCallback());

        IoTMqttClient finalIoTMqttClient1 = ioTMqttClient;
        ioTMqttClient.setOnConnectedCallback(new IOnConnectedCallback() {
            @Override
            public void onConnectedCallback(Boolean bool,String uri) {
                System.out.println("testPubProperties onConnectedCallback:" + bool + ", URI:" + uri);

                try {
                    finalIoTMqttClient1.subscribeMultiple(topics,finalIoTMqttClient1.getMessageCallback());

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        ioTMqttClient.setOnDisconnectedCallback(new IOnDisconnectedCallback() {
            @Override
            public void onDisconnectedCallback(Boolean bool,Throwable cause) {
                System.out.println("testPubProperties onDisconnectedCallback:" + bool + ", cause:" + cause.getLocalizedMessage());
            }
        });

    }

    private static List<AppSdkMsgProperty> getListData() {
        AppSdkMsgProperty msgProperty1 = new AppSdkMsgProperty();
        msgProperty1.identifier = "order_no"; //对应物模型的字段标识符
        msgProperty1.timestamp = new Date().getTime();
        msgProperty1.value = "11111";
        AppSdkMsgProperty msgProperty2 = new AppSdkMsgProperty();
        msgProperty2.identifier = "resource_no";
        msgProperty2.timestamp = new Date().getTime();
        msgProperty2.value = "22222";
        AppSdkMsgProperty msgProperty3 = new AppSdkMsgProperty();
        msgProperty3.identifier = "resource_info";
        msgProperty3.timestamp = new Date().getTime();
        msgProperty3.value = "3";

        List<AppSdkMsgProperty> list = new ArrayList<AppSdkMsgProperty>();
        list.add(msgProperty1);
        list.add(msgProperty2);
        list.add(msgProperty3);

        return list;
    }

    private static List<AppSdkMsgProperty> getListData2() {
        AppSdkMsgProperty msgProperty1 = new AppSdkMsgProperty();
        msgProperty1.identifier = "order_no"; //对应物模型的字段标识符
        msgProperty1.timestamp = 1711570L;
        msgProperty1.value = "11111";
        AppSdkMsgProperty msgProperty2 = new AppSdkMsgProperty();
        msgProperty2.identifier = "resource_no";
        msgProperty2.timestamp = 1711570L;
        msgProperty2.value = "22222";
        AppSdkMsgProperty msgProperty3 = new AppSdkMsgProperty();
        msgProperty3.identifier = "resource_info";
        msgProperty3.timestamp = 1711570L;
        msgProperty3.value = "3";

        List<AppSdkMsgProperty> list = new ArrayList<AppSdkMsgProperty>();
        list.add(msgProperty1);
        list.add(msgProperty2);
        list.add(msgProperty3);

        return list;
    }

    /**
     * 测试消息事件
     * @throws Exception
     */
    private static void testPublishEvent() throws Exception {
        Object msgObj = new Object();
        Object evtObj = new Object();

        AppCoreClient app = new AppCoreClient(CommonConst.AppSdkRuntimeType.RuntimeType_Docker, null,msgObj,null
                ,evtObj);

        //docker
        app.setAppType(CommonConst.AppSdkRuntimeType.RuntimeType_Docker);

        //app初始化，启动
        app.init();
        app.start();

        //获取调试用信息
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
        System.out.println("app getCodec getIdentifier:" + app.getCodec().getThingId());

        IoTMqttClient ioTMqttClient = app.getIoTMqttClient();
        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        IMessageCallback messageCallback = new IMessageCallback() {
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("testPublishEvent messageCallback topic:" + topic + ", payload:" + new String(payload));
            }
        };
        app.getIoTMqttClient().setMessageCallback(messageCallback);

        IEventCallback eventCallback = new IEventCallback() {
            public void eventCallback(AppSdkEventData data, Object obj) {
                System.out.println("testPublishEvent eventCallback topic:" + data.type + ", payload:" + obj.toString());
            }
        };
        app.getIoTMqttClient().setEventCallback(eventCallback);

        app.setConnectStatusCB(new OnConnectStatusCB() {
            @Override
            public void onConnectStatusCB(boolean bool,String details) {
                System.out.println("testPublishEvent testPublishEvent onConnectStatusCB :" + bool + "," + details);
            }
        });

        ioTMqttClient = app.getIoTMqttClient();
        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        String appId = app.getCfg().getAppId();
        String deviceId = app.getCodec().getDeviceId();
        String thingId = app.getCfg().getThingId();
        String identifier = app.getCodec().getThingId(); //仅仅是测试，实际为模型的字段identifier

        Topic topic = new Topic();
        ArrayList<MqttMessage> arrayList = new ArrayList<MqttMessage>();

        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
//        topic.equipSubscribeEventTopic(appId,identifier);
        //edge/68352965-2fab-11eb-a5e9-52549e81d51b/thing/event/subOffline/control
        topic.equipSubscribeEventTopic(appId,"subOffline");

        topic.equipPublishServiceTopic(appId,identifier); //仅仅是测试，实际为模型的字段identifier

        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getPublishServiceTopic()};

        ioTMqttClient.setTopics(topics);

        int i;
        for (i=0; i<topics.length; i++) {
            System.out.println("topics:" + topics[i]);
        }

        ioTMqttClient.getMqttClient().setCallback(new IoTMqttCallback(ioTMqttClient));

        ioTMqttClient.setMessageCallback(new IMessageCallback() {
            @Override
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("testPubProperties messageCallback topic2:" + topic + ", payload:" + new String(payload));
                app.onRecvData(topic,payload);
            }
        });

        ioTMqttClient.subscribeMultiple(topics,ioTMqttClient.getMessageCallback());

        IoTMqttClient finalIoTMqttClient1 = ioTMqttClient;
        ioTMqttClient.setOnConnectedCallback(new IOnConnectedCallback() {
            @Override
            public void onConnectedCallback(Boolean bool,String uri) {
                System.out.println("testPubProperties onConnectedCallback:" + bool + ", URI:" + uri);

                try {
                    finalIoTMqttClient1.subscribeMultiple(topics,finalIoTMqttClient1.getMessageCallback());

                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        ioTMqttClient.setOnDisconnectedCallback(new IOnDisconnectedCallback() {
            @Override
            public void onDisconnectedCallback(Boolean bool,Throwable cause) {
                System.out.println("testPubProperties onDisconnectedCallback:" + bool + ", cause:" + cause.getLocalizedMessage());
            }
        });

        ioTMqttClient.publish(topic.getPublishPropertyTopic(),0,"hello".getBytes());
        ioTMqttClient.publish(topic.getPublishEventTopic(),0,"hello".getBytes());

        AppSdkMessageData msgData = new AppSdkMessageData();
        msgData.setType(CommonConst.AppSdkMessageType.MessageType_Event);

        AppSdkMsgEvent msgEvent = new AppSdkMsgEvent();
        msgEvent.identifier = "subOffline";
        Long time = new Date().getTime();
        msgEvent.timestamp = (Long) time;

        HashMap<String, Object> hashMap = new HashMap<String,Object>();
        hashMap.put("k1", "v1");
        hashMap.put("k2", "v2");
        msgEvent.setParams(hashMap);
        System.out.println("msgEvent:" + msgEvent.getIdentifier() + "," + msgEvent.getTimestamp().toString());

        String jsonStr = JSON.toJSONString(msgEvent);
        AppSdkMsgEvent msgEvent1 = JSON.parseObject(jsonStr, AppSdkMsgEvent.class);

        System.out.println("jsonStr:" + jsonStr);
        System.out.println("msgEvent1:" + msgEvent1.getIdentifier() + "," + msgEvent1.getTimestamp().toString());
        msgData.setPayload(jsonStr.getBytes());

        app.sendMessage(msgData);

        //注意数据格式
        ioTMqttClient.publish(topic.getSubscribeEventTopic(), 0, JSON.toJSONString(msgData).getBytes()); //error
        ioTMqttClient.publish(topic.getSubscribePropertyTopic(), 0, JSON.toJSONString(msgData).getBytes()); //error
    }
}
