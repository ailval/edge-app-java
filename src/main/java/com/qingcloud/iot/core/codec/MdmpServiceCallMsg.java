package com.qingcloud.iot.core.codec;

import java.util.HashMap;
import java.util.Objects;

public class MdmpServiceCallMsg {
    public MdmpMsgHeader mdmpMsgHeader;
    public HashMap<String, Object> params;

    public MdmpServiceCallMsg() {
        this(new MdmpMsgHeader(),new HashMap<String,Object>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MdmpServiceCallMsg that = (MdmpServiceCallMsg) o;
        return mdmpMsgHeader.equals(that.mdmpMsgHeader) &&
                params.equals(that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdmpMsgHeader, params);
    }

    public MdmpServiceCallMsg(MdmpMsgHeader mdmpMsgHeader,HashMap<String, Object> params) {
        this.mdmpMsgHeader = mdmpMsgHeader;
        this.params = params;
    }

    public MdmpMsgHeader getMdmpMsgHeader() {
        return mdmpMsgHeader;
    }

    public void setMdmpMsgHeader(MdmpMsgHeader mdmpMsgHeader) {
        this.mdmpMsgHeader = mdmpMsgHeader;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

}
