package com.example.core.config;

import com.example.core.util.JsonUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson JSON 序列化配置类
 *
 * <h2>配置说明
 * <p>配置 Spring Boot 应用中的 Jackson JSON 序列化和反序列化行为，包括日期时间格式、时区和序列化策略。
 * <p>统一应用中所有 JSON 数据的格式化标准，确保前后端数据交互的一致性。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 **/
@Configuration
public class JacksonConfig {

    /**
     * 配置 Jackson JsonMapper Bean
     *
     * @return 配置完成的 JsonMapper 实例
     */
    @Bean
    public JsonMapper jsonMapper() {
        return JsonUtils.createJsonMapper();
    }
}
