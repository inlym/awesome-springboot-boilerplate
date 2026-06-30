package com.example.core.extension;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * SecurityFilterChain 定制器接口
 *
 * <h2>说明
 * <p>业务模块可实现此接口，自定义 SecurityFilterChain 配置。
 * <p>核心模块会自动收集所有实现类，在基础配置完成后依次调用。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
public interface SecurityFilterChainCustomizer {

    /**
     * 定制 SecurityFilterChain 配置
     *
     * @param http HttpSecurity 配置对象
     * @throws Exception 配置过程中可能出现的异常
     */
    void customize(HttpSecurity http) throws Exception;
}