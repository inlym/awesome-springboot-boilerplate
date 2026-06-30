package com.example.core.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 定时任务配置属性类
 *
 * <h2>配置说明
 * <p>封装定时任务线程池的所有配置项，包括线程池大小、线程名前缀等参数。
 * <p>使用 @ConfigurationProperties 注解自动绑定配置文件中的值，并通过 @Validated 验证参数。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "schedule.config")
public class ScheduleProperties {

    // ================================ 线程池配置 ================================

    /** 线程池大小 */
    @Min(value = 1, message = "线程池大小不能小于 1")
    @Max(value = 50, message = "线程池大小不能超过 50")
    private Integer poolSize;

    /** 线程名称前缀 */
    @NotBlank(message = "线程名称前缀不能为空")
    private String threadNamePrefix;

    /** 关闭时等待任务完成的时间（秒） */
    @Min(value = 1, message = "等待时间不能小于 1 秒")
    @Max(value = 300, message = "等待时间不能超过 5 分钟")
    private Integer awaitTerminationSeconds;
}
