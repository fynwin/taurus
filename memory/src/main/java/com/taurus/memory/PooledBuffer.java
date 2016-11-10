package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/3.
 */
public class PooledBuffer<T> implements Buffer {
    public long handle;
    public Chunk<T> chunk;

    public void init(Chunk<T> chunk, long handle) {
        this.handle = handle;
        this.chunk = chunk;
    }

    public void initUnpooled(Chunk<T> chunk){
        this.chunk = chunk;
    }
}
