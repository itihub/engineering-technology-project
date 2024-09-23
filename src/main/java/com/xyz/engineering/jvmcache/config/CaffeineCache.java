package com.xyz.engineering.jvmcache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CaffeineCache {

    private final LoadingCache<Integer, Optional<SystemConfig>> systemConfigLoadingCache =
            Caffeine.newBuilder()
                    // 初始容量
                    .initialCapacity(10)
                    // 最大容量
                    .maximumSize(100)
                    // 缓存写入后的过期时间
                    .expireAfterWrite(60, TimeUnit.SECONDS)
                    // 缓存刷新周期
                    .refreshAfterWrite(60, TimeUnit.SECONDS)
                    // 监听缓存被移除
                    .removalListener((key, val, removalCache) -> {
                        log.info("{}, {}, {}", key, val, removalCache);
                    })
                    .recordStats()
                    .build((key) -> {
                        // 从DB中加载数据并返回
                        return Optional.empty();
                    });

    public SystemConfig getConfigById(Integer configId) {
        return systemConfigLoadingCache.get(configId).orElse(null);
    }

    /**
     * 清除缓存
     */
    public void clear() {
        // 单个清除
        systemConfigLoadingCache.invalidate(1);

        // 批量清除
        systemConfigLoadingCache.invalidateAll(Arrays.asList(1, 2, 3));

        // 清除所有的缓存项
        systemConfigLoadingCache.invalidateAll();
    }

    /**
     * 打印缓存统计信息
     */
    public void systemConfigLoadingCacheStat() {
        log.info("stat info: {}", systemConfigLoadingCache.stats());
    }
}
