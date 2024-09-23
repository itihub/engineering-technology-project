package com.xyz.engineering.queue.rabbitmq.priority;

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
 * 优先级队列的应用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitPriorityApplication {

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    /**
     * 发送优先级消息（生产者）
     * @param message
     */
    public void sendBusinessMessage(OrderMessage message) throws JsonProcessingException {
        rabbitTemplate.convertAndSend(
                "business.exchange",    // topic
                "order.priority.qinyi",       // 路由key
                objectMapper.writeValueAsString(message),   // 消息
                buildTtlMessagePostProcessor(3));
        log.info("send order message success");
    }

    /**
     * 监听优先级队列（消费者）
     * @param message
     */
    @RabbitListener(queues = {"business.queue"})
    public void consumerPriorityQueue(Object message) {
        log.info("....");
    }

    /**
     * 消息后处理对象，用来在发送消息之前设置属性
     * @param priority 优先级
     * @return
     */
    private MessagePostProcessor buildTtlMessagePostProcessor(Integer priority) {
        return message -> {
            // 为消息添加优先级
            message.getMessageProperties().setPriority(priority);
            return message;
        };
    }
}
