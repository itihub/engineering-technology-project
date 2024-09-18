package com.xyz.engineering.ecommerce.goods;

/**
 * 商品表Mapper
 */
public interface GoodsMapper {

    /**
     * 使用乐观锁扣减库存：更新sold_out字段 也就是售卖个数
     * update goods set sold_out = sold_out + #{number}
     *      where goods_id = #{goodsId} and stock >= sold_out + #{number}
     * @param goodsId
     * @param number
     * @return
     */
    int decreaseStock(String goodsId, Integer number);
}
