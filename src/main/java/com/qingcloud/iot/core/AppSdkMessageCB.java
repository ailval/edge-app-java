package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkMessageData;

public interface AppSdkMessageCB {
    public void appSdkMessageCB(AppSdkMessageData messageData, Object object);
}
