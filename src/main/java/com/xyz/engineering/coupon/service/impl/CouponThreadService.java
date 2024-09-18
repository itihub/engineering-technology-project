package com.xyz.engineering.coupon.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xyz.engineering.coupon.cache.CouponTemplateCacheHelper;
import com.xyz.engineering.coupon.entity.CouponTemplate;
import com.xyz.engineering.coupon.entity.UserCoupon;
import com.xyz.engineering.coupon.service.ICouponThreadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * 一些异步任务、多线程处理的服务接口实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponThreadService implements ICouponThreadService, InitializingBean {

    private final CouponTemplateCacheHelper couponTemplateCacheHelper;

    /* 用户优惠券校验所使用的线程池 */
    private ThreadPoolExecutor userCouponCheckExecutor;

    private final ObjectMapper objectMapper;

    /**
     * 初始化Bean方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 完成线程池的初始化
        userCouponCheckExecutor = new ThreadPoolExecutor(
              4,
              8,
              1,
                TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(1000),
                new ThreadFactoryBuilder().setNameFormat("user-coupon-check-worker-%s").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 刷新优惠券模版缓存
        refreshCouponTemplateCache();
    }

    @Override
    public void refreshCouponTemplateCache() {
        // 定时任务线程池
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
                1,
                new BasicThreadFactory.Builder()
                        .namingPattern("refresh-coupon-template-pool-%d")
                        .daemon(true).build()
        );

        // 定时主动刷写 coupon template  设置延迟时间和间隔时间
        executorService.scheduleAtFixedRate(() -> {
            // TODO 从DB中读取 coupon template 数据 并写入缓存
            Map<Long, Optional<CouponTemplate>> templateMap = new HashMap<>();
            couponTemplateCacheHelper.putCouponTemplateToCache(templateMap);
        }, 0, 30L, TimeUnit.MINUTES);

    }

    /**
     * 使用多线程校验 user coupon 的合法性
     * @param coupons
     * @return
     */
    @Override
    public boolean checkUserCouponValid(List<UserCoupon> coupons) {
        if (CollectionUtils.isEmpty(coupons)) {
            return true;
        }

        // 使用CountDownLatch 做倒计时来判断所有任务是否已经执行完成
        CountDownLatch latch = new CountDownLatch(coupons.size());

        coupons.forEach(c -> {
            // TODO 做一些额外的前置处理

            // 提交到线程池
            userCouponCheckExecutor.execute(() -> {
                try {
                    // 线程任务：校验 user coupon 的合法性
                } catch (Exception ex) {
                    log.error("...", ex);
                } finally {
                    // 任务执行完成
                    latch.countDown();
                    try {
                        log.info("checked user coupon: [{}]", objectMapper.writeValueAsString(c));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        });

        // 根据校验结果返回合法性
        return false;
    }


}
