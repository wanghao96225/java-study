package com.wh.sharding.enums;
import java.util.*;

/**
 * @Author: zhangxq
 * @CreateTime: 2023-11-15  18:35
 * @Description:
 * @Version: 1.0
 */
public enum  ShardingTableCacheEnum {
    /**
     * 历史报警表
     */
    TEST_HISTORY("test_history", new HashSet<>()),
    /**
     * 实时报警表
     */
    TEST_REALTIME("test_realtime", new HashSet<>());

    /**
     * 逻辑表名
     */
    private final String logicTableName;
    /**
     * 实际表名
     */
    private final Set<String> resultTableNamesCache;

    private static Map<String, ShardingTableCacheEnum> valueMap = new HashMap<>();

    static {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(o -> valueMap.put(o.logicTableName, o));
    }

    ShardingTableCacheEnum(String logicTableName, Set<String> resultTableNamesCache) {
        this.logicTableName = logicTableName;
        this.resultTableNamesCache = resultTableNamesCache;
    }

    public static ShardingTableCacheEnum of(String value) {
        return valueMap.get(value);
    }

    public String logicTableName() {
        return logicTableName;
    }

    public Set<String> resultTableNamesCache() {
        return resultTableNamesCache;
    }

    public static Set<String> logicTableNames() {
        return valueMap.keySet();
    }

    @Override
    public String toString() {
        return "ShardingTableCacheEnum{" +
                "logicTableName='" + logicTableName + '\'' +
                ", resultTableNamesCache=" + resultTableNamesCache +
                '}';
    }
}
