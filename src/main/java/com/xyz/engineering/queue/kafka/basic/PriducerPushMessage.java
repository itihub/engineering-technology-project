package com.xyz.engineering.queue.kafka.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 生产者发送消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriducerPushMessage {

    /** kafka 客户端 */
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送方式一：异步发送，不管发送结果
     * 效率高，可靠性差
     */
    public void ignoreSend() {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic", "test", "test-value");
        kafkaTemplate.send(record);
    }

    /**
     * 发送方式二：异步发送，同步等待并处理发送结果
     * 效率低，耗时长，可靠性高
     */
    public void syncSend() {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic", "test", "test-value");
        try {
            SendResult<String, String> result = kafkaTemplate.send(record).get();
            // 可以判断发送结果，然后做一些操作
        } catch (Exception ex) {
            log.error("...", ex);
        }
    }

    /**
     * 发送方式三：异步发送，异步等待并处理发送结果
     */
    public void asyncSend() {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic", "test", "test-value");
        CompletableFuture<SendResult<String, String>> completable = kafkaTemplate.send(record);
        // 异步回调
        completable.whenCompleteAsync((result, ex) -> {
            if (Objects.nonNull(ex)) {
                log.error("...", ex);
            } else {
                // 发生成功
            }
        });
    }
}
