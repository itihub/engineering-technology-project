package com.xyz.engineering.redis.problem;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 解决Redis缓存雪崩问题
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheSnowSlide {

    private final RedissonClient redissonClient;

    /**
     * 解决方案一：打散过期时间，为过期时间添加随机值
     */
    public void solution01() {
        // 默认过期时间
        int defaultExpireSeconds = 60;
        // 打散过期时间
        int targetExpireSeconds = defaultExpireSeconds + randomExpireSecond();

        redissonClient.getBucket("expire:0322").setAsync(new Object(),
                targetExpireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 解决方案二：添加互斥锁
     */
    public String solution02() {
        String dataKey = "expire:0322";
        String cacheData = (String) redissonClient.getBucket(dataKey).get();

        // 如果不存在缓存，使用互斥锁实现缓存的刷写
        if (StringUtils.isBlank(cacheData)) {
            RLock lock = redissonClient.getLock("lock:0322");
            try {
                // 尝试获取锁，1000是尝试获取锁的最长等待时间，2000是获取锁的最长持有时间
                if (lock.tryLock(1000, 2000, TimeUnit.MILLISECONDS)) {
                    // TODO 从数据中获取数据，并写入缓存
                    return cacheData;
                }
            } catch (Exception ex) {
                log.error("....", ex);
            } finally {
                lock.unlock();
            }
        }

        return cacheData;
    }

    /**
     * 生成随机数
     * @return
     */
    private int randomExpireSecond() {
        return RandomUtils.nextInt(10, 100);
    }
}
