package com.taurus.memory;

/**
 * Created by ynfeng on 2016/11/4.
 */
public interface ChunkListMetric {
    int numOf0to24PercentUsage();

    int numOf25to49PercentUsage();

    int numOf50to74PercentUsage();

    int numOf75to100PercentUsage();

    int totalChunkNum();
}
