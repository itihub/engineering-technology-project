package com.xyz.engineering.queue.disruptor;

import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Disruptor 测试类
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DisruptorTest {

    @Autowired
    private EventHandlerEventMessageDisruptor disruptor01;
    @Autowired
    private WorkHandlerEventMessageDisruptor disruptor02;

    /**
     * 测试生产消息
     */
    public void testDisruptorPushData() {
        disruptor01.onData("01");
        disruptor02.onData("01");
    }

}
