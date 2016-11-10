package com.taurus.memory;


/**
 * Created by ynfeng on 2016/11/3.
 * chunk管理算法，5个数组按内存使用率存放chunk链表。
 * 0:0~24
 * 1:25~49
 * 2:50~74
 * 3:75~100
 */
public class ChunkList<T> implements ChunkListMetric {
    private Chunk<T> chunks[] = new Chunk[4]; //0,25,50,75
    private int num_of_0_24;
    private int num_of_25_49;
    private int num_of_50_74;
    private int num_of_75_100;

    public ChunkList() {
        for (int i = 0; i < 4; i++) {
            chunks[i] = newHead();
        }
    }

    public boolean malloc(PooledBuffer<T> buffer, int capacity) {
        for (Chunk<T> head : chunks) {
            Chunk<T> chunk = head.next;
            if (chunk == head) continue;
            for (; ; ) {
                long handle = chunk.malloc(capacity);
                if (handle != -1) {
                    chunk.initBuf(buffer, handle);
                    adjust(chunk);
                    return true;
                }
                chunk = chunk.next;
                if (chunk == head) {
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
            updateMetricUsageNum(chunk.poolIdx, -1);
            add0(poolIdx, chunk);
            updateMetricUsageNum(poolIdx, 1);
        }
    }

    private Chunk<T> newHead() {
        Chunk<T> head = new Chunk<>();
        head.next = head;
        head.prev = head;
        return head;
    }

    public void add(Chunk<T> chunk) {
        int poolIdx = getPoolIdx(chunk.usage());
        add0(poolIdx, chunk);
        updateMetricUsageNum(poolIdx, 1);
    }

    public boolean free(PooledBuffer<T> pooledBuffer) {
        Chunk<T> chunk = pooledBuffer.chunk;
        chunk.free(pooledBuffer.handler);
        int usage = chunk.usage();
        if (usage == 0) {
            remove0(chunk);
            updateMetricUsageNum(chunk.poolIdx, -1);
            return true;
        }
        adjust(chunk);
        return false;
    }

    private void add0(int poolIdx, Chunk<T> node) {
        final Chunk<T> head = chunks[poolIdx];
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
        if (usage >= 0 && usage < 25) {
            return 0;
        } else if (usage > 24 && usage < 50) {
            return 1;
        } else if (usage > 49 && usage < 75) {
            return 2;
        } else {
            return 3;
        }
    }

    private void updateMetricUsageNum(int poolIdx, int val) {
        switch (poolIdx) {
            case 0:
                num_of_0_24 += val;
                break;
            case 1:
                num_of_25_49 += val;
                break;
            case 2:
                num_of_50_74 += val;
                break;
            case 3:
                num_of_75_100 += val;
                break;
        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("内存片使用情况,已分配内存片总数%d:\n", totalChunkNum()))
                .append(String.format("使用率0~24%%的内存片数量:%d", numOf0to24PercentUsage()))
                .append("\n")
                .append(String.format("使用率25~49%%的内存片数量:%d", numOf25to49PercentUsage()))
                .append("\n")
                .append(String.format("使用率50~74%%的内存片数量:%d", numOf50to74PercentUsage()))
                .append("\n")
                .append(String.format("使用率75~100%%的内存片数量:%d", numOf75to100PercentUsage()))
                .append("\n");
        return sb.toString();
    }

    @Override
    public int numOf0to24PercentUsage() {
        return num_of_0_24;
    }

    @Override
    public int numOf25to49PercentUsage() {
        return num_of_25_49;
    }

    @Override
    public int numOf50to74PercentUsage() {
        return num_of_50_74;
    }

    @Override
    public int numOf75to100PercentUsage() {
        return num_of_75_100;
    }

    @Override
    public int totalChunkNum() {
        return num_of_0_24 + num_of_25_49 + num_of_50_74 + num_of_75_100;
    }
}
