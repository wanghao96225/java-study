package com.wh.cachestudy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {

    /**
     * 锁定时间
     *
     * @return 锁定时间 - 默认:5 单位:秒(TimeUnit)
     */
    int lockedTime() default 5;

    /**
     * 时间单位
     *
     * @return 锁定时间单位 - 默认:秒(TimeUnit)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
