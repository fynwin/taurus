package com.taurus.config;



/**
 * Created by ynfeng on 16/8/23.
 */
public class ConfigTest {

    /**
     * -Dcom.taurus.config.test=yes
     * @param args
     */
    public static void main(String args[]){
        ConfigurationReader configurationReader = ConfigurationReaderFactory.getSystemConfigurationReader();
        String testConfig = configurationReader.getString("com.taurus.config.test","no");
        System.out.println(testConfig);
    }
}
