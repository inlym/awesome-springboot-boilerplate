package com.example.core.config;

import com.example.core.model.response.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.json.JsonMapper;

/**
 * Spring Security 配置类
 *
 * <h2>主要功能
 * <p>配置 Spring Security 安全策略，包括认证和授权设置。
 * <p>已启用方法级鉴权（{@code @Secured} 注解），用于控制器方法的权限控制。
 *
 * <h2>安全策略说明
 * <p>HTTP 层采用宽松配置，所有请求路径允许匿名访问。
 * <p>方法级鉴权通过 {@code @Secured} 注解实现，需要配合 SecurityContext 中的认证信息。
 * <p>认证和授权失败时返回标准 JSON 错误响应，与全局异常处理器格式保持一致。
 *
 * <h2>SecurityContext 管理说明
 * <p>使用 {@code requireExplicitSave(false)} 配置，允许 Filter 直接设置的 SecurityContext
 * <p>在请求生命周期内保持有效，无需显式保存到 repository。
 *
 * <h2>CORS 配置说明
 * <p>通过 Spring Security 的 CorsFilter 统一管理跨域请求，替代 WebMvc 层的 CORS 配置。
 * <p>安全过滤器链中的 CorsFilter 在认证过滤器之前执行，确保预检请求不被拦截。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    /** JSON 序列化工具 */
    private final JsonMapper jsonMapper;

    // ================================ public 方法 ================================

    /**
     * 配置安全过滤器链
     *
     * <h3>安全注意事项
     * <p>当前配置为完全开放策略，生产环境部署前应评估安全风险并根据实际需求配置相应的认证和授权机制。
     *
     * <h3>配置说明
     * <p>禁用表单登录、HTTP Basic、CSRF、匿名认证和会话管理，适配无状态 API 架构。
     * <p>使用 {@code NullRequestCache} 替代默认的 {@code HttpSessionRequestCache}，避免无意义的会话依赖。
     * <p>显式配置安全响应头，关闭对 API 无意义的 frameOptions 和 HSTS。
     * <p>CORS 通过 Spring Security CorsFilter 统一管理，配置源由 {@code corsConfigurationSource} Bean 提供。
     * <p>认证和授权失败时返回标准 JSON 错误响应，错误码与 {@code GlobalExceptionHandler} 保持一致。
     *
     * @param http HttpSecurity 配置对象
     * @return 配置好的安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            // 关闭 form 表单认证，禁用基于表单的用户名密码登录方式
            .formLogin(AbstractHttpConfigurer::disable)
            // 关闭 HTTP Basic 认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 关闭 CSRF 防护，适用于无状态的 API 接口
            .csrf(AbstractHttpConfigurer::disable)
            // 关闭匿名认证，无状态 API 不需要匿名用户概念
            .anonymous(AbstractHttpConfigurer::disable)
            // 使用 NullRequestCache，避免无状态 API 产生不必要的会话依赖
            .requestCache(cache -> cache.requestCache(new NullRequestCache()))
            // 配置无状态会话管理，不创建 HTTP 会话
            .sessionManagement(registry -> registry.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置 SecurityContext，允许 Filter 直接设置的上下文保持有效
            .securityContext(securityContext -> securityContext.requireExplicitSave(false))
            // 显式配置安全响应头，关闭对 API 无意义的 frameOptions 和 HSTS
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
            )
            // 配置 CORS，使用统一的 CorsConfigurationSource Bean
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置异常处理，认证和授权失败时返回标准 JSON 错误响应
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jsonAuthenticationEntryPoint())
                .accessDeniedHandler(jsonAccessDeniedHandler())
            )
            // 配置所有请求路径允许匿名访问
            .authorizeHttpRequests(registry -> registry.anyRequest().permitAll());

        return http.build();
    }

    /**
     * 创建 JSON 格式的认证入口点
     *
     * <h3>触发场景
     * <p>当未认证用户访问需要认证的资源时由 {@code ExceptionTranslationFilter} 调用。
     *
     * <h3>响应格式
     * <p>返回 HTTP 401 状态码和标准 JSON 错误响应。
     *
     * @return 认证入口点
     */
    @Bean
    public AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return (_, response, _) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            jsonMapper.writeValue(
                response.getWriter(),
                new ErrorResponse(2, "response.auth.unauthenticated")
            );
        };
    }

    /**
     * 创建 JSON 格式的访问拒绝处理器
     *
     * <h3>说明
     * <p>当已认证用户访问无权限的资源时由 {@code ExceptionTranslationFilter} 调用。
     * <p>当前架构中方法级鉴权由 {@code @Secured} 注解实现，权限不足时抛出 {@code AccessDeniedException}
     * <p>并由 {@code GlobalExceptionHandler} 统一处理。
     * <p>此处理器作为防御性配置，用于处理过滤器链层可能出现的访问拒绝场景。
     *
     * <h3>响应格式
     * <p>返回 HTTP 403 状态码和标准 JSON 错误响应，错误码与 {@code GlobalExceptionHandler#handleAccessDenied} 保持一致。
     *
     * @return 访问拒绝处理器
     */
    @Bean
    public AccessDeniedHandler jsonAccessDeniedHandler() {
        return (_, response, _) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            jsonMapper.writeValue(
                response.getWriter(),
                new ErrorResponse(5, "response.auth.permission_denied")
            );
        };
    }

    /**
     * 创建 CORS 配置源
     *
     * <h3>配置说明
     * <p>允许所有来源进行跨域访问，支持常用 HTTP 方法。
     * <p>预检请求缓存时间为 10 天（864000 秒）。
     * <p>由 Spring Security CorsFilter 在认证过滤器之前执行。
     *
     * @return CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge(864000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
