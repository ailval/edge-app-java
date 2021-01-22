package com.qingcloud.iot.core.codec;

import java.util.Objects;

public class MdmpEventMsg {

    public MdmpMsgHeader mdmpMsgHeader;
    public ModelEventData params;

    public MdmpEventMsg() {
        mdmpMsgHeader = new MdmpMsgHeader();
        params = new ModelEventData();
    }

    public MdmpMsgHeader getMdmpMsgHeader() {
        return mdmpMsgHeader;
    }

    public void setMdmpMsgHeader(MdmpMsgHeader mdmpMsgHeader) {
        this.mdmpMsgHeader = mdmpMsgHeader;
    }

    public ModelEventData getParams() {
        return params;
    }

    public void setParams(ModelEventData params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MdmpEventMsg)) return false;
        MdmpEventMsg that = (MdmpEventMsg) o;
        return Objects.equals(mdmpMsgHeader, that.mdmpMsgHeader) &&
                Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdmpMsgHeader, params);
    }

    public MdmpEventMsg(MdmpMsgHeader messageHeader,ModelEventData params) {
        this.mdmpMsgHeader = messageHeader;
        this.params = params;
    }
}
