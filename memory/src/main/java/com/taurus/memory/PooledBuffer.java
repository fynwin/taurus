package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/3.
 */
public class PooledBuffer<T> implements Buffer {
    public long handler;
    public Chunk<T> chunk;

    public void init(Chunk<T> chunk, long handle) {
        this.handler = handle;
        this.chunk = chunk;
    }
}
