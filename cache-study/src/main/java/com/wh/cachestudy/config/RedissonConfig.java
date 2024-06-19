package com.wh.cachestudy.config;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private String port;
//    @Value("${spring.data.redis.password}")
//    private String password;

//    @Value("${spring.data.redis.sentinel.nodes}")
//    private String sentinel;
//    @Value("${spring.data.redis.sentinel.master}")
//    private String masterName;
//
//    @Value("${spring.data.redis.cluster.nodes}")
//    private String cluster;


    private int timeout = 2000;
    private int scanInterval = 60000;
    private static String ADDRESS_PREFIX = "redis://";

    /**
     * 单机模式
     */
    @Bean
    public RedissonClient initBean() {
        // 哨兵模式
//        if (StringUtils.isNotBlank(sentinel)) {
//            log.info("redis is sentinel mode");
//            return redissonSentinel();
//        }
//        // 集群模式
//        if (StringUtils.isNotBlank(cluster)) {
//            log.info("redis is cluster mode");
//            return redissonCluster();
//        }
        // 单机模式
        if (StringUtils.isNotBlank(host)) {
            log.info("redis is single mode");
            return redissonSingle();
        }

        log.error("redisson config can not support this redis mode");
        return null;
    }

    /**
     * 单机模式
     */
    private RedissonClient redissonSingle() {
        Config config = new Config();
        String address = ADDRESS_PREFIX + host + ":" + port;
        //设置
        config.setCodec(new StringCodec())
                //这是用的集群server
                .useSingleServer()
                .setAddress(address)
                .setTimeout(timeout);
//                .setPassword(password);
        return Redisson.create(config);
    }


    /**
     * 哨兵模式
     */
//    private RedissonClient redissonSentinel() {
//
//        String[] nodes = sentinel.split(",");
//        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = ADDRESS_PREFIX + nodes[i];
//        }
//        Config config = new Config();
//        //设置
//        config.setCodec(new StringCodec())
//                .useSentinelServers()
//                .setMasterName(masterName)
////                .setPassword(password)
//                .setTimeout(timeout)
//                .addSentinelAddress(nodes)
//                // 在Redisson启动期间启用sentinels列表检查,默认为true,这里我们设置为false,不检查
//                .setCheckSentinelsList(false);
//        return Redisson.create(config);
//    }

    /**
     * 集群模式
     */
//    private RedissonClient redissonCluster() {
//        String[] nodes = cluster.split(",");
//        //redisson版本是3.5，集群的ip前面要加上“redis://”，不然会报错，3.2版本可不加
//        for (int i = 0; i < nodes.length; i++) {
//            nodes[i] = ADDRESS_PREFIX + nodes[i];
//        }
//        Config config = new Config();
//        //设置
//        config.setCodec(new StringCodec())
//                //这是用的集群server
//                .useClusterServers()
//                //设置集群状态扫描时间
//                .setScanInterval(scanInterval)
//                .addNodeAddress(nodes)
////                .setPassword(password)
//                .setReadMode(ReadMode.MASTER);
//        return Redisson.create(config);
//    }
}
