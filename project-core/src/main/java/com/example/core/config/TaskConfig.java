package com.example.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 异步与定时任务配置
 *
 * <h2>配置说明
 * <p>异步任务（@Async）由 Spring Boot 自动配置的虚拟线程 SimpleAsyncTaskExecutor 执行（spring.threads.virtual.enabled=true）。
 * <p>定时任务（@Scheduled）由 Spring Boot 自动配置的虚拟线程 SimpleAsyncTaskScheduler 调度。
 * <p>上下文传播（MDC、Observation、Span）由 spring.task.execution.propagate-context=true 启用。
 * <p>自动注册的 ContextPropagatingTaskDecorator 作为共享 TaskDecorator bean，同时应用到执行器和调度器。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class TaskConfig implements AsyncConfigurer {

    // ================================ public 方法 ================================

    /**
     * 获取异步异常处理器
     *
     * <h3>异常处理
     * <p>配置异步方法执行时的异常处理策略，记录异常日志。
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, _) -> log.error(
            "异步方法执行失败，方法名={}, 错误原因={}",
            method.getName(),
            throwable.getMessage(),
            throwable
        );
    }
}
