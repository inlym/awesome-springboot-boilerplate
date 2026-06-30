package com.example.system.actuator.info;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 应用元数据 Info 端点贡献者
 *
 * <h2>说明
 * <p>向 /actuator/info 端点贡献应用基础信息（名称、描述、版本、构建时间）。
 *
 * <h2>版本来源
 * <p>版本与构建时间从 META-INF/build-info.properties 读取（由 spring-boot-maven-plugin 的 build-info goal 生成），避免手动维护导致漂移。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class AppInfoContributor implements InfoContributor {

    /** 应用名称 */
    private static final String APP_NAME = "Awesome Springboot Boilerplate";

    /** 应用描述 */
    private static final String APP_DESCRIPTION = "Spring Boot 4.x 项目后端服务";

    /** 构建产物元数据，提供 version 与 buildTime */
    private final BuildProperties buildProperties;

    @Override
    public void contribute(Info.Builder builder) {
        // 用 LinkedHashMap 保证字段输出顺序稳定，便于人读
        Map<String, Object> appDetails = new LinkedHashMap<>();
        appDetails.put("name", APP_NAME);
        appDetails.put("description", APP_DESCRIPTION);
        appDetails.put("version", buildProperties.getVersion());
        appDetails.put("buildTime", buildProperties.getTime());

        // 将应用基础信息以 "app" 键贡献到 Info 端点
        builder.withDetail("app", appDetails);
    }
}

