package com.xyz.engineering.coupon.cache;

import com.xyz.engineering.coupon.entity.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户优惠券缓存工具类
 */
@Service
@Component
@RequiredArgsConstructor
public class UserCouponCacheHelper {

    private final RedissonClient redissonClient;

    private final static String USER_COUPON_CACHE_KEY = "user:coupon:%s";
    private final static Long USER_COUPON_CACHE_TIME = 60L;

    /**
     * 把用户优惠券加入到缓存中
     * @param userId
     * @param userCoupons
     */
    public void setUserCouponToCache(Long userId, Collection<UserCoupon> userCoupons) {

        // couponId -> UserCoupon
        Map<Long, UserCoupon> couponId2Coupon = userCoupons.stream()
                .collect(Collectors.toMap(UserCoupon::getCouponId, Function.identity()));

        // 批量存储数据（分批词）
        redissonClient.getMap(buildUserCouponCacheKey(userId))
                .putAllAsync(couponId2Coupon, 1000);

        /*
        // 设置过期时间
        redissonClient.getMap(buildUserCouponCacheKey(userId))
                .expire(Duration.ofMinutes(USER_COUPON_CACHE_TIME));
        */

        // 设置过期时间并随机化，规避缓存雪崩问题
        redissonClient.getMap(buildUserCouponCacheKey(userId))
                .expire(Duration.ofMinutes(USER_COUPON_CACHE_TIME + randomExpireMinute()));

        // 缓存击穿问题很少会出现用户数据中，通常像秒杀类业务配置数据 大流量、高并发
    }

    /**
     * 生成5~30之间的随机数
     * 针对于缓存雪崩的问题，使得缓存过期时间随机化
     * @return
     */
    private int randomExpireMinute() {
        return RandomUtils.nextInt(5, 30);
    }

    /**
     * 从缓存中读取用户优惠券
     * @param userId
     * @param couponIds
     * @return
     */
    public Map<Long, UserCoupon> getUserCouponFromCache(Long userId, Set<Long> couponIds) {
        RMap<Long, UserCoupon> couponId2Coupon = redissonClient.getMap(buildUserCouponCacheKey(userId));
        return couponId2Coupon.getAll(couponIds);
    }

    /**
     * 清理用户优惠券缓存
     * @param userId
     * @param couponIds
     */
    public void delete(Long userId, Set<Long> couponIds) {
        RMap<Long, UserCoupon> couponId2Coupon = redissonClient.getMap(buildUserCouponCacheKey(userId));
        couponId2Coupon.fastRemove(couponIds.toArray(new Long[]{}));
    }

    private String buildUserCouponCacheKey(Long userId) {
        return String.format(USER_COUPON_CACHE_KEY, userId);
    }
}
