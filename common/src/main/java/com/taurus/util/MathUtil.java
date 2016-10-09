package com.taurus.util;

/**
 * Created by ynfeng on 16/8/26.
 */
public class MathUtil {
    public static int log2(int val){
        return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(val);
    }
}
