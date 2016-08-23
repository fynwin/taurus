package com.taurus.config;

/**
 * Created by ynfeng on 16/8/22.
 * 从各种数据源读取配置的接口
 */
public interface ConfigurationReader {
    /**
     * 读取字符串类型配置,当配置不存在时返回{@code defaultValue}
     *
     * @param key          配置名称
     * @param defaultValue 默认值
     * @return 配置
     */
    String getString(String key, String defaultValue);

    /**
     * 读取整型类型的配置,当配置不存在时返回{@code defaultValue}
     *
     * @param key          配置名称
     * @param defaultValue 默认值
     * @return 配置
     */
    int getInt(String key, int defaultValue);

    /**
     * 读取布尔类型的配置,当配置不存在时返回{@code defaultValue}
     *
     * @param key          配置名称
     * @param defaultValue 默认值
     * @return 配置
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * 读取浮点类型的配置,当配置不存在时返回{@code defaultValue}
     *
     * @param key          配置名称
     * @param defaultValue 默认值
     * @return 配置
     */
    float getFloat(String key, float defaultValue);

}
