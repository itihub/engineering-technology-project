package com.xyz.engineering.coupon.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.xyz.engineering.coupon.entity.CouponTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 优惠券模版缓存工具类
 */
@Slf4j
@Component
public class CouponTemplateCacheHelper {

    private final LoadingCache<Long, Optional<CouponTemplate>> couponTemplateLoadingCache
            = CacheBuilder.newBuilder()
            .initialCapacity(1000)  // 初始化容量
            .maximumSize(10000) // 最大容量
            .concurrencyLevel(Runtime.getRuntime().availableProcessors())   // 并发级别
            .expireAfterWrite(600, TimeUnit.SECONDS)    // 写入后缓存过期时间
            .build(new CacheLoader<Long, Optional<CouponTemplate>>() {
                @Override
                public Optional<CouponTemplate> load(Long templateId) throws Exception {
                    // TODO 根据templateId去数据表查询数据 并写入缓存
                    return Optional.of(new CouponTemplate());
                }
            });

    public CouponTemplate getCouponTemplateByTemplateId(Long templateId) {
        try {
            return couponTemplateLoadingCache.get(templateId).orElse(null);
        } catch (ExecutionException ex) {
            log.error("get cache fail", ex);
        }
        return null;
    }

    public Map<Long, Optional<CouponTemplate>> getCouponTemplateByTemplateId(List<Long> templateIds) {
        try {
            return couponTemplateLoadingCache.getAll(templateIds);
        } catch (ExecutionException ex) {
            log.error("get cache fail", ex);
        }
        return null;
    }

    public void putCouponTemplateToCache(CouponTemplate template) {
        couponTemplateLoadingCache.put(template.getTemplateId(), Optional.of(template));
    }

    public void putCouponTemplateToCache(Map<Long, Optional<CouponTemplate>> templateMap) {
        couponTemplateLoadingCache.putAll(templateMap);
    }

    public void cleanCouponTemplateCouponByTemplateId(Long templateId) {
        couponTemplateLoadingCache.invalidate(templateId);
    }
}
