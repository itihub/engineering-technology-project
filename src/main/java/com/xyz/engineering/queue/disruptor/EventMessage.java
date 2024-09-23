package com.xyz.engineering.queue.disruptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 事件消息，事件数据模型
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventMessage implements Serializable {

    private Object obj;
}
