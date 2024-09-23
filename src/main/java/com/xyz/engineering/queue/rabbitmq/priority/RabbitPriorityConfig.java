package com.xyz.engineering.queue.rabbitmq.priority;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 优先级队列配置
 */
@Configuration
public class RabbitPriorityConfig {

    /**
     * business 交换机
     * 消息中的路由键（RoutingKey）需要和Binding中的bindingKey完全匹配
     * 交换机就会将消息发送到对应的队列中，是基于完全匹配
     * @return
     */
    @Bean
    public DirectExchange businessExchange() {
        return ExchangeBuilder.directExchange("business.exchange")
                .durable(true)
                .build();
    }

    /**
     * 优先级队列
     * @return
     */
    @Bean
    public Queue businessPriorityQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-max-priority", 10);
        // 可以通过args参数配置，也可以通过属性直接配置maxPriority
//        return QueueBuilder.durable("business.queue").maxPriority(10).build();
        return QueueBuilder.durable("business.queue").withArguments(args).build();

    }

    /**
     * 交换机和优先级队列绑定
     * @param priorityQueue
     * @param directExchange
     * @return
     */
    @Bean
    public Binding businessBinding(Queue priorityQueue, DirectExchange directExchange) {
        return BindingBuilder
                .bind(priorityQueue)
                .to(directExchange)
                .with("order.priority.qinyi");
    }
}
