package com.example.system.actuator.health;

import com.example.core.support.actuator.AbstractSamplingHealthIndicator;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * MySQL 健康检查指示器
 *
 * <h2>说明
 * <p>实现 HealthIndicator，将 MySQL 健康状态纳入标准 /actuator/health 聚合端点。
 *
 * <h2>探测方式
 * <p>通过 HikariDataSource 执行 SELECT 1 探测，探测策略由基类统一封装。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class MysqlHealthIndicator extends AbstractSamplingHealthIndicator {

    /** Hikari 数据源 */
    private final HikariDataSource hikariDataSource;

    // ================================ protected 重写方法 ================================

    /**
     * 执行单次 MySQL SELECT 1 探测
     *
     * <h3>处理逻辑
     * <p>通过 HikariDataSource 获取连接并执行 SELECT 1，测量包含连接获取、SQL 执行与结果读取的完整往返时间。
     *
     * @return 往返延迟毫秒数，检测失败返回 -1
     */
    @Override
    protected long probeOnce() {
        long startTime = System.nanoTime();

        // Connection、Statement、ResultSet 均需显式关闭，否则会造成连接池泄漏；不同 JDBC 驱动对 ResultSet 关闭时是否级联关闭 Statement 行为不一致，因此 Statement 独立声明；SQLException 在连接异常或 SQL 执行失败时抛出，HealthIndicator 不被全局异常处理器接管，必须在此捕获以视为本次采样失败
        try (Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            return (System.nanoTime() - startTime) / 1_000_000;
        } catch (Exception e) {
            return -1;
        }
    }
}
