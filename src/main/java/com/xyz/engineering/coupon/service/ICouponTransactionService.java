package com.xyz.engineering.coupon.service;

import com.xyz.engineering.coupon.entity.UserCoupon;

import java.util.List;

/**
 * 事务相关的服务接口定义
 */
public interface ICouponTransactionService {

    void completeOrder_A(List<UserCoupon> coupons);

    void completeOrder_B(List<UserCoupon> coupons);

}
