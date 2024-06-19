package com.wh.cachestudy.aspect;

import com.wh.cachestudy.annotations.RepeatSubmit;
import com.wh.cachestudy.config.RedissonConfig;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    // 将RedissonClient实例化为静态成员变量，避免重复初始化
    @Resource
    private RedissonConfig redissonConfig;

    @Pointcut("@annotation(com.wh.cachestudy.annotations.RepeatSubmit)")
    private void pointcut() {}

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {

            RedissonClient redissonClient = redissonConfig.initBean();

            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = signature.getMethod();
            RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);

            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Objects.requireNonNull(servletRequestAttributes); // 已经通过getRequestAttributes()获取，不需要再次assert
            HttpServletRequest request = servletRequestAttributes.getRequest();
            // 使用更安全的组合方式生成key，这里示例仅提供了一种思路，具体实现应根据实际情况调整
            String safeKey = generateSafeKey(request.getSession().getId(), request.getServletPath());
            RBucket<String> bucket = redissonClient.getBucket(safeKey);

            if (bucket.get() == null) {
                Object result = proceedingJoinPoint.proceed();
                RBatch batch = redissonClient.createBatch();
                batch.getBucket(safeKey).setAsync(request.getRequestURI(), repeatSubmit.lockedTime(), repeatSubmit.timeUnit()); // 假设lockedTime为5秒，timeUnit为SECONDS
                batch.execute(); // 确保异步操作执行完成
                log.info("请求的sessionId：{}，请求URI：{}",safeKey, request.getRequestURI());
                return result;
            } else {
                log.error("重复提交");
                return "请勿短时间内重复操作";
            }
        } catch (Throwable e) {
            log.error("验证重复提交时出现异常!", e);
            return "验证重复提交时出现未知异常!";
        }
    }

    // 生成更安全的key
    private String generateSafeKey(String sessionId, String servletPath) {
        // 示例：将sessionId和servletPath进行哈希或者其他形式的安全处理
        // 注意：具体实现应根据安全需求设计，这里仅提供一种简单的组合方式，实际应用中可能需要更复杂的安全策略
        return sessionId + "-" + servletPath;
    }
}
