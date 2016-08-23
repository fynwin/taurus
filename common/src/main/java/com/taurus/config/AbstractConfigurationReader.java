package com.taurus.config;

/**
 * Created by ynfeng on 16/8/23.
 */
public abstract class AbstractConfigurationReader implements ConfigurationReader {
    private ConfigurationDataSource dataSource;

    public AbstractConfigurationReader(ConfigurationDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getString(String key, String defaultValue) {
        checkKey(key);
        String configure = dataSource.getString(key);
        return configure == null ? defaultValue : configure;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        checkKey(key);
        Integer configure = dataSource.getInt(key);
        return configure == null ? defaultValue : configure;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        checkKey(key);
        Boolean configure = dataSource.getBoolean(key);
        return configure == null ? defaultValue : configure;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        checkKey(key);
        Float configure = dataSource.getFloat(key);
        return configure == null ? defaultValue : configure;
    }

    private void checkKey(String key) {
        if (key == null) {
            throw new NullPointerException("key");
        }
        if(key.isEmpty()){
            throw new IllegalArgumentException("key must not be empty.");
        }
    }
}
