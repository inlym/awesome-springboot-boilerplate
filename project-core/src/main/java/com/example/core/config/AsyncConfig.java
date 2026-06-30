package com.example.core.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置
 *
 * <h2>配置说明
 * <p>配置异步任务执行器，使用线程池管理异步任务。
 * <p>通过 ContextPropagatingTaskDecorator 自动传播 ContextRegistry 中注册的 MDC、Observation、Span 等 ThreadLocal。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@Slf4j
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    // ================================ 依赖注入 ================================

    /** 异步任务配置属性 */
    private final AsyncProperties asyncProperties;

    // ================================ public 方法 ================================

    /**
     * 创建异步任务执行器
     *
     * <h3>配置说明
     * <p>创建线程池任务执行器，配置核心参数和 ContextPropagatingTaskDecorator。
     * <p>TaskDecorator 由 Spring Framework 提供，内部基于 ContextSnapshotFactory 捕获与恢复所有已注册的 accessor。
     */
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 从配置属性中获取线程池参数
        executor.setCorePoolSize(asyncProperties.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperties.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(asyncProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(asyncProperties.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 设置上下文传播装饰器，自动传递 MDC、Observation、Span 等已注册的 ThreadLocal
        executor.setTaskDecorator(new ContextPropagatingTaskDecorator());

        executor.initialize();
        return executor;
    }

    /**
     * 获取异步异常处理器
     *
     * <h3>异常处理
     * <p>配置异步方法执行时的异常处理策略，记录异常日志。
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> log.error(
            "异步方法执行失败，方法名={}, 错误原因={}",
            method.getName(),
            throwable.getMessage(),
            throwable
        );
    }
}
