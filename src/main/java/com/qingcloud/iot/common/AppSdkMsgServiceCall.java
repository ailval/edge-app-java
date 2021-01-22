package com.qingcloud.iot.common;

import java.util.HashMap;

public class AppSdkMsgServiceCall {
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    public String identifier;
    public HashMap<String, Object> params;
}
