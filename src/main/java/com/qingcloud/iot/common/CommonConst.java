package com.qingcloud.iot.common;


public class CommonConst {
    public enum AppSdkRuntimeType {
        RuntimeType_Unknown,
        RuntimeType_Docker,
        RuntimeType_Exec
    }

    public enum AppSdkEventType {
        EventType_Unknown,
        EventType_Connected,
        EventType_Disconnected
    }

    public enum AppSdkMessageType {
        MessageType_Property,
        MessageType_Event,
        MessageType_ServiceCall,
        MessageType_Unknown
    }

    public enum AppSdkOptionsType {
        OptionsType_RuntimeType,
        OptionsType_MessageCallback,
        OptionsType_MessageParam,
        OptionsType_EventCallback,
        OptionsType_EventParam,
        OptionsType_ServiceIds
    }
}

