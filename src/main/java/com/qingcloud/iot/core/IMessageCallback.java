package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkMessageData;

public interface IMessageCallback {
    public void messageCallback(String topic, byte[] payload);
}
