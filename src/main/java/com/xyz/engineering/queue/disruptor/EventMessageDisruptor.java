package com.xyz.engineering.queue.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.TimeUnit;

/**
 * 抽象Bean，disruptor工具
 * 用来打开生产消费
 */
@Slf4j
public abstract class EventMessageDisruptor implements InitializingBean, DisposableBean {

    /** 事件转换器，用于设置消息的内容 */
    private static final EventTranslatorOneArg<EventMessage, Object> TRANSLATOR =
            (message, sequence, obj) -> message.setObj(obj);

    protected Disruptor<EventMessage> disruptor;

    /** 消息队列 */
    protected RingBuffer<EventMessage> ringBuffer;

    /** 消息工厂 */
    private static final EventMessageFactory factory = new EventMessageFactory();

    /**
     * 生产者，发布消息
     * @param obj
     */
    public void onData(Object obj){
        ringBuffer.publishEvent(TRANSLATOR, obj);
    }

    /**
     * 消费者，消费消息
     */
    protected abstract void handleEvent();

    /**
     * Bean初始化方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 队列大小
        final int bufferSize = 1024 * 1024;
        // 实例化 disruptor
        disruptor = new Disruptor<EventMessage>(
                factory, // 消息工厂
                bufferSize, // 队列大小
                DaemonThreadFactory.INSTANCE, // 消费者线程工厂，为每一个消费者创建一个线程
                ProducerType.SINGLE, // 生产者类型
                new BlockingWaitStrategy() // 等待策略
        );
        // 设置事件处理器
        handleEvent();
        // 异常处理器
        disruptor.setDefaultExceptionHandler(new EventMessageExceptionHandler());
        // 启动 disruptor 实现生产和消费
        disruptor.start();
        // 初始化 ringBuffer
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * Bean销毁之前需要执行的销毁方法
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        try {
            // 延时关闭disruptor
            disruptor.shutdown(1, TimeUnit.MINUTES);
        } catch (TimeoutException ex) {
            log.error("...", ex);
        }
    }
}
