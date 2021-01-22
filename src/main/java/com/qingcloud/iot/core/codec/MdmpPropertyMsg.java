package com.qingcloud.iot.core.codec;

import java.util.HashMap;
import java.util.Objects;

public class MdmpPropertyMsg {

    public MdmpMsgHeader mdmpMsgHeader;
    private HashMap<String, ModelPropertyData> params;

    public MdmpPropertyMsg(MdmpMsgHeader mdmpMsgHeader,HashMap<String, ModelPropertyData> params) {
        this.mdmpMsgHeader = mdmpMsgHeader;
        this.setParams(params);
    }

    public MdmpPropertyMsg() {
        this.mdmpMsgHeader = new MdmpMsgHeader();
        this.setParams(new HashMap<String,ModelPropertyData>());
    }

    public MdmpMsgHeader getMdmpMsgHeader() {
        return mdmpMsgHeader;
    }

    public void setMdmpMsgHeader(MdmpMsgHeader mdmpMsgHeader) {
        this.mdmpMsgHeader = mdmpMsgHeader;
    }

    public HashMap<String, ModelPropertyData> getParams() {
        return params;
    }

    public void setParams(HashMap<String, ModelPropertyData> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MdmpPropertyMsg)) return false;
        MdmpPropertyMsg that = (MdmpPropertyMsg) o;
        return Objects.equals(mdmpMsgHeader, that.mdmpMsgHeader) &&
                Objects.equals(getParams(),that.getParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdmpMsgHeader,getParams());
    }

}
