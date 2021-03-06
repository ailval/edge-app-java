package com.qingcloud.iot.core.codec;

import java.util.ArrayList;

public class ModelMetadata {

    public String modelId;
    public String entityId;
    public ArrayList<String> sourceId;
    public String epochTime;

    public ModelMetadata(String modelId,String entityId,ArrayList<String> sourceId,String epochTime) {
        this.modelId = modelId;
        this.entityId = entityId;
        this.sourceId = sourceId;
        this.epochTime = epochTime;
    }

    public ModelMetadata() {
        this("","", new ArrayList<String>(),"");
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

    public ArrayList<String> getSourceId() {
        return sourceId;
    }

    public void setSourceId(ArrayList sourceId) {
        this.sourceId = sourceId;
    }

    public String getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(String epochTime) {
        this.epochTime = epochTime;
    }

}
