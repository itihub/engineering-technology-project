package com.xyz.engineering.coupon.service;

import com.xyz.engineering.coupon.entity.CouponTemplate;
import com.xyz.engineering.coupon.entity.UserCoupon;

import java.util.List;

/**
 * 优惠券通用功能接口定义
 */
public interface ICouponCommonService {

    /**
     * 修改用户优惠券
     * 演示解决缓存与数据表不一致的问题
     * 通用的修改用户优惠券的场景
     *
     * @param userCoupon
     */
    void modifyUserCoupon(UserCoupon userCoupon);

    /**
     * 给定 template ids 查询优惠券模版
     * 演示解决缓存穿透问题
     * @param templateIds
     * @return
     */
    List<CouponTemplate> queryCouponTemplate(List<Long> templateIds);

    /**
     * 给定 userId 和 couponIds 查询用户优惠券信息
     * 演示解决缓冲穿透问题
     * @param userId
     * @param couponIds
     * @return
     */
    List<UserCoupon> queryUserCoupon(Long userId, List<Long> couponIds);

}
