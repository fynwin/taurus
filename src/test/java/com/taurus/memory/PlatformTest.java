package com.taurus.memory;

import org.junit.Test;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by ynfeng on 16/8/19.
 */
public class PlatformTest {

    @Test
    public void testAllocateDirectBuffer() {
        ByteBuffer buffer = Platform.allocateDirectBuffer(20);
        long address = Platform.directBufferAddress(buffer);
        System.out.println(address);
        buffer = Platform.allocateDirectBuffer(20);
        address = Platform.directBufferAddress(buffer);
        System.out.println(address);
    }
}
