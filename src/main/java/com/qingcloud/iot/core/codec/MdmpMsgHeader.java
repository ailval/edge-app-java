package com.qingcloud.iot.core.codec;

import com.qingcloud.iot.core.codec.ModelMetadata;

public class MdmpMsgHeader {
    public String id;
    public String version;
    public String type;
    public ModelMetadata metadata;

    public MdmpMsgHeader(String id,String version,String type,ModelMetadata modelMetadata) {
        this.id = id;
        this.version = version;
        this.type = type;
        this.metadata = modelMetadata;
    }

    public MdmpMsgHeader() {
        this("","","",new ModelMetadata());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ModelMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ModelMetadata metadata) {
        this.metadata = metadata;
    }
}
