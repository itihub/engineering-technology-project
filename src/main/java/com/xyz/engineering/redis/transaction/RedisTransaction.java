package com.xyz.engineering.redis.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.transaction.TransactionException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis事务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTransaction {

    private final RedissonClient redissonClient;

    /**
     * 使用Redis事务
     */
    public void useTransaction() {
        // 事务配置信息
        TransactionOptions options = TransactionOptions.defaults()
                // 处理结果超时时间
                .responseTimeout(3, TimeUnit.SECONDS)
                // 重试时间间隔（针对未发送的命令，而执行出错的命令不会重试的）
                .retryInterval(2, TimeUnit.SECONDS)
                // 重试次数
                .retryAttempts(3)
                // 事务超时时间，在这个时间段内事务没执行完直接回滚
                .timeout(5, TimeUnit.SECONDS);
        // 创建事务
        RTransaction transaction = redissonClient.createTransaction(options);
        // 往事务中添加命令
        RMap<String, String> map = transaction.getMap("map:0322");
        map.put("text", "qinyi");
        String value = map.get("kb");
        RSet<String> set = transaction.getSet("set:0322");
        set.add(value);

        // 提交事务
        try {
            // Redis事务是通过分布式锁来保证连续写入的原子性，在内部通过操作指令来实现提交和回滚。
            transaction.commit();
        } catch (TransactionException ex) {
            transaction.rollback();
        }
    }
}
