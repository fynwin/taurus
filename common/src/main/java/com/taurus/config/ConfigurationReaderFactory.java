package com.taurus.config;

/**
 * Created by ynfeng on 16/8/23.
 */
public class ConfigurationReaderFactory {
    private static final SystemConfigDataSource systemConfigDataSource = new SystemConfigDataSource();
    private static final ConfigurationReader systemConfigReader = new SystemConfigReader(systemConfigDataSource);
    private static final ConfigurationReader systemConfigReaderWithCache = new CacheWraper(systemConfigReader);

    /**
     * 如果{@code cache}为{@code true}则表示{@link ConfigurationReader}是带缓存的
     *
     * @param cache
     * @return
     */
    public static ConfigurationReader getSystemConfigurationReader(boolean cache) {
        if (cache) {
            return systemConfigReaderWithCache;
        } else {
            return systemConfigReader;
        }
    }

}
