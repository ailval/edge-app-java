package com.qingcloud.iot.core.codec;

import java.util.HashMap;
import java.util.Objects;

public class ServiceReplyMessage {

    public ThreadLocal<MdmpMsgReplyHeader> mdmpMsgReplyHeader;
    public HashMap<String, Object> data;

    public MdmpMsgReplyHeader getMdmpMsgReplyHeader() {
        return mdmpMsgReplyHeader.get();
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceReplyMessage that = (ServiceReplyMessage) o;
        return Objects.equals(mdmpMsgReplyHeader, that.mdmpMsgReplyHeader) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mdmpMsgReplyHeader.get(), data);
    }

    public ServiceReplyMessage(MdmpMsgReplyHeader mdmpMsgReplyHeader,HashMap<String, Object> data) {
        this.mdmpMsgReplyHeader = new ThreadLocal<MdmpMsgReplyHeader>();
        this.mdmpMsgReplyHeader.set(mdmpMsgReplyHeader);
        this.data = data;
    }
}
