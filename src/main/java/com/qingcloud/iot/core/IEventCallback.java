package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkEventData;

public interface IEventCallback {
    public void eventCallback(AppSdkEventData appSdkEventData, Object obj);
}
