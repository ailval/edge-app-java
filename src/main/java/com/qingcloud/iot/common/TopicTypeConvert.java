package com.qingcloud.iot.common;

public class TopicTypeConvert {
    public enum TopicType {
        TopicType_SubscribeProperty,
        TopicType_PublishProperty,
        TopicType_SubscribeEvent,
        TopicType_PublishEvent,
        TopicType_PublishService,
        TopicType_SubscribeService,
        TopicType_Unknown
    }

    public static final String TopicType_SubscribeProperty = "TopicType_SubProperty";
    public static final String TopicType_PublishProperty = "TopicType_PubProperty";
    public static final String TopicType_SubscribeEvent = "TopicType_SubEvent";
    public static final String TopicType_PublishEvent = "TopicType_PubEvent";
    public static final String TopicType_PublishService = "TopicType_PubService";
    public static final String TopicType_SubscribeService = "TopicType_SubService";

    public String getTopicType_SubscribeProperty() {
        return TopicType_SubscribeProperty;
    }

    public String getTopicType_PublishProperty() {
        return TopicType_PublishProperty;
    }
    public String getTopicType_SubscribeEvent() {
        return TopicType_SubscribeEvent;
    }

    public String getTopicType_PublishEvent() {
        return TopicType_PublishEvent;
    }

    public String getTopicType_PublishService() {
        return TopicType_PublishService;
    }

    public String getTopicType_SubscribeService() {
        return TopicType_SubscribeService;
    }

    public TopicType topicTypeByTopicTypeString(String topicTypeStr) {
        switch (topicTypeStr) {
            case TopicType_PublishProperty:
                return TopicType.TopicType_PublishProperty;
            case TopicType_SubscribeProperty:
                return TopicType.TopicType_SubscribeProperty;
            case TopicType_PublishEvent:
                return TopicType.TopicType_PublishEvent;
            case TopicType_SubscribeEvent:
                return TopicType.TopicType_SubscribeEvent;
            case TopicType_PublishService:
                return TopicType.TopicType_PublishService;
            case TopicType_SubscribeService:
                return TopicType.TopicType_SubscribeService;
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
                return TopicType_PublishProperty;
            case TopicType_SubscribeProperty:
                return TopicType_SubscribeProperty;
            case TopicType_PublishEvent:
                return TopicType_PublishEvent;
            case TopicType_SubscribeEvent:
                return TopicType_SubscribeEvent;
            case TopicType_PublishService:
                return TopicType_PublishService;
            case TopicType_SubscribeService:
                return TopicType_SubscribeService;
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
