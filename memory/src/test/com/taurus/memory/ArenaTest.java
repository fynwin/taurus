package com.taurus.memory;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ynfeng on 2016/11/2.
 */
public class ArenaTest {

    @Test
    public void testChunkMalloc() {
        PooledBuffer buffer = new PooledBuffer();
        Arena arena = new Arena(1 << 24, 1 << 13);
        arena.malloc(buffer,128);
        Assert.assertEquals(arena.numOfChunks(),1);
        arena.free(buffer);
        Assert.assertEquals(arena.numOfChunks(),0);
    }

    @Test
    public void testChunkMalloc1() {
        PooledBuffer buffer = new PooledBuffer();
        Arena arena = new Arena(1 << 24, 1 << 13);
        arena.malloc(buffer,1 << 23);
        arena.malloc(buffer,1 << 23);
        Assert.assertEquals(arena.numOfChunks(),1);
        arena.malloc(buffer,1 << 23);
        Assert.assertEquals(arena.numOfChunks(),2);
        arena.free(buffer);
        Assert.assertEquals(arena.numOfChunks(),1);
    }

    @Test
    public void testChunkMalloc3() {
        PooledBuffer buffer = new PooledBuffer();
        int allocSize = 1024;
        Arena arena = new Arena(1 << 24, 1 << 13);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.free(buffer);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);

        arena.malloc(buffer,allocSize);
        arena.free(buffer);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        arena.malloc(buffer,allocSize);
        System.out.println(arena);
    }

}
