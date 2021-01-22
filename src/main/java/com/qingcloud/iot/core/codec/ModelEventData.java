package com.qingcloud.iot.core.codec;

import java.util.HashMap;
import java.util.Objects;

public class ModelEventData {

    public String message;
    public String level;
    public Integer time;
    public HashMap<String, Object> value;

    public ModelEventData() {
        message = "";
        level = "";
        time = Integer.valueOf(0);
        value = new HashMap<String,Object>();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public HashMap<String, Object> getValue() {
        return value;
    }

    public void setValue(HashMap<String, Object> value) {
        this.value = value;
    }

    public ModelEventData(String message, String level, Integer time, HashMap<String, Object> value) {
        this.message = message;
        this.level = level;
        this.time = time;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelEventData)) return false;
        ModelEventData that = (ModelEventData) o;
        return time == that.time &&
                Objects.equals(message, that.message) &&
                Objects.equals(level, that.level) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, level, time, value);
    }

}
