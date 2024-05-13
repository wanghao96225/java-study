package wh.cachestudy.test;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wh.cachestudy.config.RedissonConfig;
import wh.cachestudy.util.RedisUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/testRedis")
public class TestRedis {

    private static final String KEY = "dailyActivity";

    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/testIncrByCommand")
    public boolean testIncrByCommand() {

        redisUtil.strIncrBy(KEY, 1);
        // 获取当前时间
        Instant now = Instant.now();
        // 获取明天0点时间
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Instant tomorrowStart = tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant();
        // 计算过期时间间隔
        Duration expireDuration = Duration.between(now, tomorrowStart);
        long expireSeconds = expireDuration.getSeconds();

        return redisUtil.kExpire(KEY, expireSeconds, TimeUnit.SECONDS);
    }

    @GetMapping("/testIncrByAdd/{source}")
    public long testIncrByAdd(@PathVariable long source) {
        // 验证source传入的参数必须为大于0的正整数
        if (source <= 0) {
            return 0;
        }
        return redisUtil.strIncrBy(KEY, source);
    }

    @GetMapping("/testIncrByReduce/{source}")
    public long testIncrByReduce(@PathVariable long source) {
        // 验证source传入的参数必须为大于0的正整数
        if (source <= 0) {
            return 0;
        }
        return redisUtil.strIncrBy(KEY, -source);
    }
}
