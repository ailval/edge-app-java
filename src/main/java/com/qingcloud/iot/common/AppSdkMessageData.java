package com.qingcloud.iot.common;

import com.qingcloud.iot.common.CommonConst.AppSdkMessageType;

public class AppSdkMessageData {
    public AppSdkMessageType type;
    public byte[] payload;

    public AppSdkMessageData(AppSdkMessageType type,byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    public AppSdkMessageData(AppSdkMessageType type) {
        this(type, null);
    }

    public AppSdkMessageData() {
        this(null, null);
    }

    public AppSdkMessageType getType() {
        return type;
    }

    public void setType(AppSdkMessageType type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
