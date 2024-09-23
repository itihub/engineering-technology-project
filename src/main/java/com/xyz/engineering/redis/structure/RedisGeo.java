package com.xyz.engineering.redis.structure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Geo存储地理位置信息，底层存储本质上是ZSet
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisGeo {

    private final RedissonClient redissonClient;

    public void useGeo() {
        // 存储地址位置信息
        RGeo<String> geo = redissonClient.getGeo("geo:0322");
        // 新增地理元素 （经度纬度）
        geo.add(new GeoEntry(121.47, 31.21, "上海"));
        geo.add(new GeoEntry(116.47, 39.21, "北京"));

        // 计算两个地理元素之间的距离
        double distance = geo.dist("北京", "上海", GeoUnit.KILOMETERS);

        // 获取元素的经纬度信息
        Map<String, GeoPosition> pos = geo.pos("北京", "上海");

        // 实际的应用，统计上海坐标点100公里内的10个元素
        GeoSearchArgs args_01 = GeoSearchArgs.from("上海")
                .radius(100, GeoUnit.KILOMETERS)
                .order(GeoOrder.ASC)
                .count(10);
        Map<String, Double> radiusWithDistance = geo.searchWithDistance(args_01);

        // 仅返回名字
        List<String> cities = geo.search(args_01);

        // 查询经度120 纬度30 附近500公里以内的元素名称和距离
        GeoSearchArgs args_02 = GeoSearchArgs.from(120, 30)
                .radius(500, GeoUnit.KILOMETERS)
                .order(GeoOrder.ASC)
                .count(10);
        Map<String, Double> radiusWithDistance02 = geo.searchWithDistance(args_02);

    }
}
