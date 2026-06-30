package com.example.system.actuator.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * 系统健康检查指标
 *
 * <h2>说明
 * <p>封装系统健康检查相关的自定义 Micrometer 指标，供控制器上报业务调用数据。
 *
 * <h2>指标清单
 * <p>{@code project.system.health.ping.requests}：{@code /ping} 端点累计调用次数。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Component
public class SystemHealthMetrics {

    /** ping 端点调用次数的指标名称 */
    private static final String PING_REQUESTS_METRIC_NAME = "project.system.health.ping.requests";

    /** ping 端点调用次数计数器 */
    private final Counter pingRequestsCounter;

    // 构造函数显式注册 Counter 而非使用 @RequiredArgsConstructor，原因：需要在 Bean 初始化时立即将 Counter 绑定到 MeterRegistry，避免后续调用 increment 时 Counter 尚未注册而 NPE
    public SystemHealthMetrics(MeterRegistry meterRegistry) {
        // 通过 Builder 注册 Counter，附带 description 提升指标在监控系统中的可读性
        this.pingRequestsCounter = Counter
            .builder(PING_REQUESTS_METRIC_NAME)
            .description("ping 端点累计调用次数")
            .register(meterRegistry);
    }

    // ================================ public 方法 ================================

    /**
     * ping 端点调用计数自增
     */
    public void incrementPingRequests() {
        pingRequestsCounter.increment();
    }
}
