package com.taurus.memory;

import org.junit.Test;

/**
 * Created by mac on 2016/11/2.
 */
public class ArenaTest {

    @Test
    public void testMalloc(){
        UnsafeDirectBuffer buffer = new UnsafeDirectBuffer();
        Arena<Object> arena = new Arena<>(1<<24,1<<13);
        arena.malloc(buffer,256);
        arena.free(buffer.handle);
        arena.malloc(buffer,256);
    }
}
