package com.xyz.engineering.jvmcache.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用Java内置的数据结构或自定义的数据结构构造 JVM本地缓存
 */
@Slf4j
@Component
public class InnerJavaCache implements InitializingBean {

    private static Map<Integer, SystemConfig> configId2ConfigObj = new ConcurrentHashMap<>();

    /**
     * 初始化Bean方法
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化和刷新 SystemConfig
        initAndRefreshSystemConfig();
    }

    private void initAndRefreshSystemConfig() {

        // 定义定时任务，异步刷新缓存
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(
                1, new BasicThreadFactory.Builder()
                .namingPattern("init-refresh-system-config")
                .daemon(true).build()
        );

        executorService.scheduleAtFixedRate(() -> {
            // 1.获取system confing
            // 2.put 到 configId2ConfigObj 中
        }, 0, 3, TimeUnit.MINUTES);
    }

    public SystemConfig getConfigById(Integer configId) {
        return configId2ConfigObj.get(configId);
    }
}
