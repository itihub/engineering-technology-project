package com.xyz.engineering.coupon.entity;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCoupon {

    private Long id;
    private Date createTime;
    private Date updateTime;

    private Long userId;
    private Long templateId;
    private Long couponId;
    private Date collectionTime;    // 领取时间
    private boolean isWriteOff;     // 是否已经使用过了
    private Long orderId;           // 如果使用了，则这个字段关联到对应的订单id
    private Date writeOffTime;      // 核销时间，冗余定义，方便业务查询
}
