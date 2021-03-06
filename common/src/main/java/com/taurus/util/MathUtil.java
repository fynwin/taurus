package com.taurus.util;

/**
 * Created by ynfeng on 16/8/26.
 */
public class MathUtil {

    public static int log2(int val){
        return Integer.SIZE - 1 - Integer.numberOfLeadingZeros(val);
    }

    public static int to2N(int val){
        int i = MathUtil.log2(val);
        int mask = 1 << i;
        if((val & ~mask) != 0){
            return 1 << ++i;
        } else {
            return val;
        }
    }

}
