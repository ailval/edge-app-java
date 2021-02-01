package com.qingcloud.iot.core.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingcloud.iot.common.*;
import com.qingcloud.iot.core.message.AppSdkMessage;
import com.qingcloud.iot.core.message.MessageTemplate;
import com.qingcloud.iot.common.AppSdkMsgProperty;
import com.qingcloud.iot.core.topic.Topic;
import com.qingcloud.iot.common.TopicTypeConvert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import static com.qingcloud.iot.common.MessageConst.DefaultMessageVersion;

public class Codec {
    /**
     * Model:object >>>>> encode == json.Marshal(golang) >>>>> []byte 序列化
     * []byte >>>>> decode == json.Unmarshal(golang) >>>>> Model:object 反序列化
     *
     * public static final String toJSONString(Object object);
     * public static final T parseObject(String text, Class clazz);
     *
     * List<T> temp= JSON.parseObject(fastjson,T.class);
     * List<T> tempArray = JSONArray.parseArray(fastjson,T.class);
     */

    private String appId;
    private String deviceId;
    private String thingId;

    public Codec(String appId, String deviceId, String thingId) {
        this.appId = appId;
        this.deviceId = deviceId;
        this.thingId = thingId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public AppSdkMessage encodeMessage(String topicTypeStr, byte[] payload) {
        TopicTypeConvert t = new TopicTypeConvert();
        TopicTypeConvert.TopicType topicType = t.topicTypeByTopicTypeString(topicTypeStr);
        Topic topic = new Topic(appId);

        AppSdkMessage appSdkMessage = new AppSdkMessage();
        AppSdkMessage encodeTopicMsg = new AppSdkMessage();

        switch (topicType) {
            case TopicType_SubscribeProperty:
                appSdkMessage = encodePropertyMsg(payload);
                if (appSdkMessage == null) return null;

                appSdkMessage.topic = topic.getSubscribePropertyTopic();
                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                break;

            case TopicType_PublishProperty:
                appSdkMessage = encodePropertyMsg(payload);
                if (appSdkMessage == null) return null;

                appSdkMessage.topic = topic.getPublishPropertyTopic();
                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                break;

            case TopicType_SubscribeEvent:
            case TopicType_PublishEvent:
                appSdkMessage = encodeEventMsg(payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                encodeTopicMsg = encodeTopic(topicTypeStr, appSdkMessage.identifier);
                if (encodeTopicMsg == null || encodeTopicMsg.error != null) return null;

                appSdkMessage.topic = encodeTopicMsg.topic;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.topicType = topicType;
                break;

            case TopicType_PublishService:
            case TopicType_SubscribeService:
                appSdkMessage = encodeServiceMsg(payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                encodeTopicMsg = encodeTopic(topicTypeStr, appSdkMessage.identifier);
                if (encodeTopicMsg == null || encodeTopicMsg.error != null) return null;
                appSdkMessage.topic = encodeTopicMsg.topic;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.topicType = topicType;
                break;

            case TopicType_PublishServiceReply:
                appSdkMessage = encodeServiceReplyMsg(payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                encodeTopicMsg = encodeTopic(topicTypeStr, appSdkMessage.identifier);
                if (encodeTopicMsg == null || encodeTopicMsg.error != null) return null;

                appSdkMessage.topic = encodeTopicMsg.topic;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.topicType = topicType;
                break;

            default:
                return null;
        }

        return appSdkMessage;
    }

    public AppSdkMessage decodeMessage(String topic, byte[] payload) {
        AppSdkMessage message = decodeTopic(topic);
        AppSdkMessage appSdkMessage = new AppSdkMessage();

        if (appSdkMessage == null || appSdkMessage.error != null) return null;

        TopicType topicType = message.topicType;
        String topicTypeStr = message.topicTypeString;
        String identifier = message.identifier;

        switch (topicType) {
            case TopicType_SubscribeProperty:
            case TopicType_PublishProperty:
                appSdkMessage = decodePropertyMsg(payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.identifier = identifier;
                break;
            case TopicType_SubscribeEvent:
            case TopicType_PublishEvent:
                appSdkMessage = decodeEventMsg(identifier, payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.identifier = identifier;
                break;
            case TopicType_PublishService:
            case TopicType_SubscribeService:
                appSdkMessage = decodeServiceMsg(identifier, payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.identifier = identifier;
                break;
            case TopicType_PublishServiceReply:
                appSdkMessage = decodeServiceReplyMsg(identifier, payload);
                if (appSdkMessage == null || appSdkMessage.error != null) return null;

                appSdkMessage.topicType = topicType;
                appSdkMessage.topicTypeString = topicTypeStr;
                appSdkMessage.identifier = identifier;
                break;
            case TopicType_Unknown:
            default:
                appSdkMessage.topicType = TopicType.TopicType_Unknown;
                appSdkMessage.topicTypeString = "";
                appSdkMessage.payload = null;
                appSdkMessage.error = new Error("Unsupported topic type: " + topicType + "topicTypeString:" + topicTypeStr);
                appSdkMessage.setOriginalTopic(topic);
                break;
        }
        return appSdkMessage;
    }

    //return message: topic, error
    public AppSdkMessage encodeTopic(String topicTypeString, String identifier) {
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        TopicTypeConvert topicTypeConvert = new TopicTypeConvert();

        if ( topicTypeString == null || topicTypeString.equals("")) {
            appSdkMessage.topicTypeString = "";
            appSdkMessage.error = new Error("invalid arguments");
            return appSdkMessage;
        }

        if ((topicTypeString.equals(TopicTypeConvert.TOPIC_TYPE_PUB_EVENT)
            || topicTypeString.equals(TopicTypeConvert.TOPIC_TYPE_SUB_EVENT)
            || topicTypeString.equals(TopicTypeConvert.TOPIC_TYPE_PUB_SERVICE)) &&
            ( identifier == null || identifier.equals(""))) {
            appSdkMessage.topicTypeString = "";
            appSdkMessage.error = new Error("invalid identifier arguments");
            return appSdkMessage;
        }

        TopicType topicType = topicTypeConvert.topicTypeByTopicTypeString(topicTypeString);
        appSdkMessage.topicType = topicType;
        appSdkMessage.topicTypeString = topicTypeString;

        if (appId == null || appId.equals("")) {
            appSdkMessage.error = new Error("invalid appId:" + appId + ", TopicType:" + topicTypeString);
            return appSdkMessage;
        }

        Topic topic = new Topic();
        switch (topicType) {
            case TopicType_PublishProperty:
                topic = new Topic(appId);
                appSdkMessage.topic = topic.getPublishPropertyTopic();
                break;
            case TopicType_SubscribeProperty:
                topic = new Topic(appId);
                appSdkMessage.topic = topic.getSubscribePropertyTopic();
                break;
            case TopicType_PublishEvent:
                if (identifier == null || identifier.equals("")) {
                    appSdkMessage.error = new Error("invalid identifier:" + identifier + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }
                topic = new Topic(appId, identifier);
                appSdkMessage.topic = topic.getPublishEventTopic();
                break;
            case TopicType_SubscribeEvent:
                if (identifier == null || identifier.equals("")) {
                    appSdkMessage.error = new Error("invalid identifier:" + identifier + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }

                topic = new Topic(appId, identifier);
                appSdkMessage.topic = topic.getSubscribeEventTopic();
                break;
            case TopicType_PublishService:
                if (identifier == null || identifier.equals("")) {
                    appSdkMessage.error = new Error("invalid identifier:" + identifier + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }

                topic = new Topic(appId, identifier);
                appSdkMessage.topic = topic.getPublishServiceTopic();
                break;
            case TopicType_SubscribeService:
                if (thingId == null || thingId.equals("")) {
                    appSdkMessage.error = new Error("invalid thingId:" + thingId + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }
                if (deviceId == null || deviceId.equals("")) {
                    appSdkMessage.error = new Error("invalid deviceId:" + deviceId + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }
                if (identifier == null || identifier.equals("")) {
                    appSdkMessage.error = new Error("invalid identifier:" + identifier + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }

                topic = new Topic(thingId,deviceId,identifier);
                appSdkMessage.topic = topic.getSubscribeServiceTopic();
                break;
            case TopicType_PublishServiceReply:
                if (thingId == null || thingId.equals("")) {
                    appSdkMessage.error = new Error("invalid thingId:" + thingId + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }
                if (deviceId == null || deviceId.equals("")) {
                    appSdkMessage.error = new Error("invalid deviceId:" + deviceId + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }
                if (identifier == null || identifier.equals("")) {
                    appSdkMessage.error = new Error("invalid identifier:" + identifier + ", TopicType:" + topicTypeString);
                    return appSdkMessage;
                }

                topic = new Topic(thingId,deviceId,identifier);
                appSdkMessage.topic = topic.getPublishServiceReplyTopic();
                break;
            default:
                appSdkMessage.error = new Error("unsupported topicType: " + topicTypeString);
                return appSdkMessage;
        }
        return appSdkMessage;
    }

    //return message: topicType, topicTypeString, appId, identifier, error
    public AppSdkMessage decodeTopic(String topic) {
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.setOriginalTopic(topic);
        appSdkMessage.topic = topic;

        if (topic == null || topic.equals("")) {
            appSdkMessage.error = new Error("invalid arguments");
            appSdkMessage.topicType = TopicType.TopicType_Unknown;
            appSdkMessage.topic = "";
            return appSdkMessage;
        }

        //parse
        int size = topic.split("/").length;
        String[] units = topic.split("/");
        if (size != 7 && size != 8) {
            appSdkMessage.error = new Error("invalid topic format");
            appSdkMessage.topicType = TopicType.TopicType_Unknown;
            appSdkMessage.topic = "";
            return appSdkMessage;
        } else if (!units[0].equals("") ||
                    (!units[1].equals("edge") && !units[1].equals("sys"))) {
            appSdkMessage.error = new Error("invalid topic format:" + topic);
            appSdkMessage.topicType = TopicType.TopicType_Unknown;
            appSdkMessage.topic = "";
            return appSdkMessage;
        }

        //parse topics
        appSdkMessage.topicTypeString = "";
        appSdkMessage.appId = units[2];
        appSdkMessage.identifier = "";

        if (units[1].equals("edge")) {
            switch (units[4]) {
                case "property":
                    if (units[6].equals("post")) {
                        appSdkMessage.topicType = TopicType.TopicType_SubscribeProperty;
                    } else if (units[6].equals("control")) {
                        appSdkMessage.topicType = TopicType.TopicType_PublishProperty;
                    }
                    break;
                case "event":
                    if (units[6].equals("post")) {
                        appSdkMessage.topicType = TopicType.TopicType_SubscribeEvent;
                    } else if (units[6].equals("control")) {
                        appSdkMessage.topicType = TopicType.TopicType_PublishEvent;
                    }

                    appSdkMessage.identifier = units[5];
                    break;
                case "service":
                    if (units[6].equals("call")) {
                        appSdkMessage.topicType = TopicType.TopicType_PublishService;
                    }
                    appSdkMessage.identifier = units[5];
                    break;
                default:
                    break;
            }
        } else {
            if (units[5].equals("service")) {
                if (units[7].equals("call")) {
                    appSdkMessage.topicType = TopicType.TopicType_SubscribeService;
                } else if (units[7].equals("call_reply")) {
                    appSdkMessage.topicType = TopicType.TopicType_PublishServiceReply;
                }
                appSdkMessage.identifier = units[6];
            }
        }

        if (appSdkMessage.topicType == TopicType.TopicType_Unknown) {
            appSdkMessage.error = new Error("invalid topic format:" + topic);
            appSdkMessage.topicType = TopicType.TopicType_Unknown;
            appSdkMessage.topic = "";
            return appSdkMessage;
        }

        if ((appSdkMessage.topicType == TopicType.TopicType_SubscribeEvent
                || appSdkMessage.topicType == TopicType.TopicType_PublishEvent
                || appSdkMessage.topicType == TopicType.TopicType_PublishService)
            && appSdkMessage.identifier.equals("")) {
            appSdkMessage.error = new Error("invalid identifier format:" + topic);
            appSdkMessage.topic = "";
            return appSdkMessage;
        }

        TopicTypeConvert t = new TopicTypeConvert();
        appSdkMessage.topicTypeString = t.topicByEnumTopicType(appSdkMessage.topicType);

        return appSdkMessage;
    }

    public AppSdkMessage encodePropertyMsg(byte[] payload) {
        String payloadStr = new String(payload);
        List<AppSdkMsgProperty> arrayList = JSON.parseArray(payloadStr, AppSdkMsgProperty.class);
        if (arrayList == null || arrayList.size() == 0) return null;

        long now = new Date().getTime()/1000000;
        MdmpPropertyMsg mdmpPropertyMsg = new MdmpPropertyMsg();
        mdmpPropertyMsg.mdmpMsgHeader = new MdmpMsgHeader();
        mdmpPropertyMsg.mdmpMsgHeader.setId(UUID.randomUUID().toString());
        mdmpPropertyMsg.mdmpMsgHeader.setVersion(DefaultMessageVersion);

        MessageTemplate template = new MessageTemplate();
        template.equipMessageTemplateProperty();
        mdmpPropertyMsg.mdmpMsgHeader.setType(template.getMessageTemplateProperty());
        mdmpPropertyMsg.mdmpMsgHeader.metadata = new ModelMetadata();
        mdmpPropertyMsg.mdmpMsgHeader.metadata.modelId = thingId;
        mdmpPropertyMsg.mdmpMsgHeader.metadata.entityId = deviceId;
        mdmpPropertyMsg.mdmpMsgHeader.metadata.sourceId = new ArrayList<>();
        mdmpPropertyMsg.mdmpMsgHeader.metadata.epochTime = Long.valueOf(now).toString();

        mdmpPropertyMsg.setParams(new HashMap<>());
        for (AppSdkMsgProperty appSdkMsgProperty : arrayList) {
            ModelPropertyData modelPropertyData = new ModelPropertyData();
            modelPropertyData.value = appSdkMsgProperty.getValue();
            modelPropertyData.time = appSdkMsgProperty.getTimestamp();
            mdmpPropertyMsg.getParams().put(appSdkMsgProperty.getIdentifier(),modelPropertyData);
        }

        String msgStr = JSON.toJSONString(mdmpPropertyMsg);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.payload = msgStr.getBytes();
        return appSdkMessage;
    }

    public AppSdkMessage encodeEventMsg(byte[] payload) {
        AppSdkMsgEvent appSdkMsgEvent = JSONObject.parseObject(new String(payload), AppSdkMsgEvent.class);
        if (appSdkMsgEvent == null) return null;

        long now = new Date().getTime()/1000000;
        MdmpEventMsg mdmpEventMsg = new MdmpEventMsg();
        mdmpEventMsg.mdmpMsgHeader = new MdmpMsgHeader();
        mdmpEventMsg.mdmpMsgHeader.setId(UUID.randomUUID().toString());
        mdmpEventMsg.mdmpMsgHeader.setVersion(DefaultMessageVersion);

        MessageTemplate template = new MessageTemplate(appSdkMsgEvent.identifier);
        mdmpEventMsg.mdmpMsgHeader.setType(template.getMessageTemplateEvent());
        mdmpEventMsg.mdmpMsgHeader.metadata.modelId = thingId;
        mdmpEventMsg.mdmpMsgHeader.metadata.entityId = deviceId;
        mdmpEventMsg.mdmpMsgHeader.metadata.sourceId = new ArrayList<>();
        mdmpEventMsg.mdmpMsgHeader.metadata.epochTime = Long.valueOf(now).toString();

        ModelEventData modelEventData = new ModelEventData();
        modelEventData.value = appSdkMsgEvent.params;
        modelEventData.time = appSdkMsgEvent.timestamp;
        mdmpEventMsg.params = modelEventData;

        String msgStr = JSON.toJSONString(mdmpEventMsg);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.identifier = appSdkMsgEvent.identifier;
        appSdkMessage.payload = msgStr.getBytes();

        return appSdkMessage;
    }

    public AppSdkMessage encodeServiceMsg(byte[] payload) {
        AppSdkMsgServiceCall appSdkMsgServiceCall = JSONObject.parseObject(new String(payload), AppSdkMsgServiceCall.class);
        if (appSdkMsgServiceCall == null) return null;

        MdmpServiceCallMsg mdmpServiceCallMsg = new MdmpServiceCallMsg();
        mdmpServiceCallMsg.mdmpMsgHeader = new MdmpMsgHeader();
        mdmpServiceCallMsg.mdmpMsgHeader.setId(appSdkMsgServiceCall.messageId);
        mdmpServiceCallMsg.mdmpMsgHeader.setVersion(DefaultMessageVersion);
        MessageTemplate template = new MessageTemplate(appSdkMsgServiceCall.identifier);
        mdmpServiceCallMsg.mdmpMsgHeader.setType(template.getMessageTemplateService());
        mdmpServiceCallMsg.mdmpMsgHeader.metadata.modelId = thingId;
        mdmpServiceCallMsg.mdmpMsgHeader.metadata.entityId = deviceId;
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.modelId = thingId;
        serviceMetadata.entityId = deviceId;
        mdmpServiceCallMsg.params = appSdkMsgServiceCall.params;

        String jsonMsg = JSON.toJSONString(mdmpServiceCallMsg);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.identifier = appSdkMsgServiceCall.identifier;
        appSdkMessage.payload = jsonMsg.getBytes();

        return appSdkMessage;
    }

    public AppSdkMessage encodeServiceReplyMsg(byte[] payload) {
        AppSdkMsgServiceReply appSdkMsgServiceReply = JSONObject.parseObject(new String(payload), AppSdkMsgServiceReply.class);
        if (appSdkMsgServiceReply == null) return null;

        MdmpServiceReplyMsg mdmpServiceReplyMsg = new MdmpServiceReplyMsg();
        mdmpServiceReplyMsg.mdmpMsgReplyHeader = new MdmpMsgReplyHeader();
        mdmpServiceReplyMsg.mdmpMsgReplyHeader.id = appSdkMsgServiceReply.messageId;
        mdmpServiceReplyMsg.mdmpMsgReplyHeader.version = DefaultMessageVersion;
        mdmpServiceReplyMsg.mdmpMsgReplyHeader.code = appSdkMsgServiceReply.code;
        mdmpServiceReplyMsg.data = appSdkMsgServiceReply.params;

        String jsonMsg = JSON.toJSONString(mdmpServiceReplyMsg);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.identifier = appSdkMsgServiceReply.identifier;
        appSdkMessage.payload = jsonMsg.getBytes();

        return appSdkMessage;
    }

    public AppSdkMessage decodePropertyMsg(byte[] payload) {
        MdmpPropertyMsg mdmpPropertyMsg = JSONObject.parseObject(new String(payload), MdmpPropertyMsg.class);
        if (mdmpPropertyMsg == null) return null;

        ArrayList<AppSdkMsgProperty> arrayList = new ArrayList<>();
        Map<String,ModelPropertyData> map = mdmpPropertyMsg.getParams();
        for (Map.Entry<String,ModelPropertyData> entry : map.entrySet()) {
            AppSdkMsgProperty appSdkMsgProperty = new AppSdkMsgProperty();
            appSdkMsgProperty.identifier = entry.getKey();
            appSdkMsgProperty.value = entry.getValue().value;
            appSdkMsgProperty.timestamp = entry.getValue().time;
            arrayList.add(appSdkMsgProperty);
        }

        String json = JSON.toJSONString(arrayList.toArray());
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.payload = json.getBytes();

        return appSdkMessage;
    }

    public AppSdkMessage decodeEventMsg(String identifier, byte[] payload) {
        MdmpEventMsg mdmpEventMsg = JSONObject.parseObject(new String(payload), MdmpEventMsg.class);
        if (mdmpEventMsg == null) return null;

        AppSdkMsgEvent appSdkMsgEvent = new AppSdkMsgEvent();
        appSdkMsgEvent.identifier = identifier;
        appSdkMsgEvent.timestamp = mdmpEventMsg.params.time;
        appSdkMsgEvent.params = mdmpEventMsg.params.value;

        String json = JSON.toJSONString(appSdkMsgEvent);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.payload = json.getBytes();
        appSdkMessage.identifier = identifier;

        return appSdkMessage;
    }

    public AppSdkMessage decodeServiceMsg(String identifier, byte[] payload) {
        MdmpServiceCallMsg mdmpServiceCallMsg = JSONObject.parseObject(new String(payload), MdmpServiceCallMsg.class);
        if (mdmpServiceCallMsg == null) return null;

        AppSdkMsgServiceCall appSdkMsgServiceCall = new AppSdkMsgServiceCall();
        appSdkMsgServiceCall.messageId = mdmpServiceCallMsg.mdmpMsgHeader.id;
        appSdkMsgServiceCall.identifier = identifier;
        appSdkMsgServiceCall.params = mdmpServiceCallMsg.params;

        String json = JSON.toJSONString(appSdkMsgServiceCall);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.payload = json.getBytes();
        appSdkMessage.identifier = identifier;

        return appSdkMessage;
    }

    public AppSdkMessage decodeServiceReplyMsg(String identifier, byte[] payload) {
        MdmpServiceReplyMsg mdmpServiceReplyMsg = JSONObject.parseObject(new String(payload), MdmpServiceReplyMsg.class);
        if (mdmpServiceReplyMsg == null) return null;

        AppSdkMsgServiceCall appSdkMsgServiceCall = new AppSdkMsgServiceCall();
        appSdkMsgServiceCall.messageId = mdmpServiceReplyMsg.mdmpMsgReplyHeader.id;
        appSdkMsgServiceCall.identifier = identifier;
        appSdkMsgServiceCall.params = mdmpServiceReplyMsg.data;

        String json = JSON.toJSONString(appSdkMsgServiceCall);
        AppSdkMessage appSdkMessage = new AppSdkMessage();
        appSdkMessage.payload = json.getBytes();
        appSdkMessage.identifier = identifier;

        return appSdkMessage;
    }

    private static byte[] objectToByte(Object obj) {
        byte[] bytes = null;
        try {
            // object to byteArray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("exception on translated message:" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    private static Object byteToObject(byte[] bytes) {
        Object obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("exception translated message" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

}
