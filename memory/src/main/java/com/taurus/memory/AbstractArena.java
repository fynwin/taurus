package com.taurus.memory;

import com.taurus.config.ConfigurationReader;
import com.taurus.config.SystemConfigReader;
import com.taurus.util.MathUtil;

/**
 * Created by ynfeng on 2016/10/24.
 * 参考jemalloc
 */
public abstract class AbstractArena<T> implements ArenaMetric {
    private ConfigurationReader configurationReader = SystemConfigReader.getInstance(true);
    private final SubPage<T> subPagesTiny[];
    private final SubPage<T> subPagesSmall[];
    private final int pageSize;
    private final int chunkSize;
    private final int tinyMaxSize;
    private final int tinyMaxSizeShift;
    private final int pageSizeShift;
    private final int chunkSizeShift;
    private ChunkList<T> chunkList;

    public AbstractArena(int chunkSize, int pageSize) {
        this.tinyMaxSize = configurationReader.getInt("taurus.memory.tinySize", 512);
        this.pageSize = pageSize;
        this.chunkSize = chunkSize;
        this.pageSizeShift = MathUtil.log2(pageSize);
        this.chunkSizeShift = MathUtil.log2(chunkSize);
        this.tinyMaxSizeShift = MathUtil.log2(tinyMaxSize);
        subPagesTiny = new SubPage[MathUtil.log2(tinyMaxSize) + 1];
        subPagesSmall = new SubPage[pageSizeShift - subPagesTiny.length];
        chunkList = new ChunkList<T>();
        for (int i = 0; i < subPagesTiny.length; i++) {
            subPagesTiny[i] = newHead();
        }
        for (int i = 0; i < subPagesSmall.length; i++) {
            subPagesSmall[i] = newHead();
        }
    }

    private SubPage newHead() {
        SubPage<T> head = new SubPage<T>();
        head.next = head;
        head.prev = head;
        return head;
    }

    public PooledBuffer malloc(int reqCapacity) {
        int capacity = MathUtil.to2N(reqCapacity);
        PooledBuffer<T> pooledBuffer = newBuffer();
        malloc(pooledBuffer, capacity);
        return pooledBuffer;
    }

    private void malloc(PooledBuffer<T> buffer, int capacity) {
        SubPage<T> table[];
        int idx;
        if (isTinyOrSmall(capacity)) {
            if (isTiny(capacity)) {
                //TODO 先尝试从线程缓存中分分配
                table = subPagesTiny;
                idx = tinyIndex(capacity);
            } else {
                //TODO 先尝试从线程缓存中分分配
                table = subPagesSmall;
                idx = smallIndex(capacity);
            }
            SubPage<T> head = table[idx];
            synchronized (head) {
                SubPage<T> next = head.next;
                if (next != head) {
                    long handle = next.malloc();
                    next.chunk.initBuf(buffer, handle);
                    return;
                }
            }
            normalMalloc(buffer, capacity);
        } else if (isHuge(capacity)) {
            hugeMalloc(buffer, capacity);
        } else {
            normalMalloc(buffer, capacity);
        }
    }

    private boolean isTinyOrSmall(int capacity) {
        return isTiny(capacity) || isSmal(capacity);
    }

    private boolean isTiny(int capacity) {
        return capacity <= tinyMaxSize;
    }

    private boolean isSmal(int capacity) {
        return capacity > tinyMaxSize && capacity < pageSize;
    }

    private boolean isHuge(int capacity) {
        return capacity > chunkSize;
    }

    private int tinyIndex(int capacity) {
        return MathUtil.log2(capacity);
    }

    private int smallIndex(int capacity) {
        return MathUtil.log2(capacity) - tinyMaxSizeShift - 1;
    }

    private void hugeMalloc(PooledBuffer<T> buffer, int capacity) {
        Chunk<T> chunk = newUnPooledChunk(capacity, capacity);
        buffer.initUnpooled(chunk);
    }

    private synchronized void normalMalloc(PooledBuffer<T> buffer, int capacity) {
        if (!chunkList.malloc(buffer, capacity)) {
            Chunk<T> chunk = newPooledChunk(chunkSize, pageSize);
            long handle = chunk.malloc(capacity);
            chunk.initBuf(buffer, handle);
            chunkList.add(chunk);
        }
    }

    public synchronized void free(PooledBuffer<T> buffer) {
        //TODO 缓存
        //TODO 如果是超大内存不缓存直接回收
        if (chunkList.free(buffer)) {
            destroyChunk(buffer.chunk);
        }
    }

    protected SubPage<T> findHead(int capacity) {
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


    private int countSubpage(SubPage<T> head) {
        int c = 0;
        SubPage<T> next = head.next;
        while (next != head) {
            c++;
            next = next.next;
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chunkList.toString())
                .append("\n")
                .append("tinySubpage缓存:")
                .append("\n");
        for (int i = 0; i < subPagesTiny.length; i++) {
            sb.append((1 << i) + "字节:");
            sb.append(countSubpage(subPagesTiny[i]) + "个\n");
        }
        sb.append("\n");
        sb.append("smallSubpage缓存:");
        sb.append("\n");
        for (int i = 0; i < subPagesSmall.length; i++) {
            sb.append((1 << (i + subPagesTiny.length)) + "字节:");
            sb.append(countSubpage(subPagesSmall[i]) + "个\n");
        }

        return sb.toString();
    }

    public abstract Chunk<T> newPooledChunk(int chunkSize, int pageSize);

    public abstract void destroyChunk(Chunk<T> chunk);

    public abstract PooledBuffer<T> newBuffer();

    public abstract Chunk<T> newUnPooledChunk(int chunkSize, int pageSize);

    @Override
    public int numOfChunks() {
        return chunkList.totalChunkNum();
    }

    @Override
    public int numOfTinySubpages() {
        return 0;
    }

    @Override
    public int numOfSmallSubpages() {
        return 0;
    }
}
