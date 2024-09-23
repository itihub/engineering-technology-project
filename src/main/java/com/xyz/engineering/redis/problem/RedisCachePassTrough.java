package com.xyz.engineering.redis.problem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存穿透解决方案
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCachePassTrough {

    private final RedissonClient redissonClient;

    /**
     * 解决方式一：针对缓存和数据库中不存在的数据，进行缓存空值来解决
     */
    public String solution() {
        String dataKey = "expire:0322";
        String cacheData = (String) redissonClient.getBucket(dataKey).get();

        if (StringUtils.isBlank(cacheData)) {
            // 缓存和数据库中不存在的数据
            boolean isEmptyData = fakeCheckLogic();
            if (isEmptyData) {
                // 构建一个随机值进行缓存
                String fakeData = NumberUtils.INTEGER_MINUS_ONE.toString();
                redissonClient.getBucket(dataKey).setAsync(fakeData, 1, TimeUnit.DAYS);
                return fakeData;
            }
        }
        return cacheData;
    }

    /**
     * 模拟校验请求参数
     * @return
     */
    private boolean fakeCheckLogic() {
        return RandomUtils.nextInt(10, 100) > 50;
    }
}
