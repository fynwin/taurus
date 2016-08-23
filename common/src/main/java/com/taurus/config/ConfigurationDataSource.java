package com.taurus.config;

/**
 * 配置读取的数据源
 * Created by ynfeng on 16/8/23.
 */
public interface ConfigurationDataSource {
    /**
     * 读取字符串类型配置,当配置不存在是返回{@code null}
     *
     * @param key 配置名称
     * @return 配置
     */
    String getString(String key);

    /**
     * 读取整型类型的配置,当配置不存在是返回{@code null}
     *
     * @param key 配置名称
     * @return 配置
     */
    Integer getInt(String key);

    /**
     * 读取布尔类型的配置,当配置不存在是返回{@code null}
     *
     * @param key 配置名称
     * @return 配置
     */
    Boolean getBoolean(String key);

    /**
     * 读取浮点类型的配置,当配置不存在是返回{@code null}
     *
     * @param key 配置名称
     * @return 配置
     */
    Float getFloat(String key);
}
