package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/2.
 */
public class UnsafeDirectBuffer implements Buffer {
    public long handle;

    public void init(long handle){
        this.handle = handle;
    }
}
