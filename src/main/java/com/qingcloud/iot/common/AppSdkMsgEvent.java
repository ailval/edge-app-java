package com.qingcloud.iot.common;

import java.util.HashMap;

public class AppSdkMsgEvent {

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public String identifier;
    public Long timestamp;
    public HashMap<String,Object> params;
}
