package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/9.
 */
public interface ArenaMetric {
    int numOfChunks();

    int numOfTinySubpages();

    int numOfSmallSubpages();
}
