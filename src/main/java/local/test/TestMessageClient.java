package local.test;

import com.qingcloud.iot.common.AppSdkEventData;
import com.qingcloud.iot.core.IEventCallback;
import com.qingcloud.iot.core.IMessageCallback;
import com.qingcloud.iot.core.IOnConnectedCallback;
import com.qingcloud.iot.core.OnConnectStatusCB;
import com.qingcloud.iot.core.mqttclient.IoTMqttClient;
import org.eclipse.paho.client.mqttv3.*;
import com.qingcloud.iot.core.topic.Topic;

public class TestMessageClient {

    public static  final String identifier = "68352965-2fab-11eb-a5e9-52549e81d51b";
    public static final String modelId = "iott-8p1EKZQLab";
    public static final String deviceId = "iotd-bd4c7c54-496d-487b-bc1d-66e2574c5153";
    public static final String appId = "68352965-2fab-11eb-a5e9-52549e81d51b";
    public static final String host = "tcp://139.198.178.228:1883";

    private OnConnectStatusCB connectStatusCB;
    private IEventCallback eventCallback;
    private IMessageCallback messageCallback;

    /*
     *
    model_id = "iott-8p1EKZQLab"
    device_id = "iotd-bd4c7c54-496d-487b-bc1d-66e2574c5153"
    host_u = "139.198.178.228"
    app_id = "68352965-2fab-11eb-a5e9-52549e81d51b"
     *
     */

    public static void main(String[] args) throws Exception {
        testMessageClient();
    }

    private static void testMessageClient() throws Exception {

        IMessageCallback messageCallback = new IMessageCallback() {
            @Override
            public void messageCallback(String str,byte[] payload) {
                System.out.println("TestMessageClient IMessageCallback:" + str + "," + payload.toString());
            }
        };

        IEventCallback eventCallback = new IEventCallback() {
            @Override
            public void eventCallback(AppSdkEventData data, Object obj) {
                System.out.println("TestMessageClient IEventCallback:" + data.type.toString() + "," + obj.toString());
            }
        };

        IoTMqttClient ioTMqttClient = new IoTMqttClient(appId,host);
        ioTMqttClient.setMessageCallback(messageCallback);
        ioTMqttClient.setEventCallback(eventCallback);

        MqttMessage mqttMessage = new MqttMessage();

        ioTMqttClient.start();
        ioTMqttClient.stop();
        ioTMqttClient.doDisconnect();
        ioTMqttClient.doConnect();
        ioTMqttClient.doDisconnect();
        ioTMqttClient.doConnect();

        Topic topic = new Topic();
        topic.equipPublishPropertyTopic(appId);
        topic.equipSubscribePropertyTopic(appId);

        topic.equipPublishEventTopic(appId,identifier);
        topic.equipSubscribeEventTopic(appId,identifier);

        topic.equipPublishServiceTopic(appId,identifier);

        String[] topics = {topic.getSubscribePropertyTopic(),topic.getSubscribeEventTopic(),topic.getPublishServiceTopic()};
        System.out.println("topics:" + topics.toString());

        ioTMqttClient.subscribeMultiple(topics,messageCallback);

    }
}