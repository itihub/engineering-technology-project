package com.xyz.engineering.queue.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * 事件模型工厂类，用于生产事件消息
 */
public class EventMessageFactory implements EventFactory<EventMessage> {

    @Override
    public EventMessage newInstance() {
        return new EventMessage();
    }

}
