package com.example.core.support.jackson;

import org.springframework.boot.jackson.JacksonComponent;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.Instant;

/**
 * Instant 毫秒时间戳反序列化器
 *
 * <h2>功能说明
 * <p>将 JSON 中的毫秒时间戳（长整型）反序列化为 Java 的 Instant 对象。
 * <p>用于处理前端传递的毫秒级时间戳数据。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@JacksonComponent
public class InstantMillisDeserializer extends ValueDeserializer<Instant> {

    /**
     * 将 JSON 中的毫秒时间戳反序列化为 Instant 对象
     *
     * @param p    JSON 解析器，用于读取 JSON 数据
     * @param ctxt 反序列化上下文
     * @return 反序列化后的 Instant 对象
     */
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) {
        return Instant.ofEpochMilli(p.getLongValue());
    }
}
