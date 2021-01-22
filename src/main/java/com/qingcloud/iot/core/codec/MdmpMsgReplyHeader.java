package com.qingcloud.iot.core.codec;

public class MdmpMsgReplyHeader {

    public String id;
    public String version;
    public Integer code;

    public MdmpMsgReplyHeader(String id,String version,Integer code) {
        this.id = id;
        this.version = version;
        this.code = code;
    }

    public MdmpMsgReplyHeader() {
        this("","",Integer.valueOf(0));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
