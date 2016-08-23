package com.taurus.config;

/**
 * Created by ynfeng on 16/8/23.
 */
public class ConfigurationReaderFactory {
    private static final SystemConfigDataSource systemConfigDataSource = new SystemConfigDataSource();
    private static final SystemConfigReader systemConfigReader = new SystemConfigReader(systemConfigDataSource);

    public static ConfigurationReader getSystemConfigurationReader(){
        return systemConfigReader;
    }

}
