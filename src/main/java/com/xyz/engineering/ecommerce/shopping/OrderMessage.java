package com.xyz.engineering.ecommerce.shopping;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户订单的消息体定义
 */
@Data
@Builder
public class OrderMessage {
    private Long userId;
    private Long orderId;
    private String goodsId;
    private Integer number;
    private BigDecimal orderAmount; // 订单总金额
    private String orderStatus; // 订单状态：新建、支付完成、超时等等
    private Date createOrderTime; // 用户订单创建时间
    private Date payOrderTime; // 用户支付订单的时间；再未完成支付之前，这个字段值没有意义
}
