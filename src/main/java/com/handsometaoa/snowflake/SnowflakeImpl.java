package com.handsometaoa.snowflake;

import com.handsometaoa.IdGen;

import java.util.Random;

public class SnowflakeImpl implements IdGen {

    // 将-1左移12位，即将二进制数-1（全1）左移12位，再进行取反，得到的结果为00000000000000001111111111111111
    private static final long sequenceMask = ~(-1L << 12L);

    // 机器编码（初始化进行设置）
    private final int workId;
    // 自增序列
    private long sequence;
    // 记录上次获取ID的时间戳
    private long lastTimeStamp;
    private static final Random RANDOM = new Random();
    private static final long TIME_START = 1733323335000L;

    public SnowflakeImpl(int workId) {
        this.workId = workId;
    }

    @Override
    public synchronized Long getId() {
        long timeStamp = getNowTimeStamp();

        if (timeStamp < lastTimeStamp) {
            // 这里发生了时间回溯
            long offset = lastTimeStamp - timeStamp;
            if (offset < 5) {
                try {
                    wait(offset << 2);
                    timeStamp = getNowTimeStamp();
                    if (timeStamp < lastTimeStamp) {
                        return -1L;
                    }
                } catch (InterruptedException e) {
                    return -2L;
                }
            } else {
                return -3L;
            }
        }

        // 这里为什么不是else-if, 是因为上一步处理了时间回溯问题，还要继续进行获取ID
        if (timeStamp == lastTimeStamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timeStamp = getNextTimeStamp();
            }
        } else {
            sequence = RANDOM.nextInt(100);
        }

        lastTimeStamp = timeStamp;
        return (timeStamp - TIME_START) << 22 | (long) workId << 12 | sequence;
    }

    private long getNowTimeStamp() {
        return System.currentTimeMillis();
    }

    private long getNextTimeStamp() {
        long timestamp = getNowTimeStamp();
        while (lastTimeStamp >= timestamp) {
            timestamp = getNowTimeStamp();
        }
        return timestamp;
    }


}
