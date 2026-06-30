package com.example.core.support.actuator;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于多次采样的健康检查基类
 *
 * <h2>说明
 * <p>实现 Spring Boot Actuator 的 HealthIndicator，将依赖方健康状态纳入标准 /actuator/health 聚合端点。
 *
 * <h2>探测策略
 * <p>连续执行 3 次轻量探测（由子类实现 probeOnce），全部成功视为 UP，任一失败视为 DOWN。
 * <p>平均延迟仅基于成功样本计算。
 * <p>每次采样的延迟结果通过 sampleLatenciesMs 字段完整输出，失败位置为 null。
 *
 * <h2>扩展说明
 * <p>子类实现 probeOnce 方法定义具体探测命令（如 SQL 查询、PING、API 调用）。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
public abstract class AbstractSamplingHealthIndicator implements HealthIndicator {

    /** 采样次数，全部成功才视为 UP */
    private static final int SAMPLES = 3;

    // ================================ public 方法 ================================

    /**
     * 查询依赖方健康状态
     *
     * <h3>状态判定
     * <p>3 次采样全部成功（probeOnce 返回值 >= 0）时状态为 UP，否则为 DOWN。
     *
     * @return 包含状态、平均延迟、采样计数、每次延迟列表的 Health 对象
     */
    @Override
    public final Health health() {
        List<Long> sampleLatenciesMs = new ArrayList<>(SAMPLES);
        long totalLatencyMs = 0;
        int successCount = 0;

        for (int i = 0; i < SAMPLES; i++) {
            // 执行单次探测，返回值 < 0 表示本次失败
            long latency = probeOnce();
            // 失败样本用 null 占位，保持列表长度与采样次数一致，便于按位置对应
            sampleLatenciesMs.add(latency >= 0 ? latency : null);
            if (latency >= 0) {
                totalLatencyMs += latency;
                successCount++;
            }
        }

        // 平均延迟仅基于成功样本计算，失败样本不污染结果；全部失败时为 null
        Long averageLatencyMs = successCount > 0 ? totalLatencyMs / successCount : null;

        // 状态判定从严：任一次失败即 DOWN，避免间歇性问题被平均值掩盖
        Health.Builder builder = successCount == SAMPLES ? Health.up() : Health.down();

        // 平均延迟仅在成功时才有意义，全部采样失败时省略该字段（Health.Builder.withDetail 拒绝 null value）
        if (averageLatencyMs != null) {
            builder.withDetail("averageLatencyMs", averageLatencyMs);
        }

        return builder
            .withDetail("sampleLatenciesMs", sampleLatenciesMs)
            .withDetail("successSamples", successCount)
            .withDetail("totalSamples", SAMPLES)
            .build();
    }

    // ================================ protected 方法 ================================

    /**
     * 执行单次轻量探测
     *
     * <h3>处理逻辑
     * <p>由子类实现具体的探测命令（如 SQL 查询、PING、API 调用），测量完整往返时间。
     *
     * @return 往返延迟毫秒数，检测失败返回 -1
     */
    protected abstract long probeOnce();
}
