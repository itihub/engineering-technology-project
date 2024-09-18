package com.xyz.engineering.ecommerce.goods;

import lombok.Data;

/**
 * 商品实体表简略定义
 */
@Data
public class Goods {

    private Long id;
    private String goodsId;
    private Long stock;     // 商品库存
    private Long soldOut;   // 商品已经售卖的个数

}
