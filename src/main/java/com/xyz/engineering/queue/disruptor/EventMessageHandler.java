package com.xyz.engineering.queue.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 事件处理器（消费者）
 */
@Slf4j
public class EventMessageHandler implements EventHandler<EventMessage>, WorkHandler<EventMessage> {

    /* 消费者名称 */
    private final String handlerName;

    public EventMessageHandler(String handlerName) {
        this.handlerName = handlerName;
    }

    /**
     * EventHandler 独立消费者，每一个消费者都消费所有的消息
     * @param event      published to the {@link RingBuffer}
     * @param sequence   of the event being processed
     * @param endOfBatch flag to indicate if this is the last event in a batch from the {@link RingBuffer}
     * @throws Exception
     */
    @Override
    public void onEvent(EventMessage event, long sequence, boolean endOfBatch) throws Exception {
        log.info("....");
    }

    /**
     * WorkHandler 共同消费者，不会重复消费消息信息
     * @param event published to the {@link RingBuffer}
     * @throws Exception
     */
    @Override
    public void onEvent(EventMessage event) throws Exception {
        log.info("....");
    }
}
