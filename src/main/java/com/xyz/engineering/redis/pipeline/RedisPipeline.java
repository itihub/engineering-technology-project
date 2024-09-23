package com.xyz.engineering.redis.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonBatch;
import org.redisson.api.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPipeline implements InitializingBean {

    private final RedissonClient redissonClient;

    private BatchOptions batchOptions;
    private BatchOptions batchOptionsNoReturn;
    private BatchOptions batchOptionsExample;

    /**
     * Bean初始化
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        /*
            管道的执行模式有三种
            REDIS_READ_ATOMIC 可读，所有的命令都缓存到Redis节点里面，以原子性事务的方式去执行
            REDIS_WRITE_ATOMIC 可写，所有的命令都缓存到Redis节点里面，以原子性事务的方式去执行
            IN_MEMORY 默认方式，所有的命令都缓存到客户端本机内存里面进行发送，逐一执行
            IN_MEMORY_ATOMIC 默认方式，所有的命令都缓存到客户端本机内存里面进行发送，以原子性事务的方式去执行
         */
        // 指定管道的执行模式
        batchOptions = BatchOptions.defaults()
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY);
        batchOptionsNoReturn = BatchOptions.defaults()
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY).skipResult();
        batchOptionsExample = BatchOptions.defaults()
                .executionMode(BatchOptions.ExecutionMode.IN_MEMORY)
                // 告知不用返回结果到客户端，来减少网络流量
                .skipResult()
                // 超时时间
                .responseTimeout(2, TimeUnit.SECONDS)
                // 重试等待间隔时间
                .retryInterval(2, TimeUnit.SECONDS)
                // 重试次数
                .retryAttempts(4);
    }

    /**
     * 创建管道方法
     * @return
     */
    public RBatch createBatch() {
        return redissonClient.createBatch(batchOptions);
    }

    public RBatch createBatch(boolean skipResult) {
        return skipResult ? redissonClient.createBatch(batchOptions)
                : redissonClient.createBatch(batchOptionsNoReturn);
    }

    /**
     * 提交管道执行
     * @param batch
     * @return
     */
    public BatchResult executeBtch(RBatch batch) {
        try {
            // 同步执行
            return batch.execute();
            // 异步执行
            //RFuture<BatchResult<?>> asyncRes = batch.executeAsync();
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 使用管道
     */
    public void useBatch() {
        RBatch batch = createBatch();
        // 批量添加命令
        batch.getMap("map:0322").putAllAsync(new HashMap<>());
        batch.getBucket("bucket:0322").setAsync(new Object());
        // ......
        executeBtch(batch);
    }
}
