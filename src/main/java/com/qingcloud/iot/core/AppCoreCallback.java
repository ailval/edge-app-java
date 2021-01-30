package com.qingcloud.iot.core;

import com.qingcloud.iot.common.AppSdkEventData;
import com.qingcloud.iot.common.AppSdkMessageData;

public class AppCoreCallback implements AppSdkEventCB, AppSdkMessageCB {
    private AppCoreClient appCoreClient;

    public AppCoreClient getAppCoreClient() {
        return appCoreClient;
    }

    public AppCoreCallback(AppCoreClient appCoreClient) {
        this.appCoreClient = appCoreClient;
    }

    @Override
    public void appSdkEventCB(AppSdkEventData eventData,Object object) {
        if (this.appCoreClient.getEventCB() != null) {
            this.appCoreClient.getEventCB().appSdkEventCB(eventData, object);
        }
    }

    @Override
    public void appSdkMessageCB(AppSdkMessageData messageData,Object object) {
        if (this.appCoreClient.getMessageCB() != null) {
            this.appCoreClient.getMessageCB().appSdkMessageCB(messageData, object);
        }
    }

}
