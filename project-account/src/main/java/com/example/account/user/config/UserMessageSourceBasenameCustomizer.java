package com.example.account.user.config;

import com.example.core.extension.MessageSourceBasenameCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 用户模块消息源 basename 定制器
 *
 * <h2>说明
 * <p>声明账户模块用户子模块（account.user）的多语言资源文件 basename，资源文件位于 classpath:i18n/account-user_*.properties。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Configuration
public class UserMessageSourceBasenameCustomizer implements MessageSourceBasenameCustomizer {

    // ================================ public 方法 ================================

    /**
     * 声明消息源 basename
     *
     * @return 用户模块 basename 列表
     */
    @Override
    public List<String> declareBasenames() {
        return List.of("i18n/account-user");
    }
}
