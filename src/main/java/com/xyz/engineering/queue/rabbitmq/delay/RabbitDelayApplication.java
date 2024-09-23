package com.xyz.engineering.queue.rabbitmq.delay;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyz.engineering.ecommerce.shopping.OrderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ延迟队列的应用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitDelayApplication {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    /**
     * 发送订单消息（生产者）
     * @param message
     */
    public void sendOrderMessage(OrderMessage message) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(
                "order_normal_exchange",    // topic
                "order.normal.qinyi",       // 路由key
                objectMapper.writeValueAsString(message),   // 消息
                buildTtlMessagePostProcessor("15000"));
        log.info("send order message success");
    }

    /**
     * 消息后处理对象，用来在发送消息之前设置属性
     * @param expiration 过期时间
     * @return
     */
    private MessagePostProcessor buildTtlMessagePostProcessor(String expiration) {
        return message -> {
            // 为消息添加过期时间
            message.getMessageProperties().setExpiration(expiration);
            return message;
        };
    }

    /**
     * 监听死信队列（消费者）
     * @param message
     */
    @RabbitListener(queues = "order_deadletter_queue")
    public void consumeDeadLetterQueue(Object message) {
        // TODO 业务逻辑校验这条死信消息是否需要处理，即校验用户是否已经完成了支付
    }
}
