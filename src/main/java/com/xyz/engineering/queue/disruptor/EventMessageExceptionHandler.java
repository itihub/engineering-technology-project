package com.xyz.engineering.queue.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理器
 */
@Slf4j
public class EventMessageExceptionHandler implements ExceptionHandler<EventMessage> {

    /**
     * 处理处理消息时发生的异常
     * @param throwable  处理消息发生的异常
     * @param sequence 消息的顺序
     * @param eventMessage  事件消息
     */
    @Override
    public void handleEventException(Throwable throwable, long sequence, EventMessage eventMessage) {
        log.error("....");
    }

    /**
     * 处理消费线程开始之前发生的异常
     * @param throwable throw during the starting process.
     */
    @Override
    public void handleOnStartException(Throwable throwable) {
        log.error("....");
    }

    /**
     * 处理消费线程结束之前发生的异常
     * @param throwable throw during the shutdown process.
     */
    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.error("....");
    }
}
