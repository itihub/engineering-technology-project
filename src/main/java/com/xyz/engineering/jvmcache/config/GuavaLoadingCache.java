package com.xyz.engineering.jvmcache.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Guava LoadingCache 的使用
 */
@Slf4j
@Component
public class GuavaLoadingCache {

    private final LoadingCache<Integer, Optional<SystemConfig>> systemConfigLoadingCache =
            CacheBuilder.newBuilder()
                    // 并发级别
                    .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                    // 初始容量
                    .initialCapacity(10)
                    // 最大容量，超出后会按照LRU最近最少使用算法去移出缓存项
                    .maximumSize(100)
                    // 统计缓存情况，会消耗一定性能，生产环境不建议开启
                    .recordStats()
                    // 缓存写入之后的失效时间
                    .expireAfterWrite(60, TimeUnit.SECONDS)
                    // 缓存被访问之后设置失效时间，较少使用
                    //.expireAfterAccess(60, TimeUnit.SECONDS)
                    // 缓存重新加载的时间周期
                    .refreshAfterWrite(60, TimeUnit.SECONDS)
                    // 缓存移出通知
                    .removalListener(notification -> {
                        log.info("{}, {}, {}", notification.getKey(), notification.getKey(), notification.getCause());
                    })
                    .build(new SystemConfigCacheLoader());

    /* 缓存加载逻辑 */
    public static class SystemConfigCacheLoader extends CacheLoader<Integer, Optional<SystemConfig>> {

        @Override
        public Optional<SystemConfig> load(Integer key) throws Exception {
            // TODO 实现从数据库查询缓存
            return Optional.empty();
        }
    }

    public SystemConfig getConfigById(Integer configId) {
        try {
            return systemConfigLoadingCache.get(configId).orElse(null);
        } catch (ExecutionException ex) {
            return null;
        }
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
