package com.example.system.actuator.health;

import com.example.core.support.actuator.AbstractSamplingHealthIndicator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 健康检查指示器
 *
 * <h2>说明
 * <p>实现 HealthIndicator，将 Redis 健康状态纳入标准 /actuator/health 聚合端点。
 *
 * <h2>探测方式
 * <p>通过 RedisConnection 执行 PING 命令测量往返时间，这是 Redis 官方推荐的延迟检测方式。探测策略由基类统一封装。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator extends AbstractSamplingHealthIndicator {

    /** Redis 模板 */
    private final RedisTemplate<String, Object> redisTemplate;

    // ================================ protected 重写方法 ================================

    /**
     * 执行单次 Redis PING 探测
     *
     * <h3>处理逻辑
     * <p>通过 RedisConnection 执行 PING 命令测量往返时间。
     *
     * @return 往返延迟毫秒数，检测失败返回 -1
     */
    @Override
    protected long probeOnce() {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory == null) {
            log.trace("Redis 连接工厂为空，跳过本次 PING 探测");
            return -1;
        }

        long startTime = System.nanoTime();

        // RedisConnection.ping() 在连接异常时抛出异常，捕获后视为本次采样失败
        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.ping();
            return (System.nanoTime() - startTime) / 1_000_000;
        } catch (Exception e) {
            return -1;
        }
    }
}
