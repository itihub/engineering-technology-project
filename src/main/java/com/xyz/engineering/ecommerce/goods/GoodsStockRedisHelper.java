package com.xyz.engineering.ecommerce.goods;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * 使用Redis缓存进行商品预减库存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsStockRedisHelper implements InitializingBean {

    private RScript rScript;

    // 脚本的唯一标识
    private String sha1digest;

    private final static String DECR_STOCK_SCRIPT = "redis_script/decrstock.lua";
    private final static String GOODS_STOCK_CACHE_KEY = "ecommerce:goods:stock:%s";

    private final RedissonClient redissonClient;

    /**
     * 初始化Bean方法
     * 加载预减库存的lua脚本
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        // 加载预扣减库存的lua脚本
        rScript = redissonClient.getScript(IntegerCodec.INSTANCE);
        String decrStockString = new ResourceScriptSource(
                new ClassPathResource(DECR_STOCK_SCRIPT)).getScriptAsString();
        // 加载Lua脚本并返回脚本唯一标识
        sha1digest = rScript.scriptLoad(decrStockString);
        log.info("load decr stock script done: [sha1digest={}]", sha1digest);
    }

    /**
     * 初始化商品库存
     * @param goodsId
     * @param stock
     */
    public void initGoodsStock(String goodsId, Long stock) {
        incrGoodsStock(goodsId, stock);
    }

    /**
     * 增加商品库存
     * 用于下单失败预减库存缓存值的回滚
     * @param goodsId
     * @param stock
     */
    public void incrGoodsStock(String goodsId, Long stock) {
        redissonClient.getAtomicLong(String.format(GOODS_STOCK_CACHE_KEY, goodsId))
                .addAndGet(stock);
    }

    /**
     * 尝试预扣减库存
     * @param goodsId
     * @param stock
     * @return
     */
    public boolean tryDecreaseStock(String goodsId, Long stock) {
        // TODO goods 是否有效
        if (stock <= 0) {
            return false;
        }

        // 执行预扣减库存脚本
        Long result = rScript.evalSha(
                RScript.Mode.READ_WRITE,
                sha1digest,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(String.format(GOODS_STOCK_CACHE_KEY, goodsId)),
                stock.toString()
        );

        // 返回结果
        return Optional.ofNullable(result).map(Object::toString)
                .map(o -> Objects.equals("1", o))
                .orElse(false);
    }
}
