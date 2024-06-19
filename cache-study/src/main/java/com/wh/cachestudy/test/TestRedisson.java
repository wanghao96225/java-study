package com.wh.cachestudy.test;

import com.wh.cachestudy.annotations.RepeatSubmit;
import com.wh.cachestudy.config.RedissonConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/testRedisson")
public class TestRedisson {

    private static final String KEY = "testRedisson";

    //锁过期时间
    private static final Long LOCK_KEY_TIME = 10L;

    @Resource
    private RedissonConfig redissonConfig;

    @GetMapping("/testPipelined")
    @RepeatSubmit(lockedTime = 30, timeUnit = TimeUnit.SECONDS)
    public void testPipelined() {

        RBatch batch = redissonConfig.initBean().createBatch();
        batch.getMap(KEY).putAsync("key-testPipelined","value-testPipelined");
        batch.getMap(KEY).putAsync("key-testPipelined-2","value-testPipelined-2");
        batch.execute();

    }

    @GetMapping("/doJobTask")
    public void doJobTask() {
        //定时任务执行周期较短，为防止数据重复修改，加入锁
        RLock lock = redissonConfig.initBean().getLock("your_task_name");
        // 尝试获取锁并设定锁的过期时间
        boolean acquired = false;
        try {
            //获取锁 尝试加锁，最多等待100秒，锁定之后10秒自动解锁
            acquired = lock.tryLock(100, LOCK_KEY_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("取锁失败");
        }
        if (acquired) {
            try {
                // 执行业务逻辑
                log.info("Lock acquired");
            }catch (Exception e) {
                log.error("处理失败");
                //业务异常处理逻辑
            }finally {
                // 释放锁
                lock.unlock();
            }
        } else {
            // 获取锁失败，说明有其他线程或进程正在处理数据
            // 可以进行重试或触发告警机制
            log.warn("Failed to acquire lock, indicating another thread or process is handling the data. Retry or trigger an alert mechanism.");
        }
    }
}
