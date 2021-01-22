package com.qingcloud.iot.core.codec;

public class ServiceMetadata {

    public String modelId;
    public String entityId;

    public ServiceMetadata(String modelId, String entityId) {
        this.modelId = modelId;
        this.entityId = entityId;
    }

    public ServiceMetadata() {
        this("","");
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

}
