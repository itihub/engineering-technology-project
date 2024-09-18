package com.xyz.engineering.ecommerce.shopping;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对于订单超时未支付的场景，使用延迟队列的RabbitMQ配置
 */
@Configuration
public class OrderRabbitDelayConfig {

    /**
     * 正常的Order交换机
     * 1.RoutingKey 和 BindingKey 为一个点号'.'分隔的字符串
     * 2.BindingKey 可使用 * 和 # 用于做模糊匹配：*匹配一个单词，#匹配0个或多个单词
     * @return
     */
    @Bean("orderNormalExchange")
    public Exchange orderNormalExchange() {
        // order_normal_exchange 交换机的名字
        // durable, 持久化, RabbitMQ 重启之后交换仍然会存在
        return ExchangeBuilder
                .topicExchange("order_normal_exchange")
                .durable(true)
                .build();
    }

    /**
     * 正常order交换机绑定的队列
     * @return
     */
    @Bean("orderNormalQueue")
    public Queue orderNormalQueue() {
        return QueueBuilder
                // 构造一个持久化的 queue，RabbitMQ重启之后队列仍然会存在；且队列名字是 order_normal_queue
                .durable("order_normal_queue")
                // 设置过期时间（单位是毫秒， 设置为10s），通过队列属性设置，队列中所有消息都有相同的过期时间
                // 如果两种方法一起使用，则消息TTL以两者之间较小的那个数值为准；消息在队列中的生存时间一旦超过就会进入死信队列
                .ttl(10000)
                // 消息队列最大容量，即10000条消息；queue默认是没有最大容量限制的，我们配置之后如果超过了，会进入死信队列
                .maxLength(1000L)
                // 消息最大长度，1024个字节
                .maxLengthBytes(1024)
                // 正常队列绑定死信交换机
                .deadLetterExchange("order_deadletter_exchange")
                // 正常队列的死信路由key
                .deadLetterRoutingKey("order.deadletter")
                .build();
    }

    /**
     * 绑定 order交换机 和 order 队列
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingNormalOrderExchangeAndQueue(@Qualifier("orderNormalQueue") Queue queue,
                                                      @Qualifier("orderNormalExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue).to(exchange)
                // 绑定路由键
                .with("order.normal.*")
                .noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("orderDeadLetterExchange")
    public Exchange orderDeadLetterExchange() {
        return ExchangeBuilder
                .topicExchange("order_deadletter_exchange")
                .durable(true)
                .build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("orderDeadLetterQueue")
    public Queue orderDeadLetterQueue() {
        return QueueBuilder.durable("order_deadletter_queue").build();
    }

    /**
     * 绑定 死信交换机 和 死信队列
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingDeadLetterOrderExchangeAndQueue(@Qualifier("orderDeadLetterQueue") Queue queue,
                                                      @Qualifier("orderDeadLetterExchange") Exchange exchange) {
        return BindingBuilder
                .bind(queue).to(exchange)
                // 绑定路由键
                .with("order.deadletter.*")
                .noargs();
    }
}
