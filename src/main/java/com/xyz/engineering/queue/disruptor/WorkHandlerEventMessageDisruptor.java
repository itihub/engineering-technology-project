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
public class WorkHandlerEventMessageDisruptor extends EventMessageDisruptor {

    @Override
    protected void handleEvent() {
        // 定义消费者
        disruptor.handleEventsWithWorkerPool(
                new EventMessageHandler("work-handler-01"),
                new EventMessageHandler("work-handler-02")
        );
    }
}
