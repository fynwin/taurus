package com.taurus.config;

/**
 * Created by ynfeng on 16/8/23.
 */
public class SystemConfigReader extends AbstractConfigurationReader {

    public static final ConfigurationReader INSTANCE = new SystemConfigReader(new SystemConfigDataSource());
    public static final ConfigurationReader CACHED_INSTANCE = new CacheWraper(INSTANCE);

    public SystemConfigReader(ConfigurationDataSource dataSource) {
        super(dataSource);
    }

    public static ConfigurationReader getInstance(boolean cached) {
        if (cached) {
            return CACHED_INSTANCE;
        } else {
            return INSTANCE;
        }
    }
}
