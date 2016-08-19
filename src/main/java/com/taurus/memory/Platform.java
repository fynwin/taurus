package com.taurus.memory;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

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


}
