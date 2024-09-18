package com.xyz.engineering.coupon.service.impl;

import com.xyz.engineering.coupon.entity.UserCoupon;
import com.xyz.engineering.coupon.service.CheckUserCouponException;
import com.xyz.engineering.coupon.service.ICouponThreadService;
import com.xyz.engineering.coupon.service.ICouponTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponTransactionService implements ICouponTransactionService {

    private final ICouponThreadService couponThreadService;

    /**
     * 正常的多个数据表的更新使用事务
     * @param coupons
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeOrder_A(List<UserCoupon> coupons) {
        // TODO
        // 1.订单表
        // 2.优惠券核销
    }

    /**
     * Transactional注解拦截方法抛出自定义异常避免事务失效
     * @param coupons
     */
    @Transactional(rollbackFor = {CheckUserCouponException.class, Exception.class})
    @Override
    public void completeOrder_B(List<UserCoupon> coupons) {
        // TODO
        // 1.订单表y
        boolean isValid = couponThreadService.checkUserCouponValid(coupons);
        if (!isValid) {
            // 抛出自定义异常
            throw new CheckUserCouponException();
        }
    }
}
