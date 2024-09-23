package com.xyz.engineering.queue.kafka.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消费者主动拉动拉取消息进行消费
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumerPollMessage implements InitializingBean {

    /** 手动消费消息 */
    private KafkaConsumer<String, String> consumer;
    /** 记录消费者消费消息的偏移量信息 */
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();

    /**
     * 通过注解实现消息的监听消费
     * @param record
     */
    @KafkaListener(topics = {"test-topic"})
    public void annotationConsumer(ConsumerRecord<String, String> record) {
        log.info("...");
    }

    /**
     * Bean初始化方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        getKafkaConsumer();

        // 配置订阅以及再平衡的监听器
        this.consumer.subscribe(Collections.singleton("test-topic"),
                new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        // 再平衡开始之前和消费者停止读取消息之后会被调用

                        // 获取分区对应的偏移量并提交
                        Map<String, List<TopicPartition>> topicPartitionMap = partitions.stream()
                                .collect(Collectors.groupingBy(TopicPartition::topic));
                        for (Map.Entry<String, List<TopicPartition>> topicPattionEntry
                                : topicPartitionMap.entrySet()) {
                            // ...
                        }
                        // 提交offset
                        synchronized (offsets) {
                            consumer.commitSync(offsets);
                        }
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                        // 再平衡之后和消费者开始读取消息之前会被调用

                        // 清空缓存分区对应offset 保持正确的消费起点
                        Map<String, List<TopicPartition>> topicPartitionMap = partitions.stream()
                                .collect(Collectors.groupingBy(TopicPartition::topic));
                        for (Map.Entry<String, List<TopicPartition>> topicPattionEntry
                                : topicPartitionMap.entrySet()) {
                            // ...
                        }
                        synchronized (offsets) {
                            offsets.clear();
                        }
                    }
                });
    }

    /**
     * 初始化kaka consumer
     */
    private void getKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9002");
        // 关闭自动提交 offset
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        this.consumer = new KafkaConsumer<String, String>(props);
    }

    /**
     * 记录偏移量
     * @param partition
     * @param lastOffset
     */
    private void recordOffset(TopicPartition partition, long lastOffset) {
        synchronized (offsets) {
            // 判断是否存在当前分区
            if (!offsets.containsKey(partition)) {
                // 不存在，则进行缓存并设置下次拉取的偏移量
                offsets.put(partition, new OffsetAndMetadata(lastOffset + 1));
            } else {
                // 存在，比较偏移量
                long curr = offsets.get(partition).offset();
                if (curr <= lastOffset + 1) {
                    offsets.put(partition, new OffsetAndMetadata(lastOffset + 1));
                }
            }
        }
    }

    /**
     * 提价偏移量
     */
    private void commitOffset() {
        Map<TopicPartition, OffsetAndMetadata> tmp;
        synchronized (offsets) {
            if (offsets.isEmpty()) {
                return;
            }
            tmp = new HashMap<>(this.offsets);
            // 清空当前的偏移量
            offsets.clear();
        }
        // 同步提交偏移量
        consumer.commitSync(tmp);
    }

    /**
     * 消费者退出
     */
    private void terminalConsumer() {
        if (Objects.nonNull(consumer)) {
            // 唤醒consumer
            consumer.wakeup();
            // 提交偏移量
            commitOffset();
            // 停止consumer
            consumer.close();
        }
    }

    /**
     * 手动消费消息实现
     * 1.拉取消息
     * 2.提交偏移量
     */
    public void _manualConsumer() {
        ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(1500));
        // 如果拉取消息不为空
        if (!records.isEmpty()) {
            log.info("...");
            // 处理消息，同时记录偏移量
            List<TopicPartition> partitions = new ArrayList<>(records.partitions());
            for (int i = 0; i < partitions.size(); i++) {
                TopicPartition partition = partitions.get(i);
                // 获取分区下的消息
                List<ConsumerRecord<String, String>> recordList = records.records(partition);
                // TODO 处理消息
                long lastOffset = recordList.get(recordList.size() - 1).offset();
                // 记录偏移量
                recordOffset(partition, lastOffset);
            }
            // 提交偏移量
            commitOffset();
        }
    }

    /**
     * 手动消费入口
     */
    private void manualConsumer() {
        while (true) {
            try {
                _manualConsumer();
            } catch (Exception ex) {
                break;
            }
        }

        terminalConsumer();
    }
}
