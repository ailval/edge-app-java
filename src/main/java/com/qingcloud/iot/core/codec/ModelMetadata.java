package com.qingcloud.iot.core.codec;

import java.util.ArrayList;

public class ModelMetadata {

    public String modelId;
    public String entityId;
    public ArrayList<String> sourceId;
    public String epochTime;

    public ModelMetadata(String modelId, String entityId, ArrayList<String> source, String epochTime) {
        this.modelId = modelId;
        this.entityId = entityId;
        this.sourceId = source;
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

    public ArrayList<String> getSource() {
        return sourceId;
    }

    public void setSource(ArrayList source) {
        this.sourceId = source;
    }

    public String getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(String epochTime) {
        this.epochTime = epochTime;
    }

}
