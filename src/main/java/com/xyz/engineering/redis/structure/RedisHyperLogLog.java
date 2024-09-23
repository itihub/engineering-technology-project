package com.xyz.engineering.redis.structure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 基数统计，计算一个集合中不重复元素的个数
 * 是统计的策略或算法，类似sql中的distinct函数
 * hyperloglog 占用的每个键都是12k
 * 有误差，带有0.81%标准错误的近似值
 * 适合统计UV、周活、日活、用户搜索的关键词等场景
 * 小数据量使用Set数据结构，没有超过2的32次方使用BitMap，超过后使用HyperLogLog
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisHyperLogLog {

    private final RedissonClient redissonClient;

    public void useHyperLoglog() {
        // 主要命令：PFADD、PFCOUNT、PFMERGE

        // 统计日活
        RHyperLogLog<Long> hyperLogLog_0322 =
                redissonClient.getHyperLogLog("hyperloglog:0322");

        // 添加数据
        hyperLogLog_0322.add(1L);
        hyperLogLog_0322.add(10L);
        hyperLogLog_0322.add(100L);
        hyperLogLog_0322.addAll(Arrays.asList(1000L, 10000L));

        // 统计已存储的元素，是估计值 不是精确值
        long hyperLogLog_0322_count = hyperLogLog_0322.count();

        // 把多个hyperloglog数据 合并在一起
        RHyperLogLog<Long> hyperLogLog_0323 =
                redissonClient.getHyperLogLog("hyperloglog:0323");
        hyperLogLog_0323.add(1L);
        hyperLogLog_0323.add(10L);

        // 把两个hyperloglog合并在一起，取的是并集，用于统计周活、月活
        hyperLogLog_0323.mergeWith("hyperloglog:0322");
        long hyperLogLog_0322_0323_count = hyperLogLog_0323.count();
    }
}
