package com.example.system.actuator.info;

import com.example.core.service.I18nService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.boot.system.ApplicationPid;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 运行时元信息 Info 端点贡献者
 *
 * <h2>说明
 * <p>向 /actuator/info 端点贡献框架版本、进程标识、部署路径、激活 profile 与语言等静态运行时元信息。
 *
 * <h2>字段来源
 * <p>Spring 版本取自 SpringVersion / SpringBootVersion；进程名取自 RuntimeMXBean；路径取自 ApplicationHome / user.dir / ApplicationTemp；PID 取自 ApplicationPid。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class RuntimeInfoContributor implements InfoContributor {

    /** Spring 环境对象，用于读取激活的 profile */
    private final Environment environment;

    /** 国际化服务，用于读取当前应用语言 */
    private final I18nService i18nService;

    @Override
    public void contribute(Info.Builder builder) {
        // 用 LinkedHashMap 保证字段输出顺序稳定，便于人读
        Map<String, Object> runtimeDetails = new LinkedHashMap<>();
        // 框架版本
        runtimeDetails.put("springVersion", SpringVersion.getVersion());
        runtimeDetails.put("springBootVersion", SpringBootVersion.getVersion());
        // 进程标识
        runtimeDetails.put("processName", ManagementFactory.getRuntimeMXBean().getName());
        runtimeDetails.put("pid", new ApplicationPid().toString());
        // 部署路径
        runtimeDetails.put("deployDir", new ApplicationHome().getDir().getAbsolutePath());
        runtimeDetails.put("workDir", System.getProperty("user.dir"));
        runtimeDetails.put("tempDir", new ApplicationTemp().getDir().getAbsolutePath());
        // 应用配置
        runtimeDetails.put("activeProfiles", environment.getActiveProfiles());
        runtimeDetails.put("language", i18nService.getMessage("language.current"));

        // 将运行时元信息以 "runtime" 键贡献到 Info 端点
        builder.withDetail("runtime", runtimeDetails);
    }
}
