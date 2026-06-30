package com.example.core.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

/**
 * 语言类型枚举
 *
 * <h2>说明
 * <p>定义系统支持的语言类型，用于多语言场景的语言标识。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum LanguageType {

    // ================================ 枚举常量 ================================

    /**
     * 中文
     *
     * <h3>字段说明
     * <p>支持中国大陆简体中文
     */
    CHINESE("zh", "zh-CN"),

    /**
     * 英文
     *
     * <h3>字段说明
     * <p>支持美国英语
     */
    ENGLISH("en", "en-US");

    // ================================ 枚举字段 ================================

    /**
     * ISO 语言代码
     *
     * <h3>字段说明
     * <p>ISO 639-1 标准的两字母语言代码
     * <p>示例值：CHINESE 对应 "zh"，ENGLISH 对应 "en"
     */
    @EnumValue
    private final String isoCode;

    /**
     * 语言区域代码
     *
     * <h3>字段说明
     * <p>完整的语言-区域组合代码，符合 IETF BCP 47 标准
     * <p>示例值：CHINESE 对应 "zh-CN"，ENGLISH 对应 "en-US"
     */
    private final String localeCode;

    // ================================ public 方法 ================================

    /**
     * 获取 Locale 对象
     *
     * <h3>方法说明
     * <p>将 localeCode 转换为 Java Locale 对象，用于国际化配置
     *
     * @return Locale 语言区域对象
     */
    public Locale getLocale() {
        return Locale.forLanguageTag(localeCode);
    }
}