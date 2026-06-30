package com.example.core.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 异步任务配置属性类
 *
 * <h2>配置说明
 * <p>封装异步任务执行器的所有配置项，包括线程池大小、队列容量等参数。
 * <p>使用 @ConfigurationProperties 注解自动绑定配置文件中的值，并通过 @Validated 验证参数。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "async.config")
public class AsyncProperties {

    // ================================ 线程池配置 ================================

    /** 核心线程数 */
    @Min(value = 1, message = "核心线程数不能小于 1")
    @Max(value = 100, message = "核心线程数不能超过 100")
    private Integer corePoolSize;

    /** 最大线程数 */
    @Min(value = 1, message = "最大线程数不能小于 1")
    @Max(value = 500, message = "最大线程数不能超过 500")
    private Integer maxPoolSize;

    /** 队列容量 */
    @Min(value = 1, message = "队列容量不能小于 1")
    @Max(value = 10000, message = "队列容量不能超过 10000")
    private Integer queueCapacity;

    /** 线程存活时间（秒） */
    @Min(value = 1, message = "线程存活时间不能小于 1 秒")
    @Max(value = 3600, message = "线程存活时间不能超过 1 小时")
    private Integer keepAliveSeconds;

    /** 线程名称前缀 */
    @NotBlank(message = "线程名称前缀不能为空")
    private String threadNamePrefix;
}
