package com.example.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * 国际化配置类
 *
 * <h2>配置说明
 * <p>配置 LocaleResolver，根据客户端 Accept-Language 请求头解析语言。MessageSource 由 Spring Boot 自动配置，配置项见 spring.messages。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
public class I18nConfig {

    // ================================ 静态常量 ================================

    /** 默认语言 */
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("zh-CN");

    /** 支持的语言列表 */
    private static final List<Locale> SUPPORTED_LOCALES = List.of(
        Locale.forLanguageTag("zh-CN"),
        Locale.forLanguageTag("en-US")
    );

    // ================================ public 方法 ================================

    /**
     * 配置 LocaleResolver Bean
     *
     * <h3>配置说明
     * <p>使用 AcceptHeaderLocaleResolver 解析客户端 Accept-Language 请求头，根据支持的语言列表匹配。
     *
     * @return LocaleResolver 本地化解析器实例
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();

        // 限定可识别的语言为中文和英文，白名单外的请求回退到默认语言
        resolver.setSupportedLocales(SUPPORTED_LOCALES);

        // 设置回退语言，用于客户端未发送 Accept-Language 或请求语言不在白名单时的兜底
        resolver.setDefaultLocale(DEFAULT_LOCALE);

        return resolver;
    }
}
