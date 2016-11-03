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
    private final int capacity;
    private final int memoryMapIdx;
    private int availableCapacity;
    public final SubPage<T> head;
    public final Chunk<T> chunk;

    /**
     * For head
     */
    public SubPage() {
        this.pageSize = 0;
        this.capacity = 0;
        this.memoryMapIdx = 0;
        this.head = null;
        this.chunk = null;
    }

    public SubPage(SubPage<T> head, Chunk<T> chunk,int pageSize, int capacity, int memoryMapIdx) {
        this.head = head;
        this.pageSize = pageSize;
        this.capacity = capacity;
        this.memoryMapIdx = memoryMapIdx;
        this.availableCapacity = pageSize;
        this.chunk = chunk;
    }


    public long malloc() {
        if (availableCapacity < capacity) {
            return -1;
        }
        availableCapacity -= capacity;
        //当一页全部被分配完，从池中移出
        if (availableCapacity == 0) {
            removeFromPool();
        }
        return toHandle(capacity);
    }

    public void free(long handle) {
        int freeCapacity = ((int) (handle >>> 32)) & ~0x40000000;
        //当一页全部被分配完后，如果有释放操作，加入池中。
        if (availableCapacity == 0) {
            addToPool();
        }
        availableCapacity += freeCapacity;
        //当一页全部被释放，从池中移出
        if (availableCapacity == pageSize) {
            //保留一个
            if (prev != next) {
                removeFromPool();
            }
        }
    }

    public void reuse() {
        availableCapacity = pageSize;
        addToPool();
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

    private long toHandle(int capacity) {
        return 0x4000000000000000L | ((long) capacity) << 32 | memoryMapIdx;
    }

}
