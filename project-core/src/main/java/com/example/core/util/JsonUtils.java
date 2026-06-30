package com.example.core.util;

import com.example.core.support.jackson.InstantMillisDeserializer;
import com.example.core.support.jackson.InstantMillisSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.module.SimpleModule;

import java.time.Instant;
import java.util.TimeZone;

/**
 * JSON 序列化工具类
 *
 * <h2>功能说明
 * <p>提供统一配置的 JsonMapper 实例，用于 JSON 序列化和反序列化操作
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
public final class JsonUtils {

    /** JSON 序列化映射器 */
    private static final JsonMapper JSON_MAPPER = createJsonMapper();

    // 私有构造函数，防止实例化
    private JsonUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    // ================================ public 方法 ================================

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param object 待序列化的对象
     * @return JSON 字符串
     */
    public static String stringify(Object object) {
        return JSON_MAPPER.writeValueAsString(object);
    }

    /**
     * 将 JSON 字符串反序列化为指定类型对象
     *
     * @param jsonString JSON 字符串
     * @param valueType  目标对象的 Class 类型
     * @param <T>        目标对象类型
     * @return 反序列化后的对象
     */
    public static <T> T parse(String jsonString, Class<T> valueType) {
        return JSON_MAPPER.readValue(jsonString, valueType);
    }

    /**
     * 将 JsonNode 对象转换为指定的 Java 对象类型
     *
     * @param jsonNode  JSON 节点对象
     * @param valueType 目标对象的 Class 类型
     * @param <T>       目标对象类型
     * @return 转换后的对象
     */
    public static <T> T convert(JsonNode jsonNode, Class<T> valueType) {
        return JSON_MAPPER.treeToValue(jsonNode, valueType);
    }

    /**
     * 将 JsonNode 对象转换为指定的 Java 对象类型
     *
     * @param jsonNode      JSON 节点对象
     * @param typeReference 目标对象的 TypeReference 类型
     * @param <T>           目标对象类型
     * @return 转换后的对象
     */
    public static <T> T convert(JsonNode jsonNode, TypeReference<T> typeReference) {
        return JSON_MAPPER.convertValue(jsonNode, typeReference);
    }

    /**
     * 将 JSON 字符串解析为 JsonNode 树结构
     *
     * @param jsonString JSON 字符串
     * @return JsonNode 对象
     */
    public static JsonNode readTree(String jsonString) {
        return JSON_MAPPER.readTree(jsonString);
    }

    /**
     * 创建并配置 JsonMapper 实例
     *
     * <h3>使用场景
     * <p>同时被 {@code JsonUtils.JSON_MAPPER} 静态字段和 {@code JacksonConfig} 的 JsonMapper Bean 复用，是项目统一序列化配置的唯一入口。
     *
     * <h3>配置说明
     * <p>Instant 自定义毫秒时间戳序列化通过 SimpleModule 注册（Jackson 3.x 不再需要 JavaTimeModule，JSR-310 内置支持）。
     *
     * @return 配置好的 JsonMapper 实例
     */
    public static JsonMapper createJsonMapper() {
        // 注册 Instant 自定义序列化器，统一以毫秒时间戳传输
        SimpleModule instantModule = new SimpleModule();
        instantModule.addSerializer(Instant.class, new InstantMillisSerializer());
        instantModule.addDeserializer(Instant.class, new InstantMillisDeserializer());

        return JsonMapper
            .builder()
            .addModule(instantModule)
            // API 字段变更时不中断旧版本客户端的反序列化
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 第三方服务返回新枚举值时，映射为 null 而不中断反序列化
            .enable(EnumFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            // DTO 字段全为 null 被 NON_NULL 过滤后，序列化空对象不抛异常
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // 时间戳基准时区，避免因运行环境时区差异导致序列化结果不一致
            .defaultTimeZone(TimeZone.getTimeZone("UTC"))
            // 响应体不输出 null 字段，减少无效传输
            .changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
            .build();
    }
}
