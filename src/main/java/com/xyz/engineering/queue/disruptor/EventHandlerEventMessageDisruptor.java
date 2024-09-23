package com.xyz.engineering.queue.disruptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Disruptor 消费者实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventHandlerEventMessageDisruptor extends EventMessageDisruptor {

    @Override
    protected void handleEvent() {
        // 定义消费者
        disruptor.handleEventsWith(
                new EventMessageHandler("event-handler-01"),
                new EventMessageHandler("event-handler-02")
        );
    }
}
