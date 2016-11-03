package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/3.
 * chunk管理算法，5个数组按内存使用率存放chunk链表。
 * 0:0~24
 * 1:25~49
 * 2:50~74
 * 3:75~100
 */
public class ChunkList<T> {
    private Chunk<T> chunks[] = new Chunk[4]; //0,25,50,75

    public ChunkList() {
        for (int i = 0; i < 5; i++) {
            chunks[i] = newHead();
        }
    }

    public boolean malloc(PooledBuffer<T> buffer, int capacity) {
        for (Chunk<T> head : chunks) {
            Chunk<T> chunk = head.next;
            if (chunk == null) continue;
            for (; ; ) {
                long handle = chunk.malloc(capacity);
                if (handle != -1) {
                    chunk.initBuf(buffer, handle);
                    adjust(chunk);
                    return true;
                }
                chunk = chunk.next;
                if (chunk == null) {
                    break;
                }
            }
        }
        return false;
    }

    public void adjust(Chunk<T> chunk) {
        int poolIdx = getPoolIdx(chunk.usage());
        if (poolIdx != chunk.poolIdx) {
            remove0(chunk);
            add0(poolIdx, chunk);
        }
    }

    private Chunk<T> newHead() {
        Chunk<T> head = new Chunk<>();
        head.next = head;
        head.prev = head;
        return head;
    }

    public void add(Chunk<T> chunk) {
        add0(0, chunk);
    }

    public boolean free(PooledBuffer<T> pooledBuffer) {
        Chunk<T> chunk = pooledBuffer.chunk;
        chunk.free(pooledBuffer.handler);
        int usage = chunk.usage();
        if (usage == 0) {
            remove0(chunk);
            return true;
        }
        return false;
    }

    private void add0(int poolIdx, Chunk<T> node) {
        assert poolIdx >= 0;
        Chunk<T> head = chunks[poolIdx];
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
        node.poolIdx = poolIdx;
    }

    private void remove0(Chunk<T> chunk) {
        chunk.prev.next = chunk.next;
        chunk.next.prev = chunk.prev;
        chunk.prev = null;
        chunk.next = null;
    }

    private int getPoolIdx(int usage) {
        if (usage > 0 && usage < 25) {
            return 0;
        } else if (usage > 24 && usage < 50) {
            return 1;
        } else if (usage > 49 && usage < 75) {
            return 2;
        } else {
            return 3;
        }
    }

}
