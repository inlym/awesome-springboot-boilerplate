package com.example.core.extension;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * 拦截器定制器接口
 *
 * <h2>说明
 * <p>业务模块可实现此接口，向 Spring MVC 注册自定义拦截器。
 * <p>核心模块会自动收集所有实现类，在 WebMvcConfig 初始化时依次调用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
public interface InterceptorCustomizer {

    /**
     * 注册拦截器
     *
     * @param registry 拦截器注册表
     */
    void addInterceptors(InterceptorRegistry registry);
}
