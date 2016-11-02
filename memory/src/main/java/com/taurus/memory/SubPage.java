package com.taurus.memory;

import com.taurus.config.ConfigurationReader;
import com.taurus.config.SystemConfigReader;
import com.taurus.util.MathUtil;

/**
 * Created by ynfeng on 2016/10/24.
 */
public class SubPage<T> {
    private ConfigurationReader configurationReader = SystemConfigReader.getInstance(true);
    public SubPage<T> prev;
    public SubPage<T> next;
    private final int pageSize;
    private final int elementSize;
    private final long bitmap[];
    private final int capacity;
    private final int elementNum;
    private final int memoryMapIdx;
    public final SubPage<T> head;
    private int nextAvailable;
    private int numAvailable;

    /**
     * For head
     */
    public SubPage() {
        this.pageSize = 0;
        this.elementSize = configurationReader.getInt("taurus.memory.elementSize", 16);
        bitmap = null;
        capacity = 0;
        nextAvailable = 0;
        elementNum = 0;
        memoryMapIdx = 0;
        head = null;
        numAvailable = 0;
    }

    public SubPage(SubPage<T> head, int pageSize, int capacity, int memoryMapIdx) {
        this.head = head;
        this.pageSize = pageSize;
        this.capacity = capacity;
        this.memoryMapIdx = memoryMapIdx;
        this.elementSize = configurationReader.getInt("taurus.memory.elementSize", 16);
        int elementSizeShift = MathUtil.log2(elementSize);
        //默认bitmap数组中每个元素的64位中的一位代表一个16字节内存的使用情况
        numAvailable = elementNum = pageSize >> elementSizeShift;
        bitmap = new long[pageSize >> elementSizeShift >> 6];
        nextAvailable = 0;
    }


    public long malloc() {
        long bitMap[] = this.bitmap;
        long val = bitMap[nextAvailable];
        if (val == -1 && nextAvailable < elementNum - 1) {
            val = bitMap[++nextAvailable];
        }
        if (val == -1) {
            return -1;
        }
        int i = 0;
        while ((val >> i++) != 0) ;
        val |= (long) 1 << (i - 1);
        bitMap[nextAvailable] = val;
        if (--numAvailable == 0) {
            removeFromPool();
        }
        return toHandle(nextAvailable);
    }

    public void free(long handle) {
        long bitMap[] = this.bitmap;
        int bitMapIdx = (int) (handle >> 32) & ~(0x40000000);
        long val = bitMap[bitMapIdx];
        int i = 0;
        while (((val >> i++) & 1) == 0) ;
        val = val >> i << i;
        bitMap[bitMapIdx] = val;
        if (numAvailable++ == 0) {
            addToPool();
        }
        if (numAvailable == elementNum) {
            removeFromPool();
        }
    }

    public void reuse() {
        numAvailable = elementNum;
        nextAvailable = 0;
        for (int i = 0; i < bitmap.length; i++) {
            bitmap[i] = 0;
        }
    }

    private void addToPool() {
        prev = head;
        next = head.next;
        head.next = this;
        next.prev = this;
    }

    private void removeFromPool() {
        if (prev != null && next != null) {
            prev.next = next;
            next.prev = prev;
            next = null;
            prev = null;
        }
    }

    private long toHandle(int bitMapIdx) {
        return 0x4000000000000000L | (long) bitMapIdx << 32 | memoryMapIdx;
    }

}
