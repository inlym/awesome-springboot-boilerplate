package com.example.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

/**
 * 国际化配置类
 *
 * <h2>配置说明
 * <p>提供多语言支持配置，包括 MessageSource Bean 的定义和配置。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
@RequiredArgsConstructor
public class I18nConfig {

    // ================================ 依赖注入 ================================

    /** 国际化配置属性 */
    private final I18nProperties i18nProperties;

    // ================================ public 方法 ================================

    /**
     * 配置 LocaleResolver Bean
     *
     * <h3>配置说明
     * <p>提供本地化解析器，用于确定应用程序的语言偏好，配置如下：
     * <p>1. 使用 FixedLocaleResolver 根据配置文件中的固定语言设置
     * <p>2. 默认语言通过 project.i18n.language 配置项指定，引用 feature.language 属性
     * <p>3. 支持的语言包括中文、英文
     *
     * @return LocaleResolver 本地化解析器实例
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new FixedLocaleResolver(
            i18nProperties.getLanguage().getLocale()
        );
    }

    /**
     * 配置 MessageSource Bean
     *
     * <h3>配置说明
     * <p>提供多语言消息源支持，配置如下：
     * <p>1. 设置多语言文件基础路径为 classpath:i18n/messages
     * <p>2. 支持动态刷新，便于开发调试
     * <p>3. 使用 UTF-8 编码，支持中文字符
     * <p>4. 设置缓存时间为 1 小时，生产环境可根据需要调整
     *
     * @return MessageSource 消息源实例
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        // 设置多语言文件基础路径
        messageSource.setBasename("classpath:i18n/messages");

        // 设置默认编码为 UTF-8
        messageSource.setDefaultEncoding("UTF-8");

        // 设置缓存时间，生产环境建议设置更长缓存时间
        messageSource.setCacheSeconds(3600);

        return messageSource;
    }
}