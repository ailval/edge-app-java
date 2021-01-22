package com.qingcloud.iot.core.config;

import com.qingcloud.iot.common.CommonConst.AppSdkRuntimeType;
import com.qingcloud.iot.core.config.PropertiesCfgUtil;

import static com.qingcloud.iot.common.ConfigConst.*;

public class EdgeConfig {
    public String protocol;
    public String hubAddr;
    public String hubPort;
    public String appId;
    public String deviceId;
    public String thingId;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHubAddr() {
        return hubAddr;
    }

    public void setHubAddr(String hubAddr) {
        this.hubAddr = hubAddr;
    }

    public String getHubPort() {
        return hubPort;
    }

    public void setHubPort(String hubPort) {
        this.hubPort = hubPort;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getThingId() {
        return thingId;
    }

    public void setThingId(String thingId) {
        this.thingId = thingId;
    }

    public void load(AppSdkRuntimeType type) throws Exception {
        switch (type) {
            case RuntimeType_Exec:
                PropertiesCfgUtil cfg = new PropertiesCfgUtil();
                this.protocol = PropertiesCfgUtil.getValue("protocol");
                this.appId = PropertiesCfgUtil.getValue("appId");
                this.hubPort = PropertiesCfgUtil.getValue("hubPort");
                this.hubAddr = PropertiesCfgUtil.getValue("hubAddr");
                this.deviceId = PropertiesCfgUtil.getValue("deviceId");
                this.thingId = PropertiesCfgUtil.getValue("thingId");
                break;

            case RuntimeType_Docker:

                this.protocol = System.getenv(ENV_EDGE_HUB_PROTOCOL);
                if (this.protocol == null || this.protocol.trim().equals("")) {
                    this.setProtocol("tcp");
                }

                this.hubAddr = System.getenv(ENV_EDGE_HUB_HOST);

                this.hubPort = System.getenv(ENV_EDGE_HUB_PORT);
                if (this.hubPort == null || this.hubPort.trim().equals("")) {
                    this.setHubPort("1883");
                } else {
                    String str = this.hubPort.trim();
                    if (!isNumeric(str)) {
                        this.setHubPort("1883");
                    }
                }

                this.appId = System.getenv(ENV_EDGE_APP_ID).trim();
                this.deviceId = System.getenv(ENV_EDGE_DEVICE_ID).trim();
                this.thingId = System.getenv(ENV_EDGE_THING_ID).trim();

                if (this.appId.equals("") || this.deviceId.equals("")  || this.thingId.equals("")) {
                    throw new Exception("Load config failed, config params should not be empty, appId, deviceId or thingId");
                }
                break;

            case RuntimeType_Unknown:
            default:
                throw new Exception(("Application type is not supported, appType: " + type.toString()));
        }
    }

    private static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }
}
