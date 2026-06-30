package com.example.account.credential.config;

import com.example.account.credential.interceptor.UserTokenInterceptor;
import com.example.account.credential.service.UserCredentialService;
import com.example.core.extension.InterceptorCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 用户拦截器配置
 *
 * <h2>说明
 * <p>实现 InterceptorCustomizer 接口，向 Spring MVC 注册用户令牌拦截器。
 * <p>拦截器对所有路径生效，由拦截器内部通过 @UserPermission 注解判断是否需要处理。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class UserInterceptorConfig implements InterceptorCustomizer {

    /** 用户认证凭证服务 */
    private final UserCredentialService userCredentialService;

    // ================================ public 方法 ================================

    /**
     * 创建用户令牌拦截器 Bean
     *
     * @return 用户令牌拦截器实例
     */
    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor(userCredentialService);
    }

    /**
     * 注册拦截器
     *
     * <h3>配置说明
     * <p>将 UserTokenInterceptor 注册到 Spring MVC 拦截器链，对所有路径生效。
     * <p>拦截器内部通过 @UserPermission 注解判断是否需要处理。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor()).addPathPatterns("/**");
    }
}
