package com.example.core.config;

import com.example.core.annotation.resolver.ClientIpMethodArgumentResolver;
import com.example.core.annotation.resolver.UserIdMethodArgumentResolver;
import com.example.core.extension.InterceptorCustomizer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Spring MVC 配置类
 *
 * <h2>主要功能
 * <p>提供 Web 层相关配置，包括自定义参数解析器和拦截器注册。
 * <p>跨域资源共享（CORS）已迁移至 Spring Security 层统一管理，见 {@code SpringSecurityConfig#corsConfigurationSource}。
 *
 * <h2>扩展机制说明
 * <p>通过 {@code InterceptorCustomizer} 接口支持业务模块注册自定义拦截器。
 * <p>核心模块自动收集所有实现类，在初始化时依次调用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /** 拦截器定制器列表 */
    private final List<InterceptorCustomizer> interceptorCustomizers;

    /**
     * 配置自定义参数解析器
     *
     * <h3>配置说明
     * <p>1. 添加用户 ID 参数解析器，用于注入 @UserId 注解的参数
     * <p>2. 添加客户端 IP 参数解析器，用于注入 @ClientIp 注解的参数
     *
     * @param argumentResolvers 参数解析器列表
     */
    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new UserIdMethodArgumentResolver());
        argumentResolvers.add(new ClientIpMethodArgumentResolver());
    }

    /**
     * 注册拦截器
     *
     * <h3>配置说明
     * <p>遍历所有 InterceptorCustomizer 实现类，由业务模块自行注册拦截器。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        interceptorCustomizers.forEach(customizer -> customizer.addInterceptors(registry));
    }
}
