package com.taurus.memory;

import com.taurus.config.ConfigurationReader;
import com.taurus.config.SystemConfigReader;
import com.taurus.util.MathUtil;

/**
 * Created by ynfeng on 2016/10/24.
 * 参考jemalloc
 */
public class Arena<T> {
    private ConfigurationReader configurationReader = SystemConfigReader.getInstance(true);
    private final SubPage<T> subPagesTiny[];
    private final SubPage<T> subPagesSmall[];
    private final int pageSize;
    private final int chunkSize;
    private final int tinyMaxSize;
    private final int tinyMaxSizeShift;
    private final int pageSizeShift;
    private final int chunkSizeShift;

    public Arena(int chunkSize, int pageSize) {
        assert pageSize >= configurationReader.getInt("taurus.memory.elementSize", 16) << 6;
        this.tinyMaxSize = configurationReader.getInt("taurus.memory.tinySize", 512);
        this.pageSize = pageSize;
        this.chunkSize = chunkSize;
        this.pageSizeShift = MathUtil.log2(pageSize);
        this.chunkSizeShift = MathUtil.log2(chunkSize);
        this.tinyMaxSizeShift = MathUtil.log2(tinyMaxSize);
        subPagesTiny = new SubPage[MathUtil.log2(tinyMaxSize) + 1];
        subPagesSmall = new SubPage[pageSizeShift - (subPagesTiny.length - 1)];
        for (int i = 0; i < subPagesTiny.length; i++) {
            subPagesTiny[i] = newHead();
        }
        for (int i = 0; i < subPagesSmall.length; i++) {
            subPagesSmall[i] = newHead();
        }
    }

    public void malloc(int reqCapacity) {
        int capacity = MathUtil.to2N(reqCapacity);
        SubPage<T> table[];
        int idx;
        if (isTinyOrSmall(capacity)) {
            if (isTiny(capacity)) {
                //TODO 先尝试从缓存中分分配
                table = subPagesTiny;
                idx = tinyIndex(capacity);
            } else {
                //TODO 先尝试从缓存中分分配
                table = subPagesSmall;
                idx = smallIndex(capacity);
            }
            SubPage<T> head = table[idx];
            synchronized (head) {
                SubPage<T> next = head.next;
                if (next != head) {
                    //TODO 从subpage池中分配
                    return;
                }
            }
            //TODO 从chunk中分配

        } else if (isHuge(capacity)) {
            //TODO 超大的内存分配
        } else {

        }
    }

    public SubPage<T> findHead(int capacity) {
        SubPage<T> table[];
        int idx;
        if (isTiny(capacity)) {
            table = subPagesTiny;
            idx = tinyIndex(capacity);
        } else if (isSmal(capacity)) {
            table = subPagesSmall;
            idx = smallIndex(capacity);
        } else {
            throw new IllegalArgumentException(String.format("capacity:%d can't allocate from subpage.", capacity));
        }
        return table[idx];
    }

    private int tinyIndex(int capacity) {
        return MathUtil.log2(capacity) + 1;
    }

    private int smallIndex(int capacity) {
        return MathUtil.log2(capacity) - tinyMaxSizeShift - 1;
    }

    private boolean isTinyOrSmall(int capacity) {
        return isTiny(capacity) || isSmal(capacity);
    }

    private boolean isTiny(int capacity) {
        return capacity <= tinyMaxSize;
    }

    private boolean isSmal(int capacity) {
        return capacity > tinyMaxSize && capacity <= pageSize;
    }

    private boolean isHuge(int capacity) {
        return capacity > chunkSize;
    }

    private SubPage newHead() {
        SubPage<T> head = new SubPage<T>();
        head.next = head;
        head.prev = head;
        return head;
    }
}
