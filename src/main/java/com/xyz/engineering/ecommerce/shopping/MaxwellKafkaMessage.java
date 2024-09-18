package com.xyz.engineering.ecommerce.shopping;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 订单完成之后给下游服务广播的另一种方案 监听binlog消息
 * maxwell 采集到的 binlog 消息
 * maxwell + kafka 监听数据库的binlog
 */
@Data
@Builder
public class MaxwellKafkaMessage {

    private String database;    // 操作的数据库名称
    private String table;       // 操作的数据表
    private String type;        // 操作类型：insert, update, delete 等等
    private long ts;            // 操作时间，13位时间戳
    private long xid;           // 事务唯一id
    private boolean commint;    // 数据增加、更新、删除是否已经提交
    private String position;    // binlog 文件及偏移量，例如：master.00006:800911
    private int server_id;      // 数据库服务器 id
    private int thread_id;      // 代表操作数据库的客户端连接
    private List<String> primary_key;   // 数据记录的主键
    private List<String> primary_key_columns;   // 数据记录主键的列名
    private JsonNode data;            // 数据增加、更新、删除之后的内容(insert,update)
    private JsonNode old;             // 数据更新前的内容或者表结构修改之前的结构定义(update)
    private JsonNode def;             // 表创建与表修改的结构定义
    private String sql;                 // dll操作的sql语句
}
