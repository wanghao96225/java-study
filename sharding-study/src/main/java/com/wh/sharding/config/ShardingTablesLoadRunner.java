package com.wh.sharding.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
/**
 * @Author: zhangxq
 * @CreateTime: 2023-11-15  18:59
 * @Description: 项目启动后，读取已有分表，进行缓存
 * @Version: 1.0
 */
@Order(value = 1)
@Component
public class ShardingTablesLoadRunner implements CommandLineRunner{
    @Override
    public void run(String... args) {
        // 读取已有分表，进行缓存
        ShardingAlgorithmTool.tableNameCacheReloadAll();
    }
}
