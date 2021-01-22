package com.qingcloud.iot.core.codec;

import java.util.Objects;

public class ModelPropertyData {

    public Integer time;
    public Object value;

    public ModelPropertyData(Integer time, Object value) {
        this.time = time;
        this.value = value;
    }

    public ModelPropertyData() {
        this(Integer.valueOf(0), new Object());
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
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
