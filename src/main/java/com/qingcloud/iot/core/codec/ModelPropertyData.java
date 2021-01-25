package com.qingcloud.iot.core.codec;

import java.util.Objects;

public class ModelPropertyData {

    public Long time;
    public Object value;

    public ModelPropertyData(Long time, Object value) {
        this.time = time;
        this.value = value;
    }

    public ModelPropertyData() {
        this(Long.valueOf(0), new Object());
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelPropertyData that = (ModelPropertyData) o;
        return time == that.time &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, value);
    }

}
