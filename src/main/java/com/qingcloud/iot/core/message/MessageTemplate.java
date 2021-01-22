package com.qingcloud.iot.core.message;

public class MessageTemplate {

    private String messageTemplateProperty = "";
    private String messageTemplateEvent = "";
    private String messageTemplateService = "";

    public String identifier;

    public MessageTemplate(String identifier) {
        messageTemplateProperty = equipMessageTemplateProperty();
        messageTemplateEvent = equipMessageTemplateEvent(identifier);
        messageTemplateService = equipMessageTemplateService(identifier);
    }

    public MessageTemplate() {
        new MessageTemplate("DefaultIdentifier");
    }

    public String getMessageTemplateProperty() {
        return messageTemplateProperty;
    }

    protected void setMessageTemplateProperty(String messageTemplateProperty) {
        messageTemplateProperty = messageTemplateProperty;
    }

    public String getMessageTemplateEvent() {
        return messageTemplateEvent;
    }

    protected void setMessageTemplateEvent(String messageTemplateEvent) {
        messageTemplateEvent = messageTemplateEvent;
    }

    public String getMessageTemplateService() {
        return messageTemplateService;
    }

    protected void setMessageTemplateService(String messageTemplateService) {
        messageTemplateService = messageTemplateService;
    }

    public String equipMessageTemplateProperty() {
        String str = new StringBuilder().
                append("thing").append(".").
                append("property").append(".").
                append("post").toString();
        messageTemplateProperty = str;
        return str;
    }

    public String equipMessageTemplateEvent(String identifier) {
        String str = new StringBuilder().
                append("thing").append(".").
                append("event").append(".").
                append(identifier).append(".").
                append("post").toString();
        messageTemplateEvent = str;
        return str;
    }

    public String equipMessageTemplateService(String identifier) {
        String str = new StringBuilder().
                append("thing").append(".").
                append("service").append(".").
                append(identifier).append(".").
                append("call").toString();
        messageTemplateService = str;
        return messageTemplateService;
    }

}
