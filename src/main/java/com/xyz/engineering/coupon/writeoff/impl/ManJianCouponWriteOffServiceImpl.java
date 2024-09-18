package com.xyz.engineering.coupon.writeoff.impl;

import com.xyz.engineering.coupon.entity.UserCoupon;
import com.xyz.engineering.coupon.writeoff.CouponTypeEnum;
import com.xyz.engineering.coupon.writeoff.ICouponWriteOffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 满减优惠券业务核销逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManJianCouponWriteOffServiceImpl implements ICouponWriteOffService {

    @Override
    public void writeOff(UserCoupon userCoupon) {
        // TODO 核销业务逻辑 ...
    }

    @Override
    public CouponTypeEnum type() {
        return CouponTypeEnum.MAN_JIAN;
    }
}
