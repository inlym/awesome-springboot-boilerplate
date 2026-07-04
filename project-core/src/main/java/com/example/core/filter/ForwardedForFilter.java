package com.example.core.filter;

import com.example.core.constants.ContextKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * X-Forwarded-For 代理转发过滤器
 *
 * <h2>说明
 * <p>从 HTTP 标准代理转发请求头 {@code X-Forwarded-For} 中提取客户端真实 IP 地址。
 * <p>适用于通过反向代理（Nginx、HAProxy 等）访问服务的场景。
 *
 * <h2>功能特性
 * <ul>
 *   <li>从 X-Forwarded-For 请求头取倒数第二段作为客户端 IP 地址</li>
 *   <li>仅在客户端 IP 不为空且请求属性中为空时，赋值并镜像到 MDC</li>
 * </ul>
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Slf4j
@Component
public class ForwardedForFilter extends OncePerRequestFilter implements Ordered {

    /** 包含代理链 IP 地址的请求头名称 */
    private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";

    /**
     * 获取过滤器执行顺序
     *
     * <h3>执行顺序说明
     * <p>在 EnvoyExternalAddressFilter 之后、DirectAccessIpFilter 之前执行。
     *
     * @return 过滤器执行顺序优先级值
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    /**
     * 判断是否需要跳过过滤处理
     *
     * <h3>跳过条件
     * <p>当 X-Forwarded-For 请求头不存在或为空时跳过过滤处理。
     *
     * @param request HTTP 请求对象
     * @return true 表示跳过过滤处理，false 表示执行过滤处理
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String forwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);
        return !StringUtils.hasText(forwardedFor);
    }

    /**
     * 执行过滤器内部逻辑
     *
     * <h3>处理流程
     * <p>1. 从 X-Forwarded-For 请求头按逗号分割取倒数第二段作为客户端 IP 地址
     * <p>2. 仅在请求属性中为空时，赋值并镜像到 MDC
     * <p>3. 继续执行过滤器链，结束后清理 MDC
     *
     * <h3>提取规则
     * <p>X-Forwarded-For 格式为 {@code 客户端IP, 代理1, 代理2, ...}，倒数第二段即为原始客户端 IP。
     * <p>例如 {@code 125.114.63.66, 10.1.10.94} 中取 {@code 125.114.63.66}。
     *
     * @param request     HTTP 请求对象
     * @param response    HTTP 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 处理请求时发生 Servlet 异常
     * @throws IOException      处理请求时发生 IO 异常
     */
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String forwardedFor = request.getHeader(X_FORWARDED_FOR_HEADER);

        // 按逗号分割，取倒数第二段作为客户端 IP
        String[] parts = forwardedFor.split(",");
        String clientIp = parts[parts.length - 2].trim();

        // 仅在请求属性中为空时才赋值，同时镜像到 MDC 供日志输出
        if (request.getAttribute(ContextKeys.CLIENT_IP) == null) {
            request.setAttribute(ContextKeys.CLIENT_IP, clientIp);
            MDC.put(ContextKeys.CLIENT_IP, clientIp);
            log.trace("从 X-Forwarded-For 请求头获取客户端 IP 地址：{}", clientIp);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 清理 MDC，避免 Servlet 容器线程复用导致客户端 IP 串到其他请求
            MDC.remove(ContextKeys.CLIENT_IP);
        }
    }
}
