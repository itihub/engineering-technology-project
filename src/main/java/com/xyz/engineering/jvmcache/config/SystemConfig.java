package com.xyz.engineering.jvmcache.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 系统配置
 */
@Data
@Builder
@AllArgsConstructor
public class SystemConfig {

    private Integer configId;
    private String configName;
    private String limit;
}
