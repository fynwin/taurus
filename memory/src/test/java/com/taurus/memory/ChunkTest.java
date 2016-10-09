package com.taurus.memory;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ynfeng on 2016/10/8.
 */
public class ChunkTest {
    private static final int K = 1 << 10;
    private static final int M = K << 10;

    @Test
    public void chunkInitTest(){
        Chunk chunk = new Chunk(new Object(), 16, 4);
        chunk.printTree();
        System.out.println(chunk.toString());
    }

    @Test
    public void chunkMallocTest(){
        Chunk chunk = new Chunk(new Object(), 16, 4);
        Assert.assertNotEquals(chunk.malloc(4),-1);
        Assert.assertNotEquals(chunk.malloc(4),-1);
        Assert.assertNotEquals(chunk.malloc(4),-1);
        Assert.assertNotEquals(chunk.malloc(4),-1);
        Assert.assertEquals(chunk.malloc(4),-1);
        chunk.printTree();
    }
}
