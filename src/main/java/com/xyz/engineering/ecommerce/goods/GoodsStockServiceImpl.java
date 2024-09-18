package com.xyz.engineering.ecommerce.goods;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 商品库存服务接口实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsStockServiceImpl {

    private final GoodsMapper goodsMapper;

    private final GoodsStockRedisHelper goodsStockRedisHelper;

    /**
     * 扣减商品库存
     * 1.从redis中预扣减库存
     * 2.再使用乐观锁更新db
     * @param goodsId
     * @param number
     */
    public void decreaseStock(String goodsId, Integer number) {
        // 1.从redis中预扣减库存
        boolean decrCacheStockResult =
                goodsStockRedisHelper.tryDecreaseStock(goodsId, number.longValue());
        // 如果成功，在使用乐观锁更新db
        if (decrCacheStockResult) {
            int decrDBStockResult = goodsMapper.decreaseStock(goodsId, number);
            // TODO 根据扣减结果执行后续的业务逻辑

            // 1.扣减cache中库存成功，但是扣减DB库存失败，需要回滚或补偿cache中的库存
            // 2.扣减cache中库存成功，但是扣减DB库存失败，可能是网络问题也能是cache和db中的数据不一致，需要人工介入
            // 3.扣减cache中库存成功，但是扣减DB库存成功，但是后续业务逻辑处理失败了(用户超时、未支付等)，恢复cache和db中的数据
        }
    }
}
