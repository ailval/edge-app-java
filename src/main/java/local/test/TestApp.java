package local.test;

import com.alibaba.fastjson.JSON;

import com.qingcloud.iot.common.AppSdkEventData;
import com.qingcloud.iot.common.AppSdkMessageData;
import com.qingcloud.iot.common.AppSdkMsgEvent;
import com.qingcloud.iot.common.AppSdkMsgProperty;
import com.qingcloud.iot.common.CommonConst.*;
import com.qingcloud.iot.core.*;
import com.qingcloud.iot.core.mqttclient.IoTMqttCallback;
import com.qingcloud.iot.core.mqttclient.IoTMqttClient;
import com.qingcloud.iot.core.topic.Topic;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;

public class TestApp {
    static TestApp testApp;
    static AppCoreClient appCli;
    static Timer timer;
    static TimerTask timerTask;
    static IMqttToken token;
    static MqttClient mqttClient;
    static IoTMqttClient ioTMqttClient;
    static MqttConnectOptions mqttConnectOptions;

    static final int DEFAULT_TIMES_DELAY = 1000;
    static final int DEFAULT_TIMES_PERIOD = 60000;

    public static void main(String[] args) throws Exception {
        testApp = new TestApp();
        testApp.run();
    }

    void run() {
        //创建app客户端
        appCli = new AppCoreClient(AppSdkRuntimeType.RuntimeType_Docker);

        appCli.setConnectStatusCB(new OnConnectStatusCB() {
            @Override
            public void onConnectStatusCB(boolean isConnected,String details) {
                System.out.println("appCli onConnectStatusCB isConnected:" + isConnected + ", details:" + details);
            }
        });

        appCli.setOnRecvData(new OnRecvData() {
            @Override
            public void onRecvData(String topic,byte[] payload) {
                System.out.println("appCli onRecvData topic:" + topic + ", payload:" + new String(payload));
            }
        });

        appCli.setEventCB(new AppSdkEventCB() {
            @Override
            public void appSdkEventCB(AppSdkEventData eventData,Object object) {
                System.out.println("appCli appSdkEventCB eventData:" + JSON.toJSONString(eventData)
                        + ", object:" + JSON.toJSONString(object
                ));
            }
        });

        appCli.setMessageCB(new AppSdkMessageCB() {
            @Override
            public void appSdkMessageCB(AppSdkMessageData messageData,Object object) {
                System.out.println("appCli appSdkMessageCB messageData:" + JSON.toJSONString(messageData)
                        + ", object:" + JSON.toJSONString(object));
            }
        });
        appCli.setCoreCallback(new AppCoreCallback(appCli));

        //初始化mqttclient
        appCli.init();
        try {
            //建立mqttclient连接
            appCli.start();
            ioTMqttClient = appCli.getIoTMqttClient();
            mqttClient = ioTMqttClient.getMqttClient();

        } catch (MqttException e) {
            e.printStackTrace();
        }

        //mqttclient(回调)
        mqttClient.setCallback(new IoTMqttCallback(ioTMqttClient));

        ioTMqttClient.setMessageCallback(new IMessageCallback() {
            @Override
            public void messageCallback(String topic,byte[] payload) {
                System.out.println("ioTMqttClient messageCallback topic1:" + topic + ", payload:" + new String(payload));
            }
        });

        //获取主题
        Topic topic = new Topic();
        ArrayList<MqttMessage> arrayList = new ArrayList<MqttMessage>();

        topic.equipPublishPropertyTopic(appCli.getCfg().getAppId());
        topic.equipSubscribePropertyTopic(appCli.getCfg().getAppId());

        topic.equipPublishEventTopic(appCli.getCfg().getAppId(), "data_event"); //数据模型的配置信息
        topic.equipSubscribeEventTopic(appCli.getCfg().getAppId(),"data_event");
        //edge/68352965-2fab-11eb-a5e9-52549e81d51b/thing/event/data_event/control

        topic.equipPublishServiceTopic(appCli.getCfg().getAppId(),"data_event");

        String[] getTopics = {
                topic.getPublishPropertyTopic(),
                topic.getPublishEventTopic(),
                topic.getPublishServiceTopic()};

        for (String topicStr:getTopics) {
            System.out.println("topic:" + topicStr);
        }

        ioTMqttClient.setTopics(getTopics);

        //客户端断开后，重新建立连接的回调
        ioTMqttClient.setOnConnectedCallback(new IOnConnectedCallback() {
            @Override
            public void onConnectedCallback(Boolean bool,String uri) {
                System.out.println("ioTMqttClient onConnectedCallback:" + bool
                        + ", uri:" + uri);
                try {
                    ioTMqttClient.subscribeMultiple(getTopics,new IMessageCallback() {
                        @Override
                        public void messageCallback(String topic,byte[] payload) {
                            System.out.println("ioTMqttClient subscribeMultiple reconnect:" + topic
                            + ", payload:" + new String(payload));
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        //客户端断开连接的回调
        ioTMqttClient.setOnDisconnectedCallback(new IOnDisconnectedCallback() {
            @Override
            public void onDisconnectedCallback(Boolean bool,Throwable cause) {
                System.out.println("ioTMqttClient onDisconnectedCallback:" + bool
                        + ", cause:" + cause.getLocalizedMessage());
            }
        });

        //mqttclient(订阅消息)
        try {
            ioTMqttClient.subscribeMultiple(getTopics,new IMessageCallback() {
                @Override
                public void messageCallback(String topic,byte[] payload) {
                    System.out.println("ioTMqttClient subscribeMultiple first:" + topic
                            + ", payload:" + new String(payload));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        //推送数据
        postData();
    }

    void postData() {
        //获取mqttclient handler
        mqttClient = appCli.getIoTMqttClient().getMqttClient();
        token  = appCli.getIoTMqttClient().getToken();

        timer = new Timer("com.qingcloud.iot");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                AppSdkMessageData messageData;
                try {
                    int ran = (int) (Math.random()*(100-1)+1);
                    messageData = encodeMessageData(ran);
                    appCli.sendMessage(messageData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask, DEFAULT_TIMES_DELAY, DEFAULT_TIMES_PERIOD);
    }

    private AppSdkMessageData encodeMessageData(int value) {
        //设置消息数据
        AppSdkMessageData messageData = new AppSdkMessageData(AppSdkMessageType.MessageType_Property);
        ArrayList<AppSdkMsgProperty> msgProperties = new ArrayList<AppSdkMsgProperty>();
        AppSdkMsgProperty msgProperty = new AppSdkMsgProperty();
        msgProperty.identifier = "random_data";
        msgProperty.timestamp = new Date().getTime();

        msgProperty.value = String.valueOf(value);

        msgProperties.add(msgProperty);
        messageData.setPayload(JSON.toJSONString(msgProperties).getBytes());

        if (value > 50) {
            AppSdkMessageData messageData1 = encodeEventData(value);
            try {
                appCli.sendMessage(messageData1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return messageData;
    }

    private AppSdkMessageData encodeEventData(int value) {
        AppSdkMsgEvent msgEvent = new AppSdkMsgEvent();
        msgEvent.identifier = "data_event";
        msgEvent.timestamp = new Date().getTime();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("data_event",String.valueOf(value));
        msgEvent.setParams(hashMap);

        AppSdkMessageData messageData = new AppSdkMessageData(AppSdkMessageType.MessageType_Event);
        messageData.setPayload(JSON.toJSONString(msgEvent).getBytes());
        return messageData;
    }
}