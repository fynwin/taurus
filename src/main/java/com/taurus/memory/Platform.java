package com.taurus.memory;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * Created by ynfeng on 16/8/19.
 */
public class Platform {
    private static Unsafe unsafe;

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (Throwable cause) {
            unsafe = null;
        }
    }

    public static ByteBuffer allocateDirectBuffer(int size){
        ByteBuffer buffer = ByteBuffer.allocate(size);
        return buffer;
    }

    public static long directBufferAddress(ByteBuffer buffer){
        Field field = null;
        try {
            field = Buffer.class.getDeclaredField("address");
            field.setAccessible(true);
            long fileOffset = unsafe.objectFieldOffset(field);
            return unsafe.getLong(buffer,fileOffset);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return -1L;
    }


}
