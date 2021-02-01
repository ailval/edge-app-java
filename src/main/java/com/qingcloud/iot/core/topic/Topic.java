package com.qingcloud.iot.core.topic;

public class Topic {

    private String publishPropertyTopic;
    private String publishEventTopic;
    private String publishServiceTopic;
    private String publishServiceReplyTopic;
    private String subscribePropertyTopic;
    private String subscribeEventTopic;
    private String subscribeServiceTopic;

//    private String subscribeServiceReplyTopic;

    private final String appId;
    private final String identifier;
    private final String modelId;
    private final String entityId;

    public String getAppId() {
        return appId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getModelId() {
        return modelId;
    }

    public String getEntityId() {
        return entityId;
    }

    public Topic() {
        this("","","","");
    }

    public Topic(String appId) {
        this(appId,
                "",
                "",
                "");
    }

    public Topic(String appId, String identifier) {
        this(appId, identifier, "", "");
    }

    public Topic(String modelId, String entityId, String identifier) {
        this("", identifier, modelId, entityId);
    }

    public Topic(String appId, String identifier, String modelId, String entityId) {
        this.publishPropertyTopic = "";
        this.subscribeEventTopic = "";
        this.publishServiceTopic = "";
        this.subscribePropertyTopic = "";
        this.subscribeEventTopic = "";

        this.subscribeServiceTopic = "";
//        this.subscribeServiceReplyTopic = "";
        this.publishServiceReplyTopic = "";
        this.appId = appId;
        this.identifier = identifier;
        this.modelId = modelId;
        this.entityId = entityId;

        if (appId != null && !appId.equals("")) {
            buildPublishPropertyTopic(appId);
            buildSubscribePropertyTopic(appId);
        }

        if (appId != null && !appId.equals("") &&
                identifier != null && !identifier.equals("")) {
            buildPublishEventTopic(appId, identifier);
            buildSubscribeEventTopic(appId, identifier);

            buildPublishServiceTopic(appId, identifier);
        }

        if (modelId != null && !modelId.equals("") &&
                entityId != null && !entityId.equals("") &&
                identifier != null && !identifier.equals("")) {

            buildSubscribeServiceTopic(modelId, entityId, identifier);
            buildPublishServiceReplyTopic(modelId, entityId, identifier);
//            equipSubscribeServiceReplyTopic(modelId, entityId, identifier);
        }
    }

    public String getPublishPropertyTopic() {
        return publishPropertyTopic;
    }

    public String getSubscribePropertyTopic() {
        return subscribePropertyTopic;
    }

    public String getPublishEventTopic() {
        return publishEventTopic;
    }

    public String getSubscribeEventTopic() {
        return subscribeEventTopic;
    }

    public String getPublishServiceTopic() {
        return publishServiceTopic;
    }

    public String getSubscribeServiceTopic() {
        return subscribeServiceTopic;
    }

    public String getPublishServiceReplyTopic() {
        return publishServiceReplyTopic;
    }

//    public String getSubscribeServiceReplyTopic() {
//        return subscribeServiceReplyTopic;
//    }

    protected void setPublishPropertyTopic(String publishPropertyTopic) {
        this.publishPropertyTopic = publishPropertyTopic;
    }

    protected void setSubscribePropertyTopic(String subscribePropertyTopic) {
        this.subscribePropertyTopic = subscribePropertyTopic;
    }

    protected void setPublishEventTopic(String publishEventTopic) {
        this.publishEventTopic = publishEventTopic;
    }

    protected void setSubscribeEventTopic(String subscribeEventTopic) {
        this.subscribeEventTopic = subscribeEventTopic;
    }

    protected void setPublishServiceTopic(String publishServiceTopic) {
        this.publishServiceTopic = publishServiceTopic;
    }

    protected void setSubscribeServiceTopic(String subscribeServiceTopic) {
        this.subscribeServiceTopic = subscribeServiceTopic;
    }

    protected void setPublishServiceReplyTopic(String publishServiceReplyTopic) {
        this.publishServiceReplyTopic = publishServiceReplyTopic;
    }

//    protected void setSubscribeServiceReplyTopic(String subscribeServiceReplyTopic) {
//        this.subscribeServiceReplyTopic = subscribeServiceReplyTopic;
//    }

    //ioTMqttClient
    //sourceId,entityId,deviceId=iotd-xxx...
    //modelId,thingId=iott-yyy...

    public void buildPublishPropertyTopic(String appId) {
        //"/edge/%s/thing/event/property/base/control" % self.app_id
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder().append("/edge/").append(appId).append("/thing/property/base/control");
        String str = stringBuilder.toString();
        this.publishPropertyTopic = str;
    }

    public void buildSubscribePropertyTopic(String appId) {
        //"/edge/%s/thing/event/property/base/control" % self.app_id
        String str = new StringBuilder().append("/edge/").append(appId).append("/thing/property/base/post").toString();
        this.subscribePropertyTopic = str;
    }

    public void buildPublishEventTopic(String appId,String identifier) {
        String str = new StringBuilder().append("/edge/").append(appId).append("/thing/event/").append(identifier).append("/control").toString();
        this.publishEventTopic = str;
    }

    public void buildSubscribeEventTopic(String appId,String identifier) {
        String str = new StringBuilder().append("/edge/").append(appId).append("/thing/event/").append(identifier).append("/post").toString();
        this.subscribeEventTopic = str;
    }

    public void buildPublishServiceTopic(String appId,String identifier) {
        String str = new StringBuilder().append("/edge/").append(appId).append("/thing/service/").append(identifier).append("/call").toString();
        this.publishServiceTopic = str;
    }

    public void buildSubscribeServiceTopic(String modelId,String entityId,String identifier) {
        String str = new StringBuilder().append("/sys/").append(modelId).append("/").append(entityId).append("/thing/service/").append(identifier).append("/call").toString();
        this.subscribeServiceTopic = str;
    }

    public void buildPublishServiceReplyTopic(String modelId,String entityId,String identifier) {
        String str = new StringBuilder().append("/sys/").append(modelId).append("/").append(entityId).append("/thing/service/").append(identifier).append("/call_reply").toString();
        this.publishServiceReplyTopic = str;
    }

//    public void buildSubscribeServiceReplyTopic(String modelId, String entityId, String identifier) {
//        String str = new StringBuilder().append("/sys/").append(modelId).append("/").append(entityId).append("/thing/service/").append(identifier).append("/call_reply").toString();
//        this.subscribeServiceReplyTopic = str;
//    }
}
