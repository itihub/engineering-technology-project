package com.xyz.engineering.coupon.writeoff;

import com.xyz.engineering.coupon.entity.UserCoupon;

/**
 * 用户优惠券核销服务接口定义
 */
public interface ICouponWriteOffService {

    /**
     * 核销功能
     * @param userCoupon
     */
    void writeOff(UserCoupon userCoupon);

    /**
     * 优惠券类型
     * @return
     */
    CouponTypeEnum type();

}
