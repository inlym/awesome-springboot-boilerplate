package com.example.core.support.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.time.Instant;

/**
 * Instant 毫秒时间戳序列化器
 *
 * <h2>功能说明
 * <p>将 Java 的 Instant 对象序列化为 JSON 中的毫秒时间戳（长整型）。
 * <p>用于向前端传递毫秒级时间戳格式的数据。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
public class InstantMillisSerializer extends ValueSerializer<Instant> {

    /**
     * 将 Instant 对象序列化为毫秒时间戳
     *
     * @param instant     要序列化的 Instant 对象
     * @param gen         JSON 生成器，用于写入 JSON 数据
     * @param serializers 序列化上下文
     */
    @Override
    public void serialize(
        Instant instant,
        JsonGenerator gen,
        SerializationContext serializers
    ) {
        gen.writeNumber(instant.toEpochMilli());
    }
}
