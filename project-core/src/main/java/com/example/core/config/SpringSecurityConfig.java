package com.example.core.config;

import com.example.core.extension.SecurityFilterChainCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

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
 *
 * <h2>扩展机制说明
 * <p>通过 {@code SecurityFilterChainCustomizer} 接口支持业务模块扩展配置。
 * <p>核心模块自动收集所有实现类，在基础配置完成后依次调用。
 *
 * <h2>SecurityContext 管理说明
 * <p>使用 {@code requireExplicitSave(false)} 配置，允许 Filter 直接设置的 SecurityContext
 * <p>在请求生命周期内保持有效，无需显式保存到 repository。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@EnableMethodSecurity(securedEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    /** SecurityFilterChain 定制器列表 */
    private final List<SecurityFilterChainCustomizer> customizers;

    // ================================ public 方法 ================================

    /**
     * 配置安全过滤器链
     *
     * <h3>安全注意事项
     * <p>当前配置为完全开放策略，生产环境部署前应评估安全风险并根据实际需求配置相应的认证和授权机制。
     *
     * <h3>配置说明
     * <p>使用 {@code securityContext().requireExplicitSave(false)} 确保 Filter 设置的
     * <p>SecurityContext 不会被 SecurityContextHolderFilter 清除。
     * <p>在基础配置完成后，调用所有定制器进行扩展配置。
     *
     * @param http HttpSecurity 配置对象
     * @return 配置好的安全过滤器链
     * @throws Exception 配置过程中可能出现的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭 form 表单认证，禁用基于表单的用户名密码登录方式
            .formLogin(AbstractHttpConfigurer::disable)
            // 关闭 HTTP Basic 认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 关闭 CSRF 防护，适用于无状态的 API 接口
            .csrf(AbstractHttpConfigurer::disable)
            // 配置无状态会话管理，不创建 HTTP 会话
            .sessionManagement(registry -> registry.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置 SecurityContext，允许 Filter 直接设置的上下文保持有效
            .securityContext(securityContext -> securityContext.requireExplicitSave(false))
            // 配置所有请求路径允许匿名访问
            .authorizeHttpRequests(registry -> registry.anyRequest().permitAll());

        // 调用所有定制器进行扩展配置
        for (SecurityFilterChainCustomizer customizer : customizers) {
            customizer.customize(http);
        }

        return http.build();
    }
}