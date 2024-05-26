package com.wh.common.test;

/**
 * 雪花算法
 * <p>
 * 使用时根据实际需求调整数据中心ID和工作机器ID的最大值
 */
public class SnowFlake {

    // 开始时间截 (2023-01-01)
    private static final long START_TIMESTAMP = 1672531200000L;

    // 数据中心ID位数
    private static final long DATA_CENTER_ID_BITS = 5L;
    // 机器ID位数
    private static final long WORKER_ID_BITS = 5L;
    // 毫秒内序列号位数
    private static final long SEQUENCE_BITS = 12L;

    // 数据中心ID左移位数
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS;
    // 机器ID左移位数
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS + DATA_CENTER_ID_BITS;
    // 时间戳左移位数
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + DATA_CENTER_ID_BITS + WORKER_ID_BITS;

    // 生成序列的掩码，这里为4095 (0b111111111111=0xfff)
    private static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);

    // 工作机器ID(0~31)
    private long workerId;
    // 数据中心ID(0~31)
    private long dataCenterId;
    // 毫秒内序列(0~4095)
    private long sequence = 0L;
    // 上次生成ID的时间截
    private long lastTimestamp = -1L;

    public SnowFlake(long workerId, long dataCenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 获得下一个ID (线程安全)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    // 最大支持的工作机器ID
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    // 最大支持的数据中心ID
    private static final long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    public static void main(String[] args) {
        SnowFlake idWorker = new SnowFlake(0, 0);
        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.nextId());
        }
    }
}
