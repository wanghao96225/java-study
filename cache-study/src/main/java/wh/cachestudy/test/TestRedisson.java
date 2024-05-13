package wh.cachestudy.test;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wh.cachestudy.config.RedissonConfig;
import wh.cachestudy.util.RedisUtil;
import wh.cachestudy.util.RedissonUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/testRedisson")
public class TestRedisson {

    private static final String KEY = "testRedisson";

    //锁过期时间
    private static final Long LOCK_KEY_TIME = 120L;

    @Resource
    private RedissonConfig redissonConfig;

    @GetMapping("/doJobTask")
    public void doJobTask() {
        //定时任务执行周期较短，为防止数据重复修改，加入锁
        RLock lock = redissonConfig.initBean().getLock("your_task_name");
        // 尝试获取锁并设定锁的过期时间
        boolean acquired = false;
        try {
            //获取锁
            acquired = lock.tryLock(0, LOCK_KEY_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("取锁失败");
        }
        if (acquired) {
            try {
                // 执行业务逻辑

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
        }
    }
}
