package com.taurus.config;


import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by ynfeng on 16/8/26.
 * 支持缓存的包装类
 */
public class CacheWraper implements ConfigurationReader{
    private ConfigurationReader wrap;
    private Map<String,Object> cache = null;

    public CacheWraper(ConfigurationReader wrap) {
        this.wrap = wrap;
        cache = Collections.synchronizedMap(new WeakHashMap<>());
    }

    public ConfigurationReader unwrap(){
        return wrap;
    }

    @Override
    public String getString(String key, String defaultValue) {
        String ret = (String)cache.get(key);
        if(ret == null){
            ret = wrap.getString(key,defaultValue);
            cache.putIfAbsent(key,ret);
        }
        return ret;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        Integer ret = (Integer) cache.get(key);
        if(ret == null){
            ret = wrap.getInt(key,defaultValue);
            cache.putIfAbsent(key,ret);
        }
        return ret;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean ret = (Boolean) cache.get(key);
        if(ret == null){
            ret = wrap.getBoolean(key,defaultValue);
            cache.putIfAbsent(key,ret);
        }
        return ret;
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        Float ret = (Float) cache.get(key);
        if(ret == null){
            ret = wrap.getFloat(key,defaultValue);
            cache.putIfAbsent(key,ret);
        }
        return ret;
    }
}
