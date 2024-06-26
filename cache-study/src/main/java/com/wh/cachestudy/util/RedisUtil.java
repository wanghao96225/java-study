package com.wh.cachestudy.util;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /*
        Kye相关操作
     */

    /**
     * 根据key, 删除redis中的对应key-value
     * <p>
     * 注: 若删除失败, 则返回false。
     * <p>
     * 若redis中，不存在该key, 那么返回的也是false。
     * 所以，不能因为返回了false,就认为redis中一定还存
     * 在该key对应的key-value。
     *
     * @param key 要删除的key
     * @return 删除是否成功
     */
    public boolean kDelete(String key) {
        log.info("kDelete(...) => key -> {}", key);
        // 返回值只可能为true/false, 不可能为null
        Boolean result = redisTemplate.delete(key);
        log.info("kDelete(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 根据keys, 批量删除key-value
     * <p>
     * 注: 若redis中，不存在对应的key, 那么计数不会加1, 即:
     * redis中存在的key-value里，有名为a1、a2的key，
     * 删除时，传的集合是a1、a2、a3，那么返回结果为2。
     *
     * @param keys 要删除的key集合
     * @return 删除了的key-value个数
     */
    public long kDelete(Collection<String> keys) {
        log.info("kDelete(...) => keys -> {}", keys);
        Long count = redisTemplate.delete(keys);
        log.info("kDelete(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 将key对应的value值进行序列化(二进制)，并返回序列化后的value值。
     * <p>
     * 注: 若不存在对应的key, 则返回null。
     * 注: dump时，并不会删除redis中的对应key-value。
     * 注: dump功能与restore相反。
     *
     * @param key 要序列化的value的key
     * @return 序列化后的value值
     */
    public byte[] kDump(String key) {
        log.info("kDump(...) =>key -> {}", key);
        byte[] result = redisTemplate.dump(key);
        log.info("kDump(...) => result -> {}", result);
        return result;
    }

    /**
     * 将给定的value值，反序列化到redis中, 形成新的key-value。
     *
     * @param key        value对应的key
     * @param value      要反序列(二进制)的value值。
     *                   注: 这个值可以由{@link this#kDump(String)}获得
     * @param timeToLive 反序列化后的key-value的存活时长
     * @param unit       timeToLive的单位
     * @throws RedisSystemException 如果redis中已存在同样的key时，抛出此异常
     */
    public void kRestore(String key, byte[] value, long timeToLive, TimeUnit unit) {
        kRestore(key, value, timeToLive, unit, false);
    }

    /**
     * 将给定的value值，反序列化到redis中, 形成新的key-value。
     *
     * @param key     value对应的key
     * @param value   要反序列(二进制)的value值。
     *                注: 这个值可以由{@link this#kDump(String)}获得
     * @param timeout 反序列化后的key-value的存活时长
     * @param unit    timeout的单位
     * @param replace 若redis中已经存在了相同的key, 是否替代原来的key-value
     * @throws RedisSystemException 如果redis中已存在同样的key, 且replace为false时，抛出此异常
     */
    public void kRestore(String key, byte[] value, long timeout, TimeUnit unit, boolean replace) {
        log.info("kRestore(...) => key -> {}, value -> {}, timeout -> {}, unit -> {}, replace -> {}",
                key, value, timeout, unit, replace);
        redisTemplate.restore(key, value, timeout, unit, replace);
    }

    /**
     * redis中是否存在,指定key的key-value
     *
     * @param key 指定的key
     * @return 是否存在对应的key-value
     */
    public boolean kHasKey(String key) {
        log.info("kHasKey(...) => key -> {}", key);
        Boolean result = redisTemplate.hasKey(key);
        log.info("kHasKey(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 给指定的key对应的key-value设置: 多久过时
     * <p>
     * 注:过时后，redis会自动删除对应的key-value。
     * 注:若key不存在，那么也会返回false。
     *
     * @param key     指定的key
     * @param timeout 过时时间
     * @param unit    timeout的单位
     * @return 操作是否成功
     */
    public boolean kExpire(String key, long timeout, TimeUnit unit) {
        log.info("kExpire(...) => key -> {}, timeout -> {}, unit -> {}", key, timeout, unit);
        Boolean result = redisTemplate.expire(key, timeout, unit);
        log.info("kExpire(...) => result is -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 给指定的key对应的key-value设置: 什么时候过时
     * <p>
     * 注:过时后，redis会自动删除对应的key-value。
     * 注:若key不存在，那么也会返回false。
     *
     * @param key  指定的key
     * @param date 什么时候过时
     * @return 操作是否成功
     */
    public boolean kExpireAt(String key, Date date) {
        log.info("kExpireAt(...) => key -> {}, date -> {}", key, date);
        Boolean result = redisTemplate.expireAt(key, date);
        log.info("kExpireAt(...) => result is -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 移除key对应的key-value的过期时间, 使该key-value一直存在
     * <p>
     * 注: 若key对应的key-value，本身就是一直存在(无过期时间的)， 那么persist方法会返回false;
     * 若没有key对应的key-value存在，本那么persist方法会返回false;
     *
     * @param key 定位key-value的key
     * @return 操作是否成功
     */
    public boolean kPersist(String key) {
        log.info("kPersist(...) => key -> {}", key);
        Boolean result = redisTemplate.persist(key);
        log.info("kPersist(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取key对应的key-value的过期时间
     * <p>
     * 注: 若key-value永不过期， 那么返回的为-1。
     * 注: 若不存在key对应的key-value， 那么返回的为-2
     * 注:若存在零碎时间不足1 SECONDS,则(大体上)四舍五入到SECONDS级别。
     *
     * @param key 定位key-value的key
     * @return 过期时间(单位s)
     */
    public long kGetExpire(String key) {
        return kGetExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 获取key对应的key-value的过期时间
     * <p>
     * 注: 若key-value永不过期， 那么返回的为-1。
     * 注: 若不存在key对应的key-value， 那么返回的为-2
     * 注:若存在零碎时间不足1 unit,则(大体上)四舍五入到unit别。
     *
     * @param key 定位key-value的key
     * @return 过期时间(单位unit)
     */
    public long kGetExpire(String key, TimeUnit unit) {
        log.info("kGetExpire(...) =>key -> {}, unit is -> {}", key, unit);
        Long result = redisTemplate.getExpire(key, unit);
        log.info("kGetExpire(...) => result ->  {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 找到所有匹配pattern的key,并返回该key的结合.
     * <p>
     * <b>
     * <font color="red">
     * 提示:若redis中键值对较多，此方法耗时相对较长，慎用！慎用！慎用！
     * </font>
     * </b>
     *
     * @param pattern 匹配模板。
     *                注: 常用的通配符有:
     *                ?    有且只有一个;
     *                *         >=0个;
     * @return 匹配pattern的key的集合。 可能为null。
     */
    public Set<String> keys(String pattern) {
        log.info("keys(...) => pattern -> {}", pattern);
        Set<String> keys = redisTemplate.keys(pattern);
        log.info("keys(...) => keys -> {}", keys);
        return keys;
    }

    /**
     * 将当前数据库中的key对应的key-value,移动到对应位置的数据库中。
     * <p>
     * <b>
     * <font color="red">
     * 注意: 在Redis Cluster中，由于数据分片存储在不同的节点上，move操作并不适用，因为Redis Cluster不允许跨节点移动键。
     * Redis Cluster通过哈希槽（hash slots）来分配键到不同的节点，每个节点负责一定范围的哈希槽。
     * </font>
     * </b>
     * <p>
     * 注:单机版的redis,默认将存储分为16个db, index为0 到 15。
     * 注:同一个db下，key唯一； 但是在不同db中，key可以相同。
     * 注:若目标db下，已存在相同的key, 那么move会失败，返回false。
     *
     * @param key     定位要移动的key-value的key
     * @param dbIndex 要移动到哪个db
     * @return 移动是否成功。
     * 注: 若目标db下，已存在相同的key, 那么move会失败，返回false。
     */
    public boolean kMove(String key, int dbIndex) {
        log.info("kMove(...) => key  -> {}, dbIndex -> {}", key, dbIndex);
        Boolean result = redisTemplate.move(key, dbIndex);
        log.info("kMove(...) =>result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 从redis的所有key中，随机获取一个key
     * <p>
     * 注: 若redis中不存在任何key-value, 那么这里返回null
     *
     * @return 随机获取到的一个key
     */
    public String kRandomKey() {
        String result = redisTemplate.randomKey();
        log.info("kRandomKey(...) => result is -> {}", result);
        return result;
    }

    /**
     * 重命名对应的oldKey为新的newKey
     * <p>
     * 注: 若oldKey不存在， 则会抛出异常.
     * 注: 若redis中已存在与newKey一样的key,
     * 那么原key-value会被丢弃，
     * 只留下新的key，以及原来的value
     * 示例说明: 假设redis中已有 (keyAlpha, valueAlpha) 和 (keyBeta, valueBeat),
     * 在使用rename(keyAlpha, keyBeta)替换后, redis中只会剩下(keyBeta, valueAlpha)
     *
     * @param oldKey 旧的key
     * @param newKey 新的key
     * @throws RedisSystemException 若oldKey不存在时， 抛出此异常
     */
    public void kRename(String oldKey, String newKey) {
        log.info("kRename(...) => oldKey -> {}, newKey -> {}", oldKey, newKey);
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 当redis中不存在newKey时, 重命名对应的oldKey为新的newKey。
     * 否则不进行重命名操作。
     * <p>
     * 注: 若oldKey不存在， 则会抛出异常.
     *
     * @param oldKey 旧的key
     * @param newKey 新的key
     * @throws RedisSystemException 若oldKey不存在时， 抛出此异常
     */
    public boolean kRenameIfAbsent(String oldKey, String newKey) {
        log.info("kRenameIfAbsent(...) => oldKey -> {}, newKey -> {}", oldKey, newKey);
        Boolean result = redisTemplate.renameIfAbsent(oldKey, newKey);
        log.info("kRenameIfAbsent(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取key对应的value的数据类型
     * <p>
     * 注: 若redis中不存在该key对应的key-value， 那么这里返回NONE。
     *
     * @param key 用于定位的key
     * @return key对应的value的数据类型
     */
    public DataType kType(String key) {
        log.info("kType(...) => key -> {}", key);
        DataType result = redisTemplate.type(key);
        log.info("kType(...) => result -> {}", result);
        return result;
    }

    /*
        String类型操作
     */

    /**
     * 设置key-value
     * <p>
     * 注: 若已存在相同的key, 那么原来的key-value会被丢弃。
     *
     * @param key   key
     * @param value key对应的value
     */
    public void strSet(String key, String value) {
        log.info("strSet(...) => key -> {}, value -> {}", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置key-value
     * <p>
     * 注: 若已存在相同的key, 那么原来的key-value会被丢弃
     *
     * @param key     key
     * @param value   key对应的value
     * @param timeout 过时时长
     * @param unit    timeout的单位
     */
    public void strSetEx(String key, String value, long timeout, TimeUnit unit) {
        log.info("strSetEx(...) => key -> {}, value -> {}, timeout -> {}, unit -> {}",
                key, value, timeout, unit);
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 若不存在key时, 向redis中添加key-value, 返回成功/失败。
     * 若存在，则不作任何操作, 返回false。
     *
     * @param key   key
     * @param value key对应的value
     * @return set是否成功
     */
    public boolean strSetIfAbsent(String key, String value) {
        log.info("strSetIfAbsent(...) => key -> {}, value -> {}", key, value);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
        log.info("strSetIfAbsent(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 若不存在key时, 向redis中添加一个(具有超时时长的)key-value, 返回成功/失败。
     * 若存在，则不作任何操作, 返回false。
     *
     * @param key     key
     * @param value   key对应的value
     * @param timeout 超时时长
     * @param unit    timeout的单位
     * @return set是否成功
     */
    public boolean strSetIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        log.info("strSetIfAbsent(...) => key -> {}, value -> {}, key -> {}, value -> {}", key, value, timeout, unit);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        log.info("strSetIfAbsent(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 根据key，获取到对应的value值
     *
     * @param key key-value对应的key
     * @return 该key对应的值。
     * 注: 若key不存在， 则返回null。
     */
    public String strGet(String key) {
        log.info("strGet(...) => key -> {}", key);
        String result = redisTemplate.opsForValue().get(key);
        log.info("strGet(...) => result -> {} ", result);
        return result;
    }

    /**
     * 处理redis中key对应的value值, 将第offset位的值, 设置为1或0。
     * <p>
     * <b>
     * 说明: 在redis中，存储的字符串都是以二进制形式存在的; 如存储的key-value里，值为abc,实际上，
     * 在redis里面存储的是 01100001 01100010 01100011,前8为对应a,中间8为对应b,后面8位对应c。
     * 示例：这里如果setBit(key, 6, true)的话，就是将索引位置6的那个数，设置值为1，
     * 值就变成了 01100011 01100010 01100011
     * 追注:offset即index,从0开始。
     * </b>
     * <p>
     * 注: 参数value为true, 则设置为1；参数value为false, 则设置为0。
     * <p>
     * 注: 若redis中不存在对应的key,那么会自动创建新的。
     * 注: offset可以超过value在二进制下的索引长度。
     *
     * @param key    定位value的key
     * @param offset 要改变的bit的索引
     * @param value  改为1或0, true - 改为1, false - 改为0
     * @return set是否成功
     */
    public boolean strSetBit(String key, long offset, boolean value) {
        log.info("strSetBit(...) => key -> {}, offset -> {}, value -> {}", key, offset, value);
        Boolean result = redisTemplate.opsForValue().setBit(key, offset, value);
        log.info("strSetBit(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取(key对应的)value在二进制下，offset位置的bit值。
     * <p>
     * 注: 当offset的值在(二进制下的value的)索引范围外时, 返回的也是false。
     * <p>
     * 示例:
     * RedisUtil.set("akey", "a");
     * 字符串a, 转换为二进制为01100001
     * 那么getBit("akey", 6)获取到的结果为false。
     *
     * @param key    定位value的key
     * @param offset 定位bit的索引
     * @return offset位置对应的bit的值(true - 1, false - 0)
     */
    public boolean strGetBit(String key, long offset) {
        log.info("strGetBit(...) => key -> {}, offset -> {}", key, offset);
        Boolean result = redisTemplate.opsForValue().getBit(key, offset);
        log.info("strGetBit(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 批量设置 key-value
     * <p>
     * 注: 若存在相同的key, 则原来的key-value会被覆盖。
     *
     * @param maps key-value 集
     */
    public void strMultiSet(Map<String, String> maps) {
        log.info("strMultiSet(...) => maps -> {}", maps);
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 当redis中,不存在任何一个keys时, 才批量设置 key-value, 并返回成功/失败.
     * 否则，不进行任何操作, 并返回false。
     * <p>
     * 即: 假设调用此方法时传入的参数map是这样的: {k1=v1, k2=v2, k3=v3}
     * 那么redis中, k1、k2、k3都不存在时,才会批量设置key-value;
     * 否则不会设置任何key-value。
     * <p>
     * 注: 只有当 map 中的每个键在 Redis 中都不存在时，才会将这些键值对存入 Redis。
     * <p>
     * 注: 如果 map 中任何一个键在 Redis 中已经存在，此方法将不做任何操作，
     * 所有键值对都不会被设置，整个批量操作被视为失败。
     *
     * @param maps key-value 集
     * @return 操作是否成功
     */
    public boolean strMultiSetIfAbsent(Map<String, String> maps) {
        log.info("strMultiSetIfAbsent(...) => maps -> {}", maps);
        Boolean result = redisTemplate.opsForValue().multiSetIfAbsent(maps);
        log.info("strMultiSetIfAbsent(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 批量获取value值
     * <p>
     * 注: 若redis中，对应的key不存在，那么该key对应的返回的value值为null
     *
     * @param keys key集
     * @return value值集合
     */
    public List<String> strMultiGet(Collection<String> keys) {
        log.info("strMultiGet(...) => keys -> {}", keys);
        List<String> result = redisTemplate.opsForValue().multiGet(keys);
        log.info("strMultiGet(...) => result -> {}", result);
        return result;
    }

    /**
     * 从(redis中key对应的)value的offset位置起(包含该位置),用replaceValue替换对应长度的值。
     * <p>
     * 举例说明:
     * 1.假设redis中存在key-value ("ds", "0123456789"); 调
     * 用setRange("ds", "abcdefghijk", 3)后， redis中该value值就变为了[012abcdefghijk]
     * <p>
     * 2.假设redis中存在key-value ("jd", "0123456789");调
     * 用setRange("jd", "xyz", 3)后， redis中该value值就变为了[012xyz6789]
     * <p>
     * 3.假设redis中存在key-value ("ey", "0123456789");调
     * 用setRange("ey", "qwer", 15)后， redis中该value值就变为了[0123456789     qwer]
     * 注:case3比较特殊，offset超过了原value的长度了, 中间就会有一些空格来填充，但是如果在程序
     * 中直接输出的话，中间那部分空格可能会出现乱码。
     *
     * @param key          定位key-value的key
     * @param replaceValue 要替换的值
     * @param offset       起始位置
     */
    public void strSetRange(String key, String replaceValue, long offset) {
        log.info("strSetRange(...) => key -> {}, replaceValue -> {}, offset -> {}", key, replaceValue, offset);
        redisTemplate.opsForValue().set(key, replaceValue, offset);
    }

    /**
     * 对(key对应的)value进行截取, 截取范围为[start, end]
     * <p>
     * 注: 若[start, end]的范围不在value的范围中，那么返回的是空字符串 ""
     * 注: 若value只有一部分在[start, end]的范围中，那么返回的是value对应部分的内容(即:不足的地方，并不会以空来填充)
     *
     * @param key   定位value的key
     * @param start 起始位置 (从0开始)
     * @param end   结尾位置 (从0开始)
     * @return 截取后的字符串
     */
    public String strGetRange(String key, long start, long end) {
        log.info("strGetRange(...) => kry -> {}", key);
        String result = redisTemplate.opsForValue().get(key, start, end);
        log.info("strGetRange(...) => result -> {} ", result);
        return result;
    }

    /**
     * 追加值到末尾
     * <p>
     * 注: 当redis中原本不存在key时,那么（从效果上来看）此方法就等价于{@link this#strSet(String, String)}
     *
     * @param key   定位value的key
     * @param value 要追加的value值
     * @return 追加后， 整个value的长度
     */
    public int strAppend(String key, String value) {
        log.info("strAppend(...) => key -> {}, value -> {}", key, value);
        Integer result = redisTemplate.opsForValue().append(key, value);
        log.info("strAppend(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取到key对应的value的长度。
     * <p>
     * 注: 长度等于{@link String#length}。
     * 注: 若redis中不存在对应的key-value, 则返回值为0.
     *
     * @param key 定位value的key
     * @return value的长度
     */
    public long strSize(String key) {
        log.info("strSize(...) => key -> {}", key);
        Long result = redisTemplate.opsForValue().size(key);
        log.info("strSize(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 增/减 整数
     * <p>
     * 注: 负数则为减。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     * 抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key       用于定位value的key
     * @param increment 增加多少
     * @return 增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    public long strIncrBy(String key, long increment) {
        log.info("strIncrBy(...) => key -> {}, increment -> {}", key, increment);
        Long result = redisTemplate.opsForValue().increment(key, increment);
        log.info("strIncrBy(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 增/减 浮点数
     * <p>
     * 注: 慎用浮点数，会有精度问题。
     * 如: 先 RedisUtil.set("ds", "123");
     * 然后再RedisUtil.incrByFloat("ds", 100.6);
     * 就会看到精度问题。
     * 注: 负数则为减。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     * 抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key       用于定位value的key
     * @param increment 增加多少
     * @return 增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    public double strIncrByFloat(String key, double increment) {
        log.info("strIncrByFloat(...) => key -> {}, increment -> {}", key, increment);
        Double result = redisTemplate.opsForValue().increment(key, increment);
        log.info("strIncrByFloat(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 给指定key设置新的value, 并返回旧的value
     * <p>
     * 注: 若redis中不存在key, 那么此操作仍然可以成功， 不过返回的旧值是null
     *
     * @param key      定位value的key
     * @param newValue 要为该key设置的新的value值
     * @return 旧的value值
     */
    public String strGetAndSet(String key, String newValue) {
        log.info("strGetAndSet(...) => key -> {}, value -> {}", key, newValue);
        String oldValue = redisTemplate.opsForValue().getAndSet(key, newValue);
        log.info("strGetAndSet(...) => oldValue -> {}", oldValue);
        return oldValue;
    }

    /*
        Hash类型相关操作 可以将redis中Hash的数据结构看作是 Map<String, Map<HK, HV>>
     */

    /**
     * 向key对应的hash中，增加一个键值对entryKey-entryValue
     * <p>
     * 注: 同一个hash里面，若已存在相同的entryKey， 那么此操作将丢弃原来的entryKey-entryValue，
     * 而使用新的entryKey-entryValue。
     *
     * @param key        定位hash的key
     * @param entryKey   要向hash中增加的键值对里的 键
     * @param entryValue 要向hash中增加的键值对里的 值
     */
    public void hPut(String key, String entryKey, String entryValue) {
        log.info("hPut(...) => key -> {}, entryKey -> {}, entryValue -> {}", key, entryKey, entryValue);
        redisTemplate.opsForHash().put(key, entryKey, entryValue);
    }

    /**
     * 向key对应的hash中，增加maps(即: 批量增加entry集)
     * <p>
     * 注: 同一个hash里面，若已存在相同的entryKey， 那么此操作将丢弃原来的entryKey-entryValue，
     * 而使用新的entryKey-entryValue
     *
     * @param key  定位hash的key
     * @param maps 要向hash中增加的键值对集
     */
    public void hPutAll(String key, Map<String, String> maps) {
        log.info("hPutAll(...) => key -> {}, maps -> {}", key, maps);
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 当key对应的hash中,不存在entryKey时，才(向key对应的hash中，)增加entryKey-entryValue
     * 否者，不进行任何操作
     *
     * @param key        定位hash的key
     * @param entryKey   要向hash中增加的键值对里的 键
     * @param entryValue 要向hash中增加的键值对里的 值
     * @return 操作是否成功。
     */
    public boolean hPutIfAbsent(String key, String entryKey, String entryValue) {
        log.info("hPutIfAbsent(...) => key -> {}, entryKey -> {}, entryValue -> {}",
                key, entryKey, entryValue);
        Boolean result = redisTemplate.opsForHash().putIfAbsent(key, entryKey, entryValue);
        log.info("hPutIfAbsent(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取到key对应的hash里面的对应字段的值
     * <p>
     * 注: 若redis中不存在对应的key, 则返回null。
     * 若key对应的hash中不存在对应的entryKey, 也会返回null。
     *
     * @param key      定位hash的key
     * @param entryKey 定位hash里面的entryValue的entryKey
     * @return key对应的hash里的entryKey对应的entryValue值
     */
    public Object hGet(String key, String entryKey) {
        log.info("hGet(...) => key -> {}, entryKey -> {}", key, entryKey);
        Object entryValue = redisTemplate.opsForHash().get(key, entryKey);
        log.info("hGet(...) => entryValue -> {}", entryValue);
        return entryValue;
    }

    /**
     * 获取到key对应的hash(即: 获取到key对应的Map<HK, HV>)
     * <p>
     * 注: 若redis中不存在对应的key, 则返回一个没有任何entry的空的Map(，而不是返回null)。
     *
     * @param key 定位hash的key
     * @return key对应的hash。
     */
    public Map<Object, Object> hGetAll(String key) {
        log.info("hGetAll(...) => key -> {}", key);
        Map<Object, Object> result = redisTemplate.opsForHash().entries(key);
        log.info("hGetAll(...) => result -> {}", result);
        return result;
    }

    /**
     * 批量获取(key对应的)hash中的entryKey的entryValue
     * <p>
     * 注: 若hash中对应的entryKey不存在，那么返回的对应的entryValue值为null
     * 注: redis中key不存在，那么返回的List中，每个元素都为null。
     * 追注: 这个List本身不为null, size也不为0， 只是每个list中的每个元素为null而已。
     *
     * @param key       定位hash的key
     * @param entryKeys 需要获取的hash中的字段集
     * @return hash中对应entryKeys的对应entryValue集
     */
    public List<Object> hMultiGet(String key, Collection<Object> entryKeys) {
        log.info("hMultiGet(...) => key -> {}, entryKeys -> {}", key, entryKeys);
        List<Object> entryValues = redisTemplate.opsForHash().multiGet(key, entryKeys);
        log.info("hMultiGet(...) => entryValues -> {}", entryValues);
        return entryValues;
    }

    /**
     * (批量)删除(key对应的)hash中的对应entryKey-entryValue
     * <p>
     * 注: 1、若redis中不存在对应的key, 则返回0;
     * 2、若要删除的entryKey，在key对应的hash中不存在，在count不会+1, 如:
     * RedisUtil.hPut("ds", "name", "邓沙利文");
     * RedisUtil.hPut("ds", "birthday", "1994-02-05");
     * RedisUtil.hPut("ds", "hobby", "女");
     * 则调用RedisUtil.hDelete("ds", "name", "birthday", "hobby", "non-exist-entryKey")
     * 的返回结果为3
     * 注: 若(key对应的)hash中的所有entry都被删除了，那么该key也会被删除
     *
     * @param key       定位hash的key
     * @param entryKeys 定位要删除的entryKey-entryValue的entryKey
     * @return 删除了对应hash中多少个entry
     */
    public long hDelete(String key, Object... entryKeys) {
        log.info("hDelete(...) => key -> {}, entryKeys -> {}", key, entryKeys);
        Long count = redisTemplate.opsForHash().delete(key, entryKeys);
        log.info("hDelete(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 查看(key对应的)hash中，是否存在entryKey对应的entry
     * <p>
     * 注: 若redis中不存在key,则返回false。
     * 注: 若key对应的hash中不存在对应的entryKey, 也会返回false。
     *
     * @param key      定位hash的key
     * @param entryKey 定位hash中entry的entryKey
     * @return hash中是否存在entryKey对应的entry.
     */
    public boolean hExists(String key, String entryKey) {
        log.info("hExists(...) => key -> {}, entryKeys -> {}", key, entryKey);
        Boolean exist = redisTemplate.opsForHash().hasKey(key, entryKey);
        log.info("hExists(...) => exist -> {}", exist);
        return exist;
    }

    /**
     * 增/减(hash中的某个entryValue值) 整数
     * <p>
     * 注: 负数则为减。
     * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
     * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     * 抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key       用于定位hash的key
     * @param entryKey  用于定位entryValue的entryKey
     * @param increment 增加多少
     * @return 增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    public long hIncrBy(String key, Object entryKey, long increment) {
        log.info("hIncrBy(...) => key -> {}, entryKey -> {}, increment -> {}",
                key, entryKey, increment);
        Long result = redisTemplate.opsForHash().increment(key, entryKey, increment);
        log.info("hIncrBy(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 增/减(hash中的某个entryValue值) 浮点数
     * <p>
     * 注: 负数则为减。
     * 注: 若key不存在，那么会自动创建对应的hash,并创建对应的entryKey、entryValue,entryValue的初始值为increment。
     * 注: 若entryKey不存在，那么会自动创建对应的entryValue,entryValue的初始值为increment。
     * 注: 若key对应的value值不支持增/减操作(即: value不是数字)， 那么会
     * 抛出org.springframework.data.redis.RedisSystemException
     * 注: 因为是浮点数， 所以可能会和{@link #strIncrByFloat(String, double)}一样， 出现精度问题。
     * 追注: 本人简单测试了几组数据，暂未出现精度问题。
     *
     * @param key       用于定位hash的key
     * @param entryKey  用于定位entryValue的entryKey
     * @param increment 增加多少
     * @return 增加后的总值。
     * @throws RedisSystemException key对应的value值不支持增/减操作时
     */
    public double hIncrByFloat(String key, Object entryKey, double increment) {
        log.info("hIncrByFloat(...) => key -> {}, entryKey -> {}, increment -> {}",
                key, entryKey, increment);
        Double result = redisTemplate.opsForHash().increment(key, entryKey, increment);
        log.info("hIncrByFloat(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取(key对应的)hash中的所有entryKey
     * <p>
     * 注: 若key不存在，则返回的是一个空的Set(，而不是返回null)
     *
     * @param key 定位hash的key
     * @return hash中的所有entryKey
     */
    public Set<Object> hKeys(String key) {
        log.info("hKeys(...) => key -> {}", key);
        Set<Object> entryKeys = redisTemplate.opsForHash().keys(key);
        log.info("hKeys(...) => entryKeys -> {}", entryKeys);
        return entryKeys;
    }

    /**
     * 获取(key对应的)hash中的所有entryValue
     * <p>
     * 注: 若key不存在，则返回的是一个空的List(，而不是返回null)
     *
     * @param key 定位hash的key
     * @return hash中的所有entryValue
     */
    public List<Object> hValues(String key) {
        log.info("hValues(...) => key -> {}", key);
        List<Object> entryValues = redisTemplate.opsForHash().values(key);
        log.info("hValues(...) => entryValues -> {}", entryValues);
        return entryValues;
    }

    /**
     * 获取(key对应的)hash中的所有entry的数量
     * <p>
     * 注: 若redis中不存在对应的key, 则返回值为0
     *
     * @param key 定位hash的key
     * @return (key对应的)hash中, entry的个数
     */
    public long hSize(String key) {
        log.info("hSize(...) => key -> {}", key);
        Long count = redisTemplate.opsForHash().size(key);
        log.info("hSize(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 根据options匹配到(key对应的)hash中的对应的entryKey, 并返回对应的entry集
     * <p>
     * <p>
     * 注: ScanOptions实例的创建方式举例:
     * 1、ScanOptions.NONE
     * 2、ScanOptions.scanOptions().match("n??e").build()
     *
     * @param key     定位hash的key
     * @param options 匹配entryKey的条件
     *                注: ScanOptions.NONE表示全部匹配。
     *                注: ScanOptions.scanOptions().match(pattern).build()表示按照pattern匹配,
     *                其中pattern中可以使用通配符 * ? 等,
     *                * 表示>=0个字符
     *                ？ 表示有且只有一个字符
     *                此处的匹配规则与{@link #keys(String)}处的一样。
     * @return 匹配到的(key对应的)hash中的entry
     */
    public Cursor<Entry<Object, Object>> hScan(String key, ScanOptions options) {
        log.info("hScan(...) => key -> {}, options -> {}", key, JSON.toJSONString(options));
        Cursor<Entry<Object, Object>> cursor = redisTemplate.opsForHash().scan(key, options);
        log.info("hScan(...) => cursor -> {}", JSON.toJSONString(cursor));
        return cursor;
    }

    /*
        List类型相关操作 列表中的元素，可以重复。list是有序的。
        类别一: 从left到right, 是从0开始依次增大:   0,  1,  2,  3...
        类别二: 从right到left, 是从-1开始依次减小: -1, -2, -3, -4...
     */

    /**
     * 从左端推入元素进列表
     * <p>
     * 注: 若redis中不存在对应的key, 那么会自动创建
     *
     * @param key  定位list的key
     * @param item 要推入list的元素
     * @return 推入后，(key对应的)list的size
     */
    public long lLeftPush(String key, String item) {
        log.info("lLeftPush(...) => key -> {}, item -> {}", key, item);
        Long size = redisTemplate.opsForList().leftPush(key, item);
        log.info("lLeftPush(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 从左端批量推入元素进列表
     * <p>
     * 注: 若redis中不存在对应的key, 那么会自动创建
     * 注: 这一批item中，先push左侧的, 后push右侧的
     *
     * @param key   定位list的key
     * @param items 要批量推入list的元素集
     * @return 推入后，(key对应的)list的size
     */
    public long lLeftPushAll(String key, String... items) {
        log.info("lLeftPushAll(...) => key -> {}, items -> {}", key, items);
        Long size = redisTemplate.opsForList().leftPushAll(key, items);
        log.info("lLeftPushAll(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 从左端批量推入元素进列表
     * <p>
     * 注: 若redis中不存在对应的key, 那么会自动创建
     * 注: 这一批item中，那个item先从Collection取出来，就先push哪个
     *
     * @param key   定位list的key
     * @param items 要批量推入list的元素集
     * @return 推入后，(key对应的)list的size
     */
    public long lLeftPushAll(String key, Collection<String> items) {
        log.info("lLeftPushAll(...) => key -> {}, items -> {}", key, items);
        Long size = redisTemplate.opsForList().leftPushAll(key, items);
        log.info("lLeftPushAll(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 如果redis中存在key, 则从左端批量推入元素进列表;
     * 否则，不进行任何操作
     *
     * @param key  定位list的key
     * @param item 要推入list的项
     * @return 推入后，(key对应的)list的size
     */
    public long lLeftPushIfPresent(String key, String item) {
        log.info("lLeftPushIfPresent(...) => key -> {}, item -> {}", key, item);
        Long size = redisTemplate.opsForList().leftPushIfPresent(key, item);
        log.info("lLeftPushIfPresent(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 若key对应的list中存在pivot项, 那么将item放入第一个pivot项前(即:放在第一个pivot项左边);
     * 若key对应的list中不存在pivot项, 那么不做任何操作， 直接返回-1。
     * <p>
     * 注: 若redis中不存在对应的key, 那么会自动创建
     *
     * @param key  定位list的key
     * @param item 要推入list的元素
     * @return 推入后，(key对应的)list的size
     */
    public long lLeftPush(String key, String pivot, String item) {
        log.info("lLeftPush(...) => key -> {}, pivot -> {}, item -> {}", key, pivot, item);
        Long size = redisTemplate.opsForList().leftPush(key, pivot, item);
        log.info("lLeftPush(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 与{@link #lLeftPush(String, String)}类比即可， 不过是从list右侧推入元素
     */
    public long lRightPush(String key, String item) {
        log.info("lRightPush(...) => key -> {}, item -> {}", key, item);
        Long size = redisTemplate.opsForList().rightPush(key, item);
        log.info("lRightPush(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 与{@link #lLeftPushAll(String, String...)}类比即可， 不过是从list右侧推入元素
     */
    public long lRightPushAll(String key, String... items) {
        log.info("lRightPushAll(...) => key -> {}, items -> {}", key, items);
        Long size = redisTemplate.opsForList().rightPushAll(key, items);
        log.info("lRightPushAll(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 与{@link #lLeftPushAll(String, Collection<String>)}类比即可， 不过是从list右侧推入元素
     */
    public long lRightPushAll(String key, Collection<String> items) {
        log.info("lRightPushAll(...) => key -> {}, items -> {}", key, items);
        Long size = redisTemplate.opsForList().rightPushAll(key, items);
        log.info("lRightPushAll(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 与{@link #lLeftPushIfPresent(String, String)}类比即可， 不过是从list右侧推入元素
     */
    public long lRightPushIfPresent(String key, String item) {
        log.info("lRightPushIfPresent(...) => key -> {}, item -> {}", key, item);
        Long size = redisTemplate.opsForList().rightPushIfPresent(key, item);
        log.info("lRightPushIfPresent(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 与{@link #lLeftPush(String, String, String)}类比即可， 不过是从list右侧推入元素
     */
    public long lRightPush(String key, String pivot, String item) {
        log.info("lRightPush(...) => key -> {}, pivot -> {}, item -> {}", key, pivot, item);
        Long size = redisTemplate.opsForList().rightPush(key, pivot, item);
        log.info("lRightPush(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 【非阻塞队列 - 线程不安全】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
     * <p>
     * 注: 此方法是非阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，会立即返回null
     * 注: 此方法是非阻塞的， 即: 若redis中不存在对应的key,那么会立即返回null
     * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
     *
     * @param key 定位list的key
     * @return 移出的那个元素
     */
    public String lLeftPop(String key) {
        log.info("lLeftPop(...) => key -> {}", key);
        String item = redisTemplate.opsForList().leftPop(key);
        log.info("lLeftPop(...) => item -> {}", item);
        return item;
    }

    /**
     * 【阻塞队列 - 线程安全】 从左侧移出(key对应的)list中的第一个元素, 并将该元素返回
     * <p>
     * 注: 此方法是阻塞的， 即: 若(key对应的)list中的所有元素都被pop移出了，此时，再进行pop的话，
     * 会阻塞timeout这么久，然后返回null
     * 注: 此方法是阻塞的， 即: 若redis中不存在对应的key,那么会阻塞timeout这么久，然后返回null
     * 注: 若将(key对应的)list中的所有元素都pop完了，那么该key会被删除
     * <p>
     * 提示: 若阻塞过程中， 目标key-list出现了，且里面有item了，那么会立马停止阻塞, 进行元素移出并返回
     *
     * @param key     定位list的key
     * @param timeout 超时时间
     * @param unit    timeout的单位
     * @return 移出的那个元素
     */
    public String lLeftPop(String key, long timeout, TimeUnit unit) {
        log.info("lLeftPop(...) => key -> {}, timeout -> {}, unit -> {}", key, timeout, unit);
        String item = redisTemplate.opsForList().leftPop(key, timeout, unit);
        log.info("lLeftPop(...) => item -> {}", item);
        return item;
    }

    /**
     * 与{@link #lLeftPop(String)}类比即可， 不过是从list右侧移出元素
     */
    public String lRightPop(String key) {
        log.info("lRightPop(...) => key -> {}", key);
        String item = redisTemplate.opsForList().rightPop(key);
        log.info("lRightPop(...) => item -> {}", item);
        return item;
    }

    /**
     * 与{@link #lLeftPop(String, long, TimeUnit)}类比即可， 不过是从list右侧移出元素
     */
    public String lRightPop(String key, long timeout, TimeUnit unit) {
        log.info("lRightPop(...) => key -> {}, timeout -> {}, unit -> {}", key, timeout, unit);
        String item = redisTemplate.opsForList().rightPop(key, timeout, unit);
        log.info("lRightPop(...) => item -> {}", item);
        return item;
    }

    /**
     * 【非阻塞队列 - 线程不安全】 从sourceKey对应的sourceList右侧移出一个item, 并将这个item推
     * 入(destinationKey对应的)destinationList的左侧
     * <p>
     * 注: 若sourceKey对应的list中没有item了，则立马认为(从sourceKey对应的list中pop出来的)item为null,
     * null并不会往destinationKey对应的list中push。
     * 追注: 此时，此方法的返回值是null。
     * <p>
     * 注: 若将(sourceKey对应的)list中的所有元素都pop完了，那么该sourceKey会被删除。
     *
     * @param sourceKey      定位sourceList的key
     * @param destinationKey 定位destinationList的key
     * @return 移动的这个元素
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        log.info("lRightPopAndLeftPush(...) => sourceKey -> {}, destinationKey -> {}",
                sourceKey, destinationKey);
        String item = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
        log.info("lRightPopAndLeftPush(...) => item -> {}", item);
        return item;
    }

    /**
     * 【阻塞队列 - 线程安全】 从sourceKey对应的sourceList右侧移出一个item, 并将这个item推
     * 入(destinationKey对应的)destinationList的左侧
     * <p>
     * 注: 若sourceKey对应的list中没有item了，则阻塞等待, 直到能从sourceList中移出一个非null的item(或等待时长超时);
     * case1: 等到了一个非null的item, 那么继续下面的push操作，并返回这个item。
     * case2: 超时了，还没等到非null的item, 那么pop出的结果就未null,此时并不会往destinationList进行push。
     * 此时，此方法的返回值是null。
     * <p>
     * 注: 若将(sourceKey对应的)list中的所有元素都pop完了，那么该sourceKey会被删除。
     *
     * @param sourceKey      定位sourceList的key
     * @param destinationKey 定位destinationList的key
     * @param timeout        超时时间
     * @param unit           timeout的单位
     * @return 移动的这个元素
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout,
                                       TimeUnit unit) {
        log.info("lRightPopAndLeftPush(...) => sourceKey -> {}, destinationKey -> {}, timeout -> {},"
                + " unit -> {}", sourceKey, destinationKey, timeout, unit);
        String item = redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
        log.info("lRightPopAndLeftPush(...) => item -> {}", item);
        return item;
    }

    /**
     * 设置(key对应的)list中对应索引位置index处的元素为item
     * <p>
     * 注: 若key不存在，则会抛出org.springframework.data.redis.RedisSystemException
     * 注: 若索引越界，也会抛出org.springframework.data.redis.RedisSystemException
     *
     * @param key   定位list的key
     * @param index 定位list中的元素的索引
     * @param item  要替换成的值
     */
    public void lSet(String key, long index, String item) {
        log.info("lSet(...) => key -> {}, index -> {}, item -> {}", key, index, item);
        redisTemplate.opsForList().set(key, index, item);
    }

    /**
     * 通过索引index, 获取(key对应的)list中的元素
     * <p>
     * 注: 若key不存在 或 index超出(key对应的)list的索引范围，那么返回null
     *
     * @param key   定位list的key
     * @param index 定位list中的item的索引
     * @return list中索引index对应的item
     */
    public String lIndex(String key, long index) {
        log.info("lIndex(...) => key -> {}, index -> {}", key, index);
        String item = redisTemplate.opsForList().index(key, index);
        log.info("lIndex(...) => item -> {}", item);
        return item;
    }

    /**
     * 获取(key对应的)list中索引在[start, end]之间的item集
     * <p>
     * 注: 含start、含end。
     * 注: 当key不存在时，获取到的是空的集合。
     * 注: 当获取的范围比list的范围还要大时，获取到的是这两个范围的交集。
     * <p>
     * 提示: 可通过RedisUtil.lRange(key, 0, -1)来获取到该key对应的整个list
     *
     * @param key   定位list的key
     * @param start 起始元素的index
     * @param end   结尾元素的index
     * @return 对应的元素集合
     */
    public List<String> lRange(String key, long start, long end) {
        log.info("lRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        List<String> result = redisTemplate.opsForList().range(key, start, end);
        log.info("lRange(...) => result -> {}", result);
        return result;
    }

    /**
     * 获取(key对应的)list
     *
     * @param key 定位list的key
     * @return (key对应的)list
     * @see #lRange(String, long, long)
     */
    public List<String> lWholeList(String key) {
        log.info("lWholeList(...) => key -> {}", key);
        List<String> result = redisTemplate.opsForList().range(key, 0, -1);
        log.info("lWholeList(...) => result -> {}", result);
        return result;
    }

    /**
     * 获取(key对应的)list的size
     * <p>
     * 注: 当key不存在时，获取到的size为0.
     *
     * @param key 定位list的key
     * @return list的size。
     */
    public long lSize(String key) {
        log.info("lSize(...) => key -> {}", key);
        Long size = redisTemplate.opsForList().size(key);
        log.info("lSize(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 删除(key对应的)list中，前expectCount个值等于item的项
     * <p>
     * 注: 若expectCount == 0， 则表示删除list中所有的值等于item的项.
     * 注: 若expectCount > 0，  则表示删除从左往右进行
     * 注: 若expectCount < 0，  则表示删除从右往左进行
     * <p>
     * 注: 若list中,值等于item的项的个数少于expectCount时，那么会删除list中所有的值等于item的项。
     * 注: 当key不存在时, 返回0。
     * 注: 若lRemove后， 将(key对应的)list中没有任何元素了，那么该key会被删除。
     *
     * @param key         定位list的key
     * @param expectCount 要删除的item的个数
     * @param item        要删除的item
     * @return 实际删除了的item的个数
     */
    public long lRemove(String key, long expectCount, String item) {
        log.info("lRemove(...) => key -> {}, expectCount -> {}, item -> {}", key, expectCount, item);
        Long actualCount = redisTemplate.opsForList().remove(key, expectCount, item);
        log.info("lRemove(...) => actualCount -> {}", actualCount);
        if (actualCount == null) {
            throw new RedisOpsResultIsNullException();
        }
        return actualCount;
    }

    /**
     * 裁剪(即: 对list中的元素取交集。)
     * <p>
     * 举例说明: list中的元素索引范围是[0, 8], 而这个方法传入的[start, end]为 [3, 10]，
     * 那么裁剪就是对[0, 8]和[3, 10]进行取交集， 得到[3, 8], 那么裁剪后
     * 的list中，只剩下(原来裁剪前)索引在[3, 8]之间的元素了。
     * <p>
     * 注: 若裁剪后的(key对应的)list就是空的,那么该key会被删除。
     *
     * @param key   定位list的key
     * @param start 要删除的item集的起始项的索引
     * @param end   要删除的item集的结尾项的索引
     */
    public void lTrim(String key, long start, long end) {
        log.info("lTrim(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        redisTemplate.opsForList().trim(key, start, end);
    }

    /*
        Set类型相关操作 set中的元素，不可以重复。set是无序的。
     */

    /**
     * 向(key对应的)set中添加items
     * <p>
     * 注: 若key不存在，则会自动创建。
     * 注: set中的元素会去重。
     *
     * @param key   定位set的key
     * @param items 要向(key对应的)set中添加的items
     * @return 此次添加操作, 添加到set中的元素的个数
     */
    public long sAdd(String key, String... items) {
        log.info("sAdd(...) => key -> {}, items -> {}", key, items);
        Long count = redisTemplate.opsForSet().add(key, items);
        log.info("sAdd(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 从(key对应的)set中删除items
     * <p>
     * 注: 若key不存在, 则返回0。
     * 注: 若已经将(key对应的)set中的项删除完了，那么对应的key也会被删除。
     *
     * @param key   定位set的key
     * @param items 要移除的items
     * @return 实际删除了的个数
     */
    public long sRemove(String key, Object... items) {
        log.info("sRemove(...) => key -> {}, items -> {}", key, items);
        Long count = redisTemplate.opsForSet().remove(key, items);
        log.info("sRemove(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 从(key对应的)set中随机移出一个item, 并返回这个item
     * <p>
     * 注: 因为set是无序的，所以移出的这个item,是随机的; 并且，哪怕
     * 是数据一样的set,多次测试移出操作,移除的元素也是随机的。
     * <p>
     * 注: 若已经将(key对应的)set中的项pop完了，那么对应的key会被删除。
     *
     * @param key 定位set的key
     * @return 移出的项
     */
    public String sPop(String key) {
        log.info("sPop(...) => key -> {}", key);
        String popItem = redisTemplate.opsForSet().pop(key);
        log.info("sPop(...) => popItem -> {}", popItem);
        return popItem;
    }

    /**
     * 将(sourceKey对应的)sourceSet中的元素item, 移动到(destinationKey对应的)destinationSet中
     * <p>
     * 注: 当sourceKey不存在时， 返回false
     * 注: 当item不存在时， 返回false
     * 注: 若destinationKey不存在， 那么在移动时会自动创建
     * 注: 若已经将(sourceKey对应的)set中的项move出去完了，那么对应的sourceKey会被删除。
     *
     * @param sourceKey      定位sourceSet的key
     * @param item           要移动的项目
     * @param destinationKey 定位destinationSet的key
     * @return 移动成功与否
     */
    public boolean sMove(String sourceKey, String item, String destinationKey) {
        Boolean result = redisTemplate.opsForSet().move(sourceKey, item, destinationKey);
        log.info("sMove(...) => sourceKey -> {}, destinationKey -> {}, item -> {}",
                sourceKey, destinationKey, item);
        log.info("sMove(...) =>  result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取(key对应的)set中的元素个数
     * <p>
     * 注: 若key不存在，则返回0
     *
     * @param key 定位set的key
     * @return (key对应的)set中的元素个数
     */
    public long sSize(String key) {
        log.info("sSize(...) => key -> {}", key);
        Long size = redisTemplate.opsForSet().size(key);
        log.info("sSize(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 判断(key对应的)set中是否含有item
     * <p>
     * 注: 若key不存在，则返回false。
     *
     * @param key  定位set的key
     * @param item 被查找的项
     * @return (key对应的)set中是否含有item
     */
    public boolean sIsMember(String key, Object item) {
        log.info("sSize(...) => key -> {}, size -> {}", key, item);
        Boolean result = redisTemplate.opsForSet().isMember(key, item);
        log.info("sSize(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 获取两个(key对应的)Set的交集
     * <p>
     * 注: 若不存在任何交集，那么返回空的集合(, 而不是null)
     * 注: 若其中一个key不存在(或两个key都不存在)，那么返回空的集合(, 而不是null)
     *
     * @param key      定位其中一个set的键
     * @param otherKey 定位其中另一个set的键
     * @return item交集
     */
    public Set<String> sIntersect(String key, String otherKey) {
        log.info("sIntersect(...) => key -> {}, otherKey -> {}", key, otherKey);
        Set<String> intersectResult = redisTemplate.opsForSet().intersect(key, otherKey);
        log.info("sIntersect(...) => intersectResult -> {}", intersectResult);
        return intersectResult;
    }

    /**
     * 获取多个(key对应的)Set的交集
     * <p>
     * 注: 若不存在任何交集，那么返回空的集合(, 而不是null)
     * 注: 若>=1个key不存在，那么返回空的集合(, 而不是null)
     *
     * @param key       定位其中一个set的键
     * @param otherKeys 定位其它set的键集
     * @return item交集
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        log.info("sIntersect(...) => key -> {}, otherKeys -> {}", key, otherKeys);
        Set<String> intersectResult = redisTemplate.opsForSet().intersect(key, otherKeys);
        log.info("sIntersect(...) => intersectResult -> {}", intersectResult);
        return intersectResult;
    }

    /**
     * 获取两个(key对应的)Set的交集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)set中
     * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将交集添加到(storeKey对应的)set中
     * case3: 交集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求交集的部分，详见{@link #sIntersect(String, String)}
     *
     * @param key      定位其中一个set的键
     * @param otherKey 定位其中另一个set的键
     * @param storeKey 定位(要把交集添加到哪个)set的key
     * @return add到(storeKey对应的)Set后, 该set对应的size
     */
    public long sIntersectAndStore(String key, String otherKey, String storeKey) {
        log.info("sIntersectAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                key, otherKey, storeKey);
        Long size = redisTemplate.opsForSet().intersectAndStore(key, otherKey, storeKey);
        log.info("sIntersectAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取多个(key对应的)Set的交集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)set中
     * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将交集添加到(storeKey对应的)set中
     * case3: 交集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求交集的部分，详见{@link #sIntersect(String, Collection)}
     */
    public long sIntersectAndStore(String key, Collection<String> otherKeys, String storeKey) {
        log.info("sIntersectAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}", key, otherKeys, storeKey);
        Long size = redisTemplate.opsForSet().intersectAndStore(key, otherKeys, storeKey);
        log.info("sIntersectAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取两个(key对应的)Set的并集
     * <p>
     * 注: 并集中的元素也是唯一的，这是Set保证的。
     *
     * @param key      定位其中一个set的键
     * @param otherKey 定位其中另一个set的键
     * @return item并集
     */
    public Set<String> sUnion(String key, String otherKey) {
        log.info("sUnion(...) => key -> {}, otherKey -> {}", key, otherKey);
        Set<String> unionResult = redisTemplate.opsForSet().union(key, otherKey);
        log.info("sUnion(...) => unionResult -> {}", unionResult);
        return unionResult;
    }

    /**
     * 获取两个(key对应的)Set的并集
     * <p>
     * 注: 并集中的元素也是唯一的，这是Set保证的。
     *
     * @param key       定位其中一个set的键
     * @param otherKeys 定位其它set的键集
     * @return item并集
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        log.info("sUnion(...) => key -> {}, otherKeys -> {}", key, otherKeys);
        Set<String> unionResult = redisTemplate.opsForSet().union(key, otherKeys);
        log.info("sUnion(...) => unionResult -> {}", unionResult);
        return unionResult;
    }

    /**
     * 获取两个(key对应的)Set的并集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)set中
     * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将并集添加到(storeKey对应的)set中
     * case3: 并集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求并集的部分，详见{@link #sUnion(String, String)}
     *
     * @param key      定位其中一个set的键
     * @param otherKey 定位其中另一个set的键
     * @param storeKey 定位(要把并集添加到哪个)set的key
     * @return add到(storeKey对应的)Set后, 该set对应的size
     */
    public long sUnionAndStore(String key, String otherKey, String storeKey) {
        log.info("sUnionAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                key, otherKey, storeKey);
        Long size = redisTemplate.opsForSet().unionAndStore(key, otherKey, storeKey);
        log.info("sUnionAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取两个(key对应的)Set的并集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)set中
     * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将并集添加到(storeKey对应的)set中
     * case3: 并集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求并集的部分，详见{@link #sUnion(String, Collection)}
     *
     * @param key       定位其中一个set的键
     * @param otherKeys 定位其它set的键集
     * @param storeKey  定位(要把并集添加到哪个)set的key
     * @return add到(storeKey对应的)Set后, 该set对应的size
     */
    public long sUnionAndStore(String key, Collection<String> otherKeys, String storeKey) {
        log.info("sUnionAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                key, otherKeys, storeKey);
        Long size = redisTemplate.opsForSet().unionAndStore(key, otherKeys, storeKey);
        log.info("sUnionAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集
     * <p>
     * 注: 如果被减数key不存在， 那么结果为空的集合(，而不是null)
     * 注: 如果被减数key存在，但减数key不存在， 那么结果即为(被减数key对应的)Set
     *
     * @param key      定位"被减数set"的键
     * @param otherKey 定位"减数set"的键
     * @return item差集
     */
    public Set<String> sDifference(String key, String otherKey) {
        log.info("sDifference(...) => key -> {}, otherKey -> {}",
                key, otherKey);
        Set<String> differenceResult = redisTemplate.opsForSet().difference(key, otherKey);
        log.info("sDifference(...) => differenceResult -> {}", differenceResult);
        return differenceResult;
    }

    /**
     * 获取 (key对应的)Set 减去 (otherKeys对应的)Sets 的差集
     * <p>
     * 注: 如果被减数key不存在， 那么结果为空的集合(，而不是null)
     * 注: 如果被减数key存在，但减数key不存在， 那么结果即为(被减数key对应的)Set
     * <p>
     * 提示: 当有多个减数时， 被减数先减去哪一个减数，后减去哪一个减数，是无所谓的，是不影响最终结果的。
     *
     * @param key       定位"被减数set"的键
     * @param otherKeys 定位"减数集sets"的键集
     * @return item差集
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        log.info("sDifference(...) => key -> {}, otherKeys -> {}", key, otherKeys);
        Set<String> differenceResult = redisTemplate.opsForSet().difference(key, otherKeys);
        log.info("sDifference(...) => differenceResult -> {}", differenceResult);
        return differenceResult;
    }

    /**
     * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 差集不为空, storeKey不存在， 则 会创建对应的storeKey，并将差集添加到(storeKey对应的)set中
     * case2: 差集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将差集添加到(storeKey对应的)set中
     * case3: 差集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求并集的部分，详见{@link #sDifference(String, String)}
     *
     * @param key      定位"被减数set"的键
     * @param otherKey 定位"减数set"的键
     * @param storeKey 定位(要把差集添加到哪个)set的key
     * @return add到(storeKey对应的)Set后, 该set对应的size
     */
    public long sDifferenceAndStore(String key, String otherKey, String storeKey) {
        log.info("sDifferenceAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}",
                key, otherKey, storeKey);
        Long size = redisTemplate.opsForSet().differenceAndStore(key, otherKey, storeKey);
        log.info("sDifferenceAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取 (key对应的)Set 减去 (otherKey对应的)Set 的差集, 并将结果add到storeKey对应的Set中。
     * <p>
     * case1: 差集不为空, storeKey不存在， 则 会创建对应的storeKey，并将差集添加到(storeKey对应的)set中
     * case2: 差集不为空, storeKey已存在， 则 会清除原(storeKey对应的)set中所有的项，然后将差集添加到(storeKey对应的)set中
     * case3: 差集为空, 则不进行下面的操作, 直接返回0
     * <p>
     * 注: 求并集的部分，详见{@link #sDifference(String, String)}
     *
     * @param key       定位"被减数set"的键
     * @param otherKeys 定位"减数集sets"的键集
     * @param storeKey  定位(要把差集添加到哪个)set的key
     * @return add到(storeKey对应的)Set后, 该set对应的size
     */
    public long sDifferenceAndStore(String key, Collection<String> otherKeys, String storeKey) {
        log.info("sDifferenceAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                key, otherKeys, storeKey);
        Long size = redisTemplate.opsForSet().differenceAndStore(key, otherKeys, storeKey);
        log.info("sDifferenceAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取key对应的set
     * <p>
     * 注: 若key不存在, 则返回的是空的set(, 而不是null)
     *
     * @param key 定位set的key
     * @return (key对应的)set
     */
    public Set<String> sMembers(String key) {
        log.info("sMembers(...) => key -> {}", key);
        Set<String> members = redisTemplate.opsForSet().members(key);
        log.info("sMembers(...) => members -> {}", members);
        return members;
    }

    /**
     * 从key对应的set中随机获取一项
     *
     * @param key 定位set的key
     * @return 随机获取到的项
     */
    public String sRandomMember(String key) {
        log.info("sRandomMember(...) => key -> {}", key);
        String randomItem = redisTemplate.opsForSet().randomMember(key);
        log.info("sRandomMember(...) => randomItem -> {}", randomItem);
        return randomItem;
    }

    /**
     * 从key对应的set中获取count次随机项(, set中的同一个项可能被多次获取)
     * <p>
     * 注: count可大于set的size。
     * 注: 取出来的结果里可能存在相同的值。
     *
     * @param key   定位set的key
     * @param count 要取多少项
     * @return 随机获取到的项集
     */
    public List<String> sRandomMembers(String key, long count) {
        log.info("sRandomMembers(...) => key -> {}, count -> {}", key, count);
        List<String> randomItems = redisTemplate.opsForSet().randomMembers(key, count);
        log.info("sRandomMembers(...) => randomItems -> {}", randomItems);
        return randomItems;
    }

    /**
     * 从key对应的set中随机获取count个项
     * <p>
     * 注: 若count >= set的size, 那么返回的即为这个key对应的set。
     * 注: 取出来的结果里没有重复的项。
     *
     * @param key   定位set的key
     * @param count 要取多少项
     * @return 随机获取到的项集
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        log.info("sDistinctRandomMembers(...) => key -> {}, count -> {}", key, count);
        Set<String> distinctRandomItems = redisTemplate.opsForSet().distinctRandomMembers(key, count);
        log.info("sDistinctRandomMembers(...) => distinctRandomItems -> {}", distinctRandomItems);
        return distinctRandomItems;
    }

    /**
     * 根据options匹配到(key对应的)set中的对应的item, 并返回对应的item集
     * <p>
     * <p>
     * 注: ScanOptions实例的创建方式举例:
     * 1、ScanOptions.NONE
     * 2、ScanOptions.scanOptions().match("n??e").build()
     *
     * @param key     定位set的key
     * @param options 匹配set中的item的条件
     *                注: ScanOptions.NONE表示全部匹配。
     *                注: ScanOptions.scanOptions().match(pattern).build()表示按照pattern匹配,
     *                其中pattern中可以使用通配符 * ? 等,
     *                * 表示>=0个字符
     *                ？ 表示有且只有一个字符
     *                此处的匹配规则与{@link #keys(String)}处的一样。
     * @return 匹配到的(key对应的)set中的项
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        log.info("sScan(...) => key -> {}, options -> {}", key, JSON.toJSONString(options));
        Cursor<String> cursor = redisTemplate.opsForSet().scan(key, options);
        log.info("sScan(...) => cursor -> {}", JSON.toJSONString(cursor));
        return cursor;
    }

    /*
        ZSet类型相关操作
        特别说明: ZSet是有序的,不仅体现在:redis中的存储上有序。还体现在:此工具类中返回值类型为Set<?>的方法, 实际返回类型是LinkedHashSet<?>
        redis中的ZSet, 一定程度等于redis中的Set + redis中的Hash的结合体。
        ZSet中的entryKey即为成员项， entryValue即为这个成员项的分值, ZSet根据成员的分值，来堆成员进行排序。
     */

    /**
     * 向(key对应的)zset中添加(item, score)
     * <p>
     * 注: item为entryKey成员项， score为entryValue分数值。
     * <p>
     * 注: 若(key对应的)zset中已存在(与此次要添加的项)相同的item项，那么此次添加操作会失败，返回false；
     * 但是！！！ zset中原item的score会被更新为此次add的相同item项的score。
     * 所以, 也可以通过zAdd达到更新item对应score的目的。
     * <p>
     * 注: score可为正、可为负、可为0; 总之, double范围内都可以。
     * <p>
     * 注: 若score的值一样，则按照item排序。
     *
     * @param key   定位set的key
     * @param item  要往(key对应的)zset中添加的成员项
     * @param score item的分值
     * @return 是否添加成功
     */
    public boolean zAdd(String key, String item, double score) {
        log.info("zAdd(...) => key -> {}, item -> {}, score -> {}", key, item, score);
        Boolean result = redisTemplate.opsForZSet().add(key, item, score);
        log.info("zAdd(...) => result -> {}", result);
        if (result == null) {
            throw new RedisOpsResultIsNullException();
        }
        return result;
    }

    /**
     * 批量添加entry<item, score>
     * <p>
     * 注: 若entry<item, score>集中存在item相同的项(, score不一样)，那么redis在执行真正的批量add操作前,会
     * 将其中一个item过滤掉。
     * 注: 同样的，若(key对应的)zset中已存在(与此次要添加的项)相同的item项，那么此次批量添加操作中，
     * 对该item项的添加会失败，会失败，成功计数器不会加1；但是！！！ zset中原item的score会被更新为此
     * 次add的相同item项的score。所以, 也可以通过zAdd达到更新item对应score的目的。
     *
     * @param key     定位set的key
     * @param entries 要添加的entry<item, score>集
     * @return 本次添加进(key对应的)zset中的entry的个数
     */
    public long zAdd(String key, Set<TypedTuple<String>> entries) {
        log.info("zAdd(...) => key -> {}, entries -> {}", key, JSON.toJSONString(entries));
        Long count = redisTemplate.opsForZSet().add(key, entries);
        log.info("zAdd(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 从(key对应的)zset中移除项
     * <p>
     * 注:若key不存在，则返回0
     *
     * @param key   定位set的key
     * @param items 要移除的项集
     * @return 实际移除了的项的个数
     */
    public long zRemove(String key, Object... items) {
        log.info("zRemove(...) => key -> {}, items -> {}", key, items);
        Long count = redisTemplate.opsForZSet().remove(key, items);
        log.info("zRemove(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 移除(key对应的)zset中, 排名范围在[startIndex, endIndex]内的item
     * <p>
     * 注:默认的，按score.item升序排名， 排名从0开始
     * <p>
     * 注: 类似于List中的索引, 排名可以分为多个方式:
     * 从前到后(正向)的排名: 0、1、2...
     * 从后到前(反向)的排名: -1、-2、-3...
     * <p>
     * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startRange代表的元素的位置
     * 在endRange代表的元素的位置的前面， 如:
     * 示例一: RedisUtil.zRemoveRange("name", 0, 2);
     * 示例二: RedisUtil.zRemoveRange("site", -2, -1);
     * 示例三: RedisUtil.zRemoveRange("foo", 0, -1);
     * <p>
     * 注:若key不存在，则返回0
     *
     * @param key        定位set的key
     * @param startRange 开始项的排名
     * @param endRange   结尾项的排名
     * @return 实际移除了的项的个数
     */
    public long zRemoveRange(String key, long startRange, long endRange) {
        log.info("zRemoveRange(...) => key -> {}, startRange -> {}, endRange -> {}",
                key, startRange, endRange);
        Long count = redisTemplate.opsForZSet().removeRange(key, startRange, endRange);
        log.info("zRemoveRange(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 移除(key对应的)zset中, score范围在[minScore, maxScore]内的item
     * <p>
     * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
     * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
     * maxScore应该设置得比要删除的项里，最大的score还大一点
     * 追注: 本人简单测试了几组数据，暂未出现精度问题。
     * <p>
     * 注:若key不存在，则返回0
     *
     * @param key      定位set的key
     * @param minScore score下限(含这个值)
     * @param maxScore score上限(含这个值)
     * @return 实际移除了的项的个数
     */
    public long zRemoveRangeByScore(String key, double minScore, double maxScore) {
        log.info("zRemoveRangeByScore(...) => key -> {}, startIndex -> {}, startIndex -> {}",
                key, minScore, maxScore);
        Long count = redisTemplate.opsForZSet().removeRangeByScore(key, minScore, maxScore);
        log.info("zRemoveRangeByScore(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 增/减 (key对应的zset中,)item的分数值
     *
     * @param key   定位zset的key
     * @param item  项
     * @param delta 变化量(正 - 增, 负 - 减)
     * @return 修改后的score值
     */
    public double zIncrementScore(String key, String item, double delta) {
        log.info("zIncrementScore(...) => key -> {}, item -> {}, delta -> {}", key, item, delta);
        Double scoreValue = redisTemplate.opsForZSet().incrementScore(key, item, delta);
        log.info("zIncrementScore(...) => scoreValue -> {}", scoreValue);
        if (scoreValue == null) {
            throw new RedisOpsResultIsNullException();
        }
        return scoreValue;
    }

    /**
     * 返回item在(key对应的)zset中的(按score从小到大的)排名
     * <p>
     * 注: 排名从0开始。 即意味着，此方法等价于: 返回item在(key对应的)zset中的位置索引。
     * 注: 若key或item不存在， 返回null。
     * 注: 排序规则是score,item, 即:优先以score排序，若score相同，则再按item排序。
     *
     * @param key  定位zset的key
     * @param item 项
     * @return 排名(等价于 : 索引)
     */
    public long zRank(String key, Object item) {
        log.info("zRank(...) => key -> {}, item -> {}", key, item);
        Long rank = redisTemplate.opsForZSet().rank(key, item);
        log.info("zRank(...) => rank -> {}", rank);
        if (rank == null) {
            throw new RedisOpsResultIsNullException();
        }
        return rank;
    }

    /**
     * 返回item在(key对应的)zset中的(按score从大到小的)排名
     * <p>
     * 注: 排名从0开始。补充: 因为是按score从大到小排序的, 所以最大score对应的item的排名为0。
     * 注: 若key或item不存在， 返回null。
     * 注: 排序规则是score,item, 即:优先以score排序，若score相同，则再按item排序。
     *
     * @param key  定位zset的key
     * @param item 项
     * @return 排名(等价于 : 索引)
     */
    public long zReverseRank(String key, Object item) {
        log.info("zReverseRank(...) => key -> {}, item -> {}", key, item);
        Long reverseRank = redisTemplate.opsForZSet().reverseRank(key, item);
        log.info("zReverseRank(...) => reverseRank -> {}", reverseRank);
        if (reverseRank == null) {
            throw new RedisOpsResultIsNullException();
        }
        return reverseRank;
    }

    /**
     * 根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的item项集
     * <p>
     * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startIndex代表的元素的
     * 位置在endIndex代表的元素的位置的前面， 如:
     * 示例一: RedisUtil.zRange("name", 0, 2);
     * 示例二: RedisUtil.zRange("site", -2, -1);
     * 示例三: RedisUtil.zRange("foo", 0, -1);
     * <p>
     * 注: 若key不存在, 则返回空的集合。
     * <p>
     * 注: 当[start, end]的范围比实际zset的范围大时, 返回范围上"交集"对应的项集合。
     *
     * @param key   定位zset的key
     * @param start 排名开始位置
     * @param end   排名结束位置
     * @return 对应的item项集
     */
    public Set<String> zRange(String key, long start, long end) {
        log.info("zRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        Set<String> result = redisTemplate.opsForZSet().range(key, start, end);
        log.info("zRange(...) => result -> {}", result);
        return result;
    }

    /**
     * 获取(key对应的)zset中的所有item项
     *
     * @param key 定位zset的键
     * @return (key对应的)zset中的所有item项
     * @see #zRange(String, long, long)
     */
    public Set<String> zWholeZSetItem(String key) {
        log.info("zWholeZSetItem(...) => key -> {}", key);
        Set<String> result = redisTemplate.opsForZSet().range(key, 0, -1);
        log.info("zWholeZSetItem(...) =>result -> {}", result);
        return result;
    }

    /**
     * 根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的entry集
     * <p>
     * 注: 不论是使用正向排名，还是使用反向排名, 使用此方法时, 应保证 startIndex代表的元素的
     * 位置在endIndex代表的元素的位置的前面， 如:
     * 示例一: RedisUtil.zRange("name", 0, 2);
     * 示例二: RedisUtil.zRange("site", -2, -1);
     * 示例三: RedisUtil.zRange("foo", 0, -1);
     * <p>
     * 注: 若key不存在, 则返回空的集合。
     * <p>
     * 注: 当[start, end]的范围比实际zset的范围大时, 返回范围上"交集"对应的项集合。
     * <p>
     * 注: 此方法和{@link #zRange(String, long, long)}类似，不过此方法返回的不是item集， 而是entry集
     *
     * @param key   定位zset的key
     * @param start 排名开始位置
     * @param end   排名结束位置
     * @return 对应的entry集
     */
    public Set<TypedTuple<String>> zRangeWithScores(String key, long start, long end) {
        log.info("zRangeWithScores(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        log.info("zRangeWithScores(...) => entries -> {}", JSON.toJSONString(entries));
        return entries;
    }

    /**
     * 获取(key对应的)zset中的所有entry
     *
     * @param key 定位zset的键
     * @return (key对应的)zset中的所有entry
     * @see #zRangeWithScores(String, long, long)
     */
    public Set<TypedTuple<String>> zWholeZSetEntry(String key) {
        log.info("zWholeZSetEntry(...) => key -> {}", key);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        log.info("zWholeZSetEntry(...) => entries -> {}", key, JSON.toJSONString(entries));
        return entries;
    }

    /**
     * 根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的item项集
     * <p>
     * 注: 若key不存在, 则返回空的集合。
     * 注: 当[minScore, maxScore]的范围比实际zset中score的范围大时, 返回范围上"交集"对应的项集合。
     * <p>
     * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
     * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
     * maxScore应该设置得比要删除的项里，最大的score还大一点
     * 追注: 本人简单测试了几组数据，暂未出现精度问题。
     *
     * @param key      定位zset的key
     * @param minScore score下限
     * @param maxScore score上限
     * @return 对应的item项集
     */
    public Set<String> zRangeByScore(String key, double minScore, double maxScore) {
        log.info("zRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}", key, minScore, maxScore);
        Set<String> items = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore);
        log.info("zRangeByScore(...) => items -> {}", items);
        return items;
    }

    /**
     * 根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的, score处于[minScore,
     * 排名大于等于offset的count个item项
     * <p>
     * 特别注意: 对于不是特别熟悉redis的人来说, offset 和 count最好都使用正数， 避免引起理解上的歧义。
     * <p>
     * 注: 若key不存在, 则返回空的集合。
     * <p>
     * 提示: 虽然删除范围包含两侧的端点(即:包含minScore和maxScore), 但是由于double存在精度问题，所以建议:
     * 设置值时，minScore应该设置得比要删除的项里，最小的score还小一点
     * maxScore应该设置得比要删除的项里，最大的score还大一点
     * 追注: 本人简单测试了几组数据，暂未出现精度问题。
     *
     * @param key      定位zset的key
     * @param minScore score下限
     * @param maxScore score上限
     * @param offset   偏移量(即:排名下限)
     * @param count    期望获取到的元素个数
     * @return 对应的item项集
     */
    public Set<String> zRangeByScore(String key, double minScore, double maxScore,
                                     long offset, long count) {
        log.info("zRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}, offset -> {}, "
                + "count -> {}", key, minScore, maxScore, offset, count);
        Set<String> items = redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore, offset, count);
        log.info("zRangeByScore(...) => items -> {}", items);
        return items;
    }

    /**
     * 获取(key对应的)zset中的所有score处于[minScore, maxScore]中的entry
     *
     * @param key      定位zset的键
     * @param minScore score下限
     * @param maxScore score上限
     * @return (key对应的)zset中的所有score处于[minScore, maxScore]中的entry
     * @see #zRangeByScore(String, double, double)
     * <p>
     * 注: 若key不存在, 则返回空的集合。
     * 注: 当[minScore, maxScore]的范围比实际zset中score的范围大时, 返回范围上"交集"对应的项集合。
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double minScore, double maxScore) {
        log.info("zRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {}",
                key, minScore, maxScore);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeByScoreWithScores(key, minScore, maxScore);
        log.info("zRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
        return entries;
    }

    /**
     * 获取(key对应的)zset中, score处于[minScore, maxScore]里的、排名大于等于offset的count个entry
     * <p>
     * 特别注意: 对于不是特别熟悉redis的人来说, offset 和 count最好都使用正数， 避免引起理解上的歧义。
     *
     * @param key      定位zset的键
     * @param minScore score下限
     * @param maxScore score上限
     * @param offset   偏移量(即:排名下限)
     * @param count    期望获取到的元素个数
     * @return [startIndex, endIndex] & [minScore, maxScore]里的entry
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key, double minScore,
                                                           double maxScore, long offset,
                                                           long count) {
        log.info("zRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {},"
                        + " offset -> {}, count -> {}",
                key, minScore, maxScore, offset, count);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().rangeByScoreWithScores(key, minScore,
                maxScore, offset, count);
        log.info("zRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
        return entries;
    }


    /**
     * 获取时, 先按score倒序, 然后根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的item项集
     *
     * @see #zRange(String, long, long)。 只是zReverseRange这里会提前多一个倒序。
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        log.info("zReverseRange(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        Set<String> entries = redisTemplate.opsForZSet().reverseRange(key, start, end);
        log.info("zReverseRange(...) => entries -> {}", entries);
        return entries;
    }

    /**
     * 获取时, 先按score倒序, 然后根据索引位置， 获取(key对应的)zset中排名处于[start, end]中的entry集
     *
     * @see #zRangeWithScores(String, long, long)。 只是zReverseRangeWithScores这里会提前多一个倒序。
     */
    public Set<TypedTuple<String>> zReverseRangeWithScores(String key, long start, long end) {
        log.info("zReverseRangeWithScores(...) => key -> {}, start -> {}, end -> {}", key, start, end);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        log.info("zReverseRangeWithScores(...) => entries -> {}", JSON.toJSONString(entries));
        return entries;
    }

    /**
     * 获取时, 先按score倒序, 然后根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的item项集
     *
     * @see #zRangeByScore(String, double, double)。 只是zReverseRangeByScore这里会提前多一个倒序。
     */
    public Set<String> zReverseRangeByScore(String key, double minScore, double maxScore) {
        log.info("zReverseRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}",
                key, minScore, maxScore);
        Set<String> items = redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore);
        log.info("zReverseRangeByScore(...) => items -> {}", items);
        return items;
    }

    /**
     * 获取时, 先按score倒序, 然后获取(key对应的)zset中的所有score处于[minScore, maxScore]中的entry
     *
     * @see #zRangeByScoreWithScores(String, double, double)。 只是zReverseRangeByScoreWithScores这里会提前多一个倒序。
     */
    public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(String key, double minScore, double maxScore) {
        log.info("zReverseRangeByScoreWithScores(...) => key -> {}, minScore -> {}, maxScore -> {}",
                key, minScore, maxScore);
        Set<TypedTuple<String>> entries = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,
                minScore, maxScore);
        log.info("zReverseRangeByScoreWithScores(...) => entries -> {}", JSON.toJSONString(entries));
        return entries;
    }

    /**
     * 获取时, 先按score倒序, 然后根据score， 获取(key对应的)zset中分数值处于[minScore, maxScore]中的,
     * score处于[minScore,排名大于等于offset的count个item项
     *
     * @see #zRangeByScore(String, double, double, long, long)。 只是zReverseRangeByScore这里会提前多一个倒序。
     */
    public Set<String> zReverseRangeByScore(String key, double minScore, double maxScore, long offset, long count) {
        log.info("zReverseRangeByScore(...) => key -> {}, minScore -> {}, maxScore -> {}, offset -> {}, "
                + "count -> {}", key, minScore, maxScore, offset, count);
        Set<String> items = redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore, offset, count);
        log.info("items -> {}", items);
        return items;
    }

    /**
     * 统计(key对应的zset中)score处于[minScore, maxScore]中的item的个数
     *
     * @param key      定位zset的key
     * @param minScore score下限
     * @param maxScore score上限
     * @return [minScore, maxScore]中item的个数
     */
    public long zCount(String key, double minScore, double maxScore) {
        log.info("zCount(...) => key -> {}, minScore -> {}, maxScore -> {}", key, minScore, maxScore);
        Long count = redisTemplate.opsForZSet().count(key, minScore, maxScore);
        log.info("zCount(...) => count -> {}", count);
        if (count == null) {
            throw new RedisOpsResultIsNullException();
        }
        return count;
    }

    /**
     * 统计(key对应的)zset中item的个数
     * <p>
     * 注: 此方法等价于{@link #zZCard(String)}
     *
     * @param key 定位zset的key
     * @return zset中item的个数
     */
    public long zSize(String key) {
        log.info("zSize(...) => key -> {}", key);
        Long size = redisTemplate.opsForZSet().size(key);
        log.info("zSize(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 统计(key对应的)zset中item的个数
     * <p>
     * 注: 此方法等价于{@link #zSize(String)}
     *
     * @param key 定位zset的key
     * @return zset中item的个数
     */
    public long zZCard(String key) {
        log.info("zZCard(...) => key -> {}", key);
        Long size = redisTemplate.opsForZSet().zCard(key);
        log.info("zZCard(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 统计(key对应的)zset中指定item的score
     *
     * @param key  定位zset的key
     * @param item zset中的item
     * @return item的score
     */
    public double zScore(String key, Object item) {
        log.info("zScore(...) => key -> {}, item -> {}", key, item);
        Double score = redisTemplate.opsForZSet().score(key, item);
        log.info("zScore(...) => score -> {}", score);
        if (score == null) {
            throw new RedisOpsResultIsNullException();
        }
        return score;
    }

    /**
     * 获取两个(key对应的)ZSet的并集, 并将结果add到storeKey对应的ZSet中。
     * <p>
     * 注: 和set一样，zset中item是唯一的， 在多个zset进行Union时, 处理相同的item时， score的值会变为对应的score之和，如：
     * RedisUtil.zAdd("name1", "a", 1);和RedisUtil.zAdd("name2", "a", 2);
     * 对(name1和name2对应的)zset进行zUnionAndStore之后，新的zset中的项a,对应的score值为3
     * <p>
     * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)ZSet中
     * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将并集添加到(storeKey对应的)ZSet中
     * case3: 交集为空, 则不进行下面的操作, 直接返回0
     *
     * @param key      定位其中一个zset的键
     * @param otherKey 定位另外的zset的键
     * @param storeKey 定位(要把交集添加到哪个)set的key
     * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
     */
    public long zUnionAndStore(String key, String otherKey, String storeKey) {
        log.info("zUnionAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}", key, otherKey, storeKey);
        Long size = redisTemplate.opsForZSet().unionAndStore(key, otherKey, storeKey);
        log.info("zUnionAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取两个(key对应的)ZSet的并集, 并将结果add到storeKey对应的ZSet中。
     * <p>
     * 注: 和set一样，zset中item是唯一的， 在多个zset进行Union时, 处理相同的item时， score的值会变为对应的score之和，如：
     * RedisUtil.zAdd("name1", "a", 1);和RedisUtil.zAdd("name2", "a", 2);
     * 对(name1和name2对应的)zset进行zUnionAndStore之后，新的zset中的项a,对应的score值为3
     * <p>
     * case1: 并集不为空, storeKey不存在， 则 会创建对应的storeKey，并将并集添加到(storeKey对应的)ZSet中
     * case2: 并集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将并集添加到(storeKey对应的)ZSet中
     * case3: 并集为空, 则不进行下面的操作, 直接返回0
     *
     * @param key       定位其中一个set的键
     * @param otherKeys 定位其它set的键集
     * @param storeKey  定位(要把并集添加到哪个)set的key
     * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
     */
    public long zUnionAndStore(String key, Collection<String> otherKeys, String storeKey) {
        log.info("zUnionAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}", key, otherKeys, storeKey);
        Long size = redisTemplate.opsForZSet().unionAndStore(key, otherKeys, storeKey);
        log.info("zUnionAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取两个(key对应的)ZSet的交集, 并将结果add到storeKey对应的ZSet中。
     * <p>
     * 注: 和set一样，zset中item是唯一的， 在多个zset进行Intersect时, 处理相同的item时， score的值会变为对应的score之和，如：
     * RedisUtil.zAdd("name1", "a", 1);
     * RedisUtil.zAdd("name1", "b", 100);
     * 和R
     * edisUtil.zAdd("name2", "a", 2);
     * edisUtil.zAdd("name2", "c", 200);
     * 对(name1和name2对应的)zset进行zIntersectAndStore之后，新的zset中的项a,对应的score值为3
     * <p>
     * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)ZSet中
     * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将交集添加到(storeKey对应的)ZSet中
     * case3: 交集为空, 则不进行下面的操作, 直接返回0
     *
     * @param key      定位其中一个ZSet的键
     * @param otherKey 定位其中另一个ZSet的键
     * @param storeKey 定位(要把交集添加到哪个)ZSet的key
     * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
     */
    public long zIntersectAndStore(String key, String otherKey, String storeKey) {
        log.info("zIntersectAndStore(...) => key -> {}, otherKey -> {}, storeKey -> {}", key, otherKey, storeKey);
        Long size = redisTemplate.opsForZSet().intersectAndStore(key, otherKey, storeKey);
        log.info("zIntersectAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 获取多个(key对应的)ZSet的交集, 并将结果add到storeKey对应的ZSet中。
     * <p>
     * case1: 交集不为空, storeKey不存在， 则 会创建对应的storeKey，并将交集添加到(storeKey对应的)ZSet中
     * case2: 交集不为空, storeKey已存在， 则 会清除原(storeKey对应的)ZSet中所有的项，然后将交集添加到(storeKey对应的)ZSet中
     * case3: 交集为空, 则不进行下面的操作, 直接返回0
     *
     * @param key       定位其中一个set的键
     * @param otherKeys 定位其它set的键集
     * @param storeKey  定位(要把并集添加到哪个)set的key
     * @return add到(storeKey对应的)ZSet后, 该ZSet对应的size
     */
    public long zIntersectAndStore(String key, Collection<String> otherKeys, String storeKey) {
        log.info("zIntersectAndStore(...) => key -> {}, otherKeys -> {}, storeKey -> {}",
                key, otherKeys, storeKey);
        Long size = redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, storeKey);
        log.info("zIntersectAndStore(...) => size -> {}", size);
        if (size == null) {
            throw new RedisOpsResultIsNullException();
        }
        return size;
    }

    /**
     * 当使用Pipeline 或 Transaction操作redis时, (不论redis中实际操作是否成功, 这里)结果(都)会返回null。
     * 此时，如果试着将null转换为基本类型的数据时，会抛出此异常。
     * <p>
     * 即: 此工具类中的某些方法, 希望不要使用Pipeline或Transaction操作redis。
     * <p>
     * 注: Pipeline 或 Transaction默认是不启用的， 可详见源码:
     *
     * @see LettuceConnection#isPipelined()
     * @see LettuceConnection#isQueueing()
     * @see JedisConnection#isPipelined()
     * @see JedisConnection#isQueueing()
     */
    public class RedisOpsResultIsNullException extends NullPointerException {

        public RedisOpsResultIsNullException() {
            super();
        }

        public RedisOpsResultIsNullException(String message) {
            super(message);
        }
    }

//
//    /**
//     * 提供一些基础功能支持
//     *
//     */
//    public class Helper {
//
//        /** 默认拼接符 */
//        public final String DEFAULT_SYMBOL = ":";
//
//        /**
//         * 拼接args
//         *
//         * @see Helper#joinBySymbol(String, String...)
//         */
//        public String join(String... args) {
//            return Helper.joinBySymbol(DEFAULT_SYMBOL, args);
//        }
//
//        /**
//         * 使用symbol拼接args
//         *
//         * @param symbol
//         *            分隔符， 如: 【:】
//         * @param args
//         *            要拼接的元素数组, 如: 【a b c】
//         *
//         * @return  拼接后的字符串, 如  【a:b:c】
//         */
//        public String joinBySymbol(String symbol, String... args) {
//            if (symbol == null || symbol.trim().length() == 0) {
//                throw new RuntimeException(" symbol must not be empty!");
//            }
//            if (args == null || args.length == 0) {
//                throw new RuntimeException(" args must not be empty!");
//            }
//            StringBuilder sb = new StringBuilder(16);
//            for (String arg : args) {
//                sb.append(arg).append(symbol);
//            }
//            sb.replace(sb.length() - symbol.length(), sb.length(), "");
//            return sb.toString();
//        }
//
//    }
}
