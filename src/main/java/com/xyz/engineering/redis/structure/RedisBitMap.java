package com.xyz.engineering.redis.structure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.BitSet;

/**
 * BitMap位图数据结构
 * 与Set对比，不能做And和Or操作
 * 适用于签到、统计、用户状态等场景
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisBitMap {

    private final RedissonClient redissonClient;

    public void useBitMap() {
        long userId = 1L;
        // 统计用户日活
        RBitSet dailyActiveUserBitMap_0321 = redissonClient.getBitSet("bitmap:0321");

        // BitMap基础使用方法
        dailyActiveUserBitMap_0321.set(userId); // 添加方法
        dailyActiveUserBitMap_0321.clear(userId); // 清除方法

        // 获取位图上被设置为 true 的个数
        long trueCount = dailyActiveUserBitMap_0321.cardinality();

        // 获取指定用户的状态
        boolean isTrue = dailyActiveUserBitMap_0321.get(userId);

        // 多个bitmap可以做and、or、xor 操作
        RBitSet dailyActiveUserBitMap_0322 = redissonClient.getBitSet("bitmap:0322");
        // 转换为Java数据结构
        BitSet dailyActiveUserBitMap_0321_bitset = dailyActiveUserBitMap_0321.asBitSet();
        BitSet dailyActiveUserBitMap_0322_bitset = dailyActiveUserBitMap_0322.asBitSet();

        // 将0322的数据 统计到0321里面
        dailyActiveUserBitMap_0321_bitset.and(dailyActiveUserBitMap_0322_bitset);
        int statAndCount = dailyActiveUserBitMap_0321_bitset.cardinality();

    }
}
