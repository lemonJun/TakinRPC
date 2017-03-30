package com.lemon.soa.common;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * Data Convert
 * 
 * me
 */
public abstract class Convert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Parameter acquisition
     * 
     * @param key
     * @return
     */
    public abstract String getValue(String key);

    public String getValue(String key, String defVal) {
        String value = getValue(key);
        return value != null ? value : defVal;
    }

    public Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    public Integer getInteger(String key, Integer defVal) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : defVal;
    }

    public Long getLong(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Long.valueOf(value);
    }

    public Long getLong(String key, Long defVal) {
        String value = getValue(key);
        return value != null ? Long.valueOf(value) : defVal;
    }

    public Double getDouble(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Double.valueOf(value);
    }

    public Double getDouble(String key, Double defVal) {
        String value = getValue(key);
        return value != null ? Double.valueOf(value) : defVal;
    }

    public Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

    public Boolean getBoolean(String key, boolean defVal) {
        String value = getValue(key);
        return value != null ? Boolean.valueOf(value) : defVal;
    }

}