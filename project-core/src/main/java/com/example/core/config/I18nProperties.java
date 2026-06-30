package com.example.core.config;

import com.example.core.enums.LanguageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 国际化配置属性类
 *
 * <h2>配置说明
 * <p>封装国际化模块所有配置项，使用 @ConfigurationProperties 注解自动绑定配置文件中的值。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "project.i18n")
public class I18nProperties {

    // ================================ 配置项 ================================

    /**
     * 语言类型
     *
     * <h3>字段说明
     * <p>应用程序默认语言，引用 feature.language 属性实现统一配置
     */
    @NotNull(message = "语言类型不能为空")
    private LanguageType language;
}
