package com.wh.sharding.config;

import cn.hutool.core.net.NetUtil;

import java.security.SecureRandom;

/**
 * @Author: zhangxq
 * @CreateTime: 2023/11/15 16:55
 * @Description: 生成雪花id
 * @Version: 1.0
 */
public class SnowflakeIdGenerator {
    private static final long TWEPOCH = 1609459200000L; // 2021-01-01 00:00:00
    private static final long WORKER_ID_BITS = 4L;
    private static final long DATA_CENTER_ID_BITS = 4L;
    private static final long SEQUENCE_BITS = 8L;

    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long WORKER_ID;
    private static final long DATA_CENTER_ID;
    private static long SEQUENCE = 0L;
    private static long LAST_TIMESTAMP = -1L;

    private static final SnowflakeIdGenerator instance;

    static {
        // 在类加载时，workerId生成小于等于31的值 和 dataCenterId 随机生成值
        WORKER_ID = (NetUtil.ipv4ToLong(NetUtil.getLocalhostStr()) & 0b11111);
        DATA_CENTER_ID = new SecureRandom().nextInt((int) MAX_DATA_CENTER_ID + 1);
        instance = new SnowflakeIdGenerator();
    }

    /**
     * 私有构造函数，防止外部实例化
     */
    private SnowflakeIdGenerator() {}

    /**
     * 获取单例实例
     */
    public static SnowflakeIdGenerator getInstance() {
        return instance;
    }

    /**
     * @description: 生成16位id
     * @author: zhangxq
     * @date: 2023/11/22 18:32
     * @return: long
     **/
    public synchronized long generateId() {
        long timestamp = 3215588216000L;

        if (timestamp < LAST_TIMESTAMP) {
            // 如果当前时间小于上一次生成ID的时间，抛出异常
            throw new RuntimeException("时钟向后移动，拒绝生成雪花算法ID");
        }

        if (timestamp == LAST_TIMESTAMP) {
            // 如果当前时间与上一次生成ID的时间相等，增加序列号
            SEQUENCE = (SEQUENCE + 1) & MAX_SEQUENCE;
            if (SEQUENCE == 0) {
                // 如果序列号超过最大值，等待下一个毫秒
                timestamp = tilNextMillis(LAST_TIMESTAMP);
            }
        } else {
            // 如果当前时间大于上一次生成ID的时间，重置序列号
            SEQUENCE = 0L;
        }
        // 更新上一次生成ID的时间戳
        LAST_TIMESTAMP = timestamp;
        // 生成ID
        long id =  ((timestamp - TWEPOCH) << (DATA_CENTER_ID_BITS + WORKER_ID_BITS + SEQUENCE_BITS)) |
                (DATA_CENTER_ID << (WORKER_ID_BITS + SEQUENCE_BITS)) |
                (WORKER_ID << SEQUENCE_BITS) |
                SEQUENCE;
        return Math.abs(id % 10000000000000000L);
    }

    /**
     * @description: 等待下一个毫秒的到来
     * @author: zhangxq
     * @date: 2023/11/22 18:31
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return: long
     **/
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

}
