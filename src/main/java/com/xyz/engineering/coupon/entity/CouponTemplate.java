package com.xyz.engineering.coupon.entity;


import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTemplate {

    private Long id;
    private Date createTime;
    private Date updateTime;

    private Long templateId;        // 雪花算法
    private String templateType;    // 枚举
    private String templateName;
    private String templateDescription;
    private String templateCover;
    private BigDecimal lowerLimit;  //  使用下限，最低的消费金额
    // 在不同的优惠券类型中具有不同的含义；例如折扣、满减、满返
    private BigDecimal faceValue;   // 面额
    private Date startTime;
    private Date endTime;
    private Long totalAmount;       // 总量
    private Integer receiptQuantityLimit;   // 领取个数限制
}
