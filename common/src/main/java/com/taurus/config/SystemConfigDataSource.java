package com.taurus.config;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Created by ynfeng on 16/8/23.
 */
public class SystemConfigDataSource implements ConfigurationDataSource{

    @Override
    public String getString(String key) {
        return get(key);
    }

    @Override
    public Integer getInt(String key) {
        String configure = get(key);
        return (configure == null || configure.isEmpty()) ? null : Integer.valueOf(configure);
    }

    @Override
    public Boolean getBoolean(String key) {
        String configure = get(key);
        return (configure == null || configure.isEmpty()) ? null : Boolean.valueOf(configure);
    }

    @Override
    public Float getFloat(String key) {
        String configure = get(key);
        return (configure == null || configure.isEmpty()) ? null : Float.valueOf(configure);
    }

    private String get(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty.");
        }

        String value = null;
        try {
            if (System.getSecurityManager() == null) {
                value = System.getProperty(key);
            } else {
                value = AccessController.doPrivileged(new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty(key);
                    }
                });
            }
        } catch (Exception e) {
        }
        return value;
    }
}
