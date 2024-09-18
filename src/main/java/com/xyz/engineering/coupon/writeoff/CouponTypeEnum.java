package com.xyz.engineering.coupon.writeoff;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券类型枚举
 */
@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    MAN_JIAN("man_jian", "满减券"),
    MAN_FAN("man_fan", "满返券"),
    ZHE_KOU("zhe_kou", "折扣券"),
    ;

    private final String type;
    private final String desciption;
}
