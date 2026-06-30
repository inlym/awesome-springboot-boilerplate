package com.example.core.config;

import io.micrometer.context.ContextRegistry;
import io.micrometer.context.integration.Slf4jThreadLocalAccessor;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.springframework.context.annotation.Configuration;

/**
 * 上下文传播配置
 *
 * <h2>配置说明
 * <p>基于 Micrometer Context Propagation 统一管理跨线程上下文传播，覆盖 MDC、Observation。
 * <p>所有由 {@code ContextExecutorService.wrap} 包装的执行器、Spring 的 ContextPropagatingTaskDecorator、Reactor 流
 * <p>均会自动捕获与恢复注册到 ContextRegistry 的 accessor。
 *
 * <h2>注册项
 * <ul>
 *   <li>MDC：通过 Slf4jThreadLocalAccessor 传播 SLF4J MDC 全量键值</li>
 *   <li>Observation：通过 ObservationThreadLocalAccessor 跨线程恢复当前 Observation</li>
 * </ul>
 *
 * <h2>Reactor 自动传播
 * <p>Hooks.enableAutomaticContextPropagation 由 Spring Boot 的 ReactorAutoConfiguration 在
 * <p>`spring.reactor.context-propagation=AUTO` 配置下启用，时机早于任何 Schedulers 初始化。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
public class ContextPropagationConfig {

    /**
     * 初始化上下文传播
     *
     * <h3>处理逻辑
     * <p>依次注册 MDC、Observation accessor 到全局 ContextRegistry
     *
     * @param observationRegistry Observation 注册表，承载 Observation 跨线程恢复
     */
    public ContextPropagationConfig(ObservationRegistry observationRegistry) {
        ContextRegistry registry = ContextRegistry.getInstance();

        // MDC accessor，无参构造内部创建 GlobalMdcThreadLocalAccessor，传播 SLF4J MDC 全量键值
        registry.registerThreadLocalAccessor(new Slf4jThreadLocalAccessor());

        // Observation accessor，承载 Observation 跨线程恢复
        registry.registerThreadLocalAccessor(new ObservationThreadLocalAccessor(observationRegistry));
    }
}
