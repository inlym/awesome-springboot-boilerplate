package com.example.system.actuator.info;

import com.example.core.startup.ServerIpInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 服务器 IP Info 端点贡献者
 *
 * <h2>说明
 * <p>向 /actuator/info 端点贡献服务器 IP 信息（内网 IP、公网 IP），便于运维人员快速定位部署节点。
 *
 * <h2>数据来源
 * <p>从 ServerIpInitializer 在启动阶段已探测并缓存的服务器 IP 读取，访问端点时不触发新的网络请求。
 *
 * @author <a href="https://www.inlym.com">inlym</a>
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class ServerIpInfoContributor implements InfoContributor {

    /** 服务器 IP 初始化器，提供启动阶段探测到的内网 IP 与公网 IP */
    private final ServerIpInitializer serverIpInitializer;

    @Override
    public void contribute(Info.Builder builder) {
        // 用 LinkedHashMap 保证字段输出顺序稳定，便于人读
        Map<String, Object> serverIpDetails = new LinkedHashMap<>();
        serverIpDetails.put("internalIp", serverIpInitializer.getInternalIp());
        serverIpDetails.put("publicIp", serverIpInitializer.getPublicIp());

        // 将服务器 IP 信息以 "serverIp" 键贡献到 Info 端点
        builder.withDetail("serverIp", serverIpDetails);
    }
}
