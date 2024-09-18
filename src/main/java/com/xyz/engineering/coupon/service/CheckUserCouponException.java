package com.xyz.engineering.coupon.service;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 校验优惠券异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CheckUserCouponException extends RuntimeException {

    private Integer code;

    private String message;

    public CheckUserCouponException() {
        super();
    }

}
