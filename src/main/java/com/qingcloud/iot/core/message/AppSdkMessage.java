package com.qingcloud.iot.core.message;

import com.qingcloud.iot.common.TopicTypeConvert;

public class AppSdkMessage {

    public String topic;
    public TopicTypeConvert.TopicType topicType;
    public String topicTypeString; //应该从TopicTypeConvert方法获取

    public String appId;
    public String identifier;
    public byte[] payload;

    public Error error;

    public AppSdkMessage() {
        topic = "";
        topicType = TopicTypeConvert.TopicType.TopicType_Unknown;
        topicTypeString = "";
        appId = "";
        identifier = "";
        error = null;
    }

    public String getTopicTypeString() {
        return topicTypeString;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public TopicTypeConvert.TopicType getTopicType() {
        return topicType;
    }

    public void setTopicType(TopicTypeConvert.TopicType topicType) {
        this.topicType = topicType;
    }

    public String getTopicTypeDesc() {
        return topicTypeString;
    }

    public void setTopicTypeString(String topicTypeStr) {
        this.topicTypeString = topicTypeStr;
    }

}
