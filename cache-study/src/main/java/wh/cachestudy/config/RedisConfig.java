package wh.cachestudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 创建并配置RedisTemplate，用于操作Redis数据库。
     *
     * @param redisConnectionFactory Redis连接工厂，用于创建与Redis服务器的连接。
     * @return RedisTemplate<String, Object> 返回配置好的Redis模板对象，可以用于执行各种Redis操作。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置Redis连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置键的序列化方式为StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 设置值的序列化方式为StringRedisSerializer
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
