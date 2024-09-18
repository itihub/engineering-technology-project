package com.xyz.engineering.coupon.service;

import com.xyz.engineering.coupon.entity.UserCoupon;

import java.util.List;

/**
 * 一些异步任务，多线程处理的服务接口定义
 * core java
 */
public interface ICouponThreadService {

    /**
     * 刷新 coupon template 缓存
     * 异步任务
     */
    void refreshCouponTemplateCache();

    /**
     * 对用户优惠券进行有效性校验
     * 多线程
     * @param coupons
     * @return
     */
    boolean checkUserCouponValid(List<UserCoupon> coupons);

}
