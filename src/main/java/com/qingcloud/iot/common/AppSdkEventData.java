package com.qingcloud.iot.common;

import com.qingcloud.iot.common.CommonConst.AppSdkEventType;

public class AppSdkEventData {

    public AppSdkEventType type;
    public Object payload;

    public AppSdkEventData(AppSdkEventType type,Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public AppSdkEventData() {
        this(null, null);
    }

    public AppSdkEventData(AppSdkEventType type) {
        this(type, null);
    }

    public AppSdkEventType getType() {
        return type;
    }

    public void setType(AppSdkEventType type) {
        this.type = type;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
