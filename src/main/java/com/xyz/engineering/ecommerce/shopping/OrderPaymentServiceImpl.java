package com.xyz.engineering.ecommerce.shopping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 订单支付业务接口实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderPaymentServiceImpl {

    private final ObjectMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final static String ORDER_PAYMENT_TOPIC = "order_payment";

    /**
     * 模拟支付完成，发送订单信息，下游系统订阅
     * @param message
     */
    @Transactional(rollbackFor = Exception.class)
    public void payment(OrderMessage message) throws Exception {
        // TODO ...

        // 方式一：发送消息不管结果
        kafkaTemplate.send(ORDER_PAYMENT_TOPIC, mapper.writeValueAsString(message));
        // 方式二：异步发送消息，带有消息回调
        CompletableFuture<SendResult<String, String>> completable
                = kafkaTemplate.send(ORDER_PAYMENT_TOPIC, mapper.writeValueAsString(message));
        // 获取响应结果方式一：阻塞方法
        completable.get();
        // 获取响应结果方式二：非阻塞方法
        completable.whenCompleteAsync((result, ex) -> {
            // 发送失败
            if (Objects.nonNull(ex)) {
                log.error("send message error: [ex={}]", message, ex);
            } else {
                RecordMetadata metadata = result.getRecordMetadata();
                String topic = metadata.topic();    // 获取topic
                int partition = metadata.partition();   // 获取分区
                long offset = metadata.offset();    // 获取偏移量
                // 发送成功
                log.info("send message success: {}, {}, {}", topic, partition, offset);
            }
        });

    }
}
