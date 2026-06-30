package com.example.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 定时任务配置
 *
 * <h2>配置说明
 * <p>配置 Spring Boot 定时任务执行器，使用专用线程池管理定时任务。
 * <p>通过 ContextPropagatingTaskDecorator 自动传播 ContextRegistry 中注册的 MDC、Observation、Span 等 ThreadLocal。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ScheduleConfig implements SchedulingConfigurer {

    // ================================ 依赖注入 ================================

    /** 定时任务配置属性 */
    private final ScheduleProperties scheduleProperties;

    // ================================ public 方法 ================================

    /**
     * 配置定时任务执行器
     *
     * <h3>配置说明
     * <p>创建专用的定时任务线程池，设置线程池大小和线程名前缀。
     * <p>使用 ThreadPoolTaskScheduler 提供定时任务调度功能，确保任务按时执行。
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Integer poolSize = scheduleProperties.getPoolSize();
        String threadNamePrefix = scheduleProperties.getThreadNamePrefix();
        Integer awaitTerminationSeconds = scheduleProperties.getAwaitTerminationSeconds();

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix(threadNamePrefix);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);

        // 设置上下文传播装饰器，自动传递 MDC、Observation、Span 等已注册的 ThreadLocal
        taskScheduler.setTaskDecorator(new ContextPropagatingTaskDecorator());

        taskScheduler.initialize();

        taskRegistrar.setTaskScheduler(taskScheduler);

        log.info(
            "定时任务线程池初始化完成，线程池大小: {}, 线程名前缀: {}, 等待关闭时间: {}秒",
            poolSize,
            threadNamePrefix,
            awaitTerminationSeconds
        );
    }
}
