package com.qingcloud.iot.common;

public class TopicTypeConvert {
    public enum TopicType {
        TopicType_SubscribeProperty,
        TopicType_PublishProperty,
        TopicType_SubscribeEvent,
        TopicType_PublishEvent,
        TopicType_PublishService,
        TopicType_SubscribeService,
        TopicType_PublishServiceReply,
        TopicType_Unknown
    }

    public static final String TOPIC_TYPE_SUB_PROPERTY = "TopicType_SubProperty";
    public static final String TOPIC_TYPE_PUB_PROPERTY = "TopicType_PubProperty";
    public static final String TOPIC_TYPE_SUB_EVENT = "TopicType_SubEvent";
    public static final String TOPIC_TYPE_PUB_EVENT = "TopicType_PubEvent";
    public static final String TOPIC_TYPE_PUB_SERVICE = "TopicType_PubService";
    public static final String TOPIC_TYPE_SUB_SERVICE = "TopicType_SubService";
    public static final String TOPIC_TYPE_PUB_SERVICE_REPLY = "TopicType_PubServiceReply";

    public String getTopicType_SubscribeProperty() {
        return TOPIC_TYPE_SUB_PROPERTY;
    }

    public String getTopicType_PublishProperty() {
        return TOPIC_TYPE_PUB_PROPERTY;
    }
    public String getTopicType_SubscribeEvent() {
        return TOPIC_TYPE_SUB_EVENT;
    }

    public String getTopicType_PublishEvent() {
        return TOPIC_TYPE_PUB_EVENT;
    }

    public String getTopicType_PublishService() {
        return TOPIC_TYPE_PUB_SERVICE;
    }
    public String getTopicType_SubscribeService() {
        return TOPIC_TYPE_SUB_SERVICE;
    }
    public String getTopicType_PublishServiceReply() {
        return TOPIC_TYPE_PUB_SERVICE_REPLY;
    }


    public TopicType topicTypeByTopicTypeString(String topicTypeStr) {
        switch (topicTypeStr) {
            case TOPIC_TYPE_PUB_PROPERTY:
                return TopicType.TopicType_PublishProperty;
            case TOPIC_TYPE_SUB_PROPERTY:
                return TopicType.TopicType_SubscribeProperty;
            case TOPIC_TYPE_PUB_EVENT:
                return TopicType.TopicType_PublishEvent;
            case TOPIC_TYPE_SUB_EVENT:
                return TopicType.TopicType_SubscribeEvent;
            case TOPIC_TYPE_PUB_SERVICE:
                return TopicType.TopicType_PublishService;
            case TOPIC_TYPE_SUB_SERVICE:
                return TopicType.TopicType_SubscribeService;
            case TOPIC_TYPE_PUB_SERVICE_REPLY:
                return TopicType.TopicType_PublishServiceReply;
            default:
                try {
                    throw new Exception("Unknown TopicType");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return TopicType.TopicType_Unknown;
    }

    public String topicByEnumTopicType(TopicType topicType) {
        switch (topicType) {
            case TopicType_PublishProperty:
                return TOPIC_TYPE_PUB_PROPERTY;
            case TopicType_SubscribeProperty:
                return TOPIC_TYPE_SUB_PROPERTY;
            case TopicType_PublishEvent:
                return TOPIC_TYPE_PUB_EVENT;
            case TopicType_SubscribeEvent:
                return TOPIC_TYPE_SUB_EVENT;
            case TopicType_PublishService:
                return TOPIC_TYPE_PUB_SERVICE;
            case TopicType_SubscribeService:
                return TOPIC_TYPE_SUB_SERVICE;
            case TopicType_PublishServiceReply:
                return TOPIC_TYPE_PUB_SERVICE_REPLY;
            case TopicType_Unknown:
                return null;
            default:
                try {
                    throw new Exception("Unknown TopicType");
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
