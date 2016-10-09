package com.taurus.memory;

import com.taurus.util.MathUtil;

import java.util.Arrays;

/**
 * 内存管理，伙伴算法
 * Created by ynfeng on 16/8/30.
 */
public final class Chunk<T> {
    private final byte[] pageMap;
    private final byte[] depthMap;
    private final int chunkSize;
    private final byte unusable;
    private final int pageSize;
    private final int pageSizeShift;
    private final int pageSizeOverflowMask;
    private final int maxDepth;
    private int freeBytes;

    /**
     * 初始化内存块
     *
     * @param memory    真实的内存块
     * @param chunkSize 内存块大小
     * @param pageSize  页大小
     */
    //chunkSize = 2^{maxDepth} * pageSize
    public Chunk(T memory, int chunkSize, int pageSize) {
        int maxDepth = MathUtil.log2(chunkSize / pageSize);
        int nodeNum = 1 << (maxDepth + 1);
        int index = 1;
        this.pageSize = pageSize;
        this.chunkSize = chunkSize;
        this.maxDepth = maxDepth;
        this.pageSizeShift = MathUtil.log2(pageSize);
        this.pageSizeOverflowMask = ~(pageSize - 1);
        this.unusable = (byte) (maxDepth + 1);
        freeBytes = chunkSize;
        pageMap = new byte[nodeNum];
        depthMap = new byte[nodeNum];
        for (int i = 0; i <= maxDepth; i++) {
            int childNum = 1 << i;
            for (int j = 0; j < childNum; j++) {
                pageMap[index] = (byte) i;
                depthMap[index] = (byte) i;
                index++;
            }
        }
    }

    private int allocateNode(int depth) {
        int id = 1;
        int val = pageMap[id];
        //可搜索的最大数组下标
        int targetIndex = 1 << depth;
        if (val > depth) {
            //没有内存可分配
            return -1;
        }
        //如果节点有可分配的节点或者没到目标的层数
        while (val < depth || id < targetIndex) {
            //先看左节点
            id <<= 1;
            val = pageMap[id];
            if (val > depth) {
                //左节点已被分配，去右节点
                id ^= 1;
                val = pageMap[id];
            }
        }
        //设置节点已被使用并且更新父节点
        pageMap[id] = unusable;
        updateParentNodeAlloc(id);
        return id;
    }


    private void updateParentNodeAlloc(int id) {
        /**
         * 1.取出id对应节点的值和兄弟节点的值
         * 2.将父节点设置成两者最小值
         * 3.检查上一级节点
         */
        while (id > 1) {
            int parentId = id >>> 1;
            int val1 = pageMap[id];
            int val2 = pageMap[id ^ 1];
            int val = val1 < val2 ? val1 : val2;
            pageMap[parentId] = (byte) val;
            id = parentId;
        }
    }

    private void updateParentNodeFree(int id) {
        while(id > 1){
            int parentId = id >>> 1;
            int val1 = pageMap[id];
            int val2 = pageMap[id ^1];
            if(val1 == val1){
                pageMap[parentId] = depthMap[parentId];
            } else {
                int val = val1 < val2 ? val1:val2;
                pageMap[parentId] = (byte)val;
            }
            id = parentId;
        }
    }

    /**
     * 分配内存
     *
     * @param capacity 必须2^n
     * @return
     */
    public int malloc(int capacity) {
        if ((capacity & pageSizeOverflowMask) == 0) {
            //TODO 页内分配
        } else {
            int target = maxDepth - (MathUtil.log2(capacity) - pageSizeShift);
            int id = allocateNode(target);
            return id;
        }
        return -1;
    }

    public void free(int id) {
        pageMap[id] = depthMap[id];
        updateParentNodeFree(id);
    }


    protected void printTree() {
        System.out.println(Arrays.toString(pageMap));
    }

    public String toString() {
        return new StringBuffer()
                .append("Chunk size:")
                .append(chunkSize)
                .append(" bytes")
                .append(",pageSize:")
                .append(pageSize)
                .append(" bytes")
                .toString();
    }
}
