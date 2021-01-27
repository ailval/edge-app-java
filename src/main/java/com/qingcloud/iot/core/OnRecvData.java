package com.qingcloud.iot.core;

public interface OnRecvData {
    public void onRecvData(String topic, byte[] payload);
}
