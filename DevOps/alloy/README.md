# Grafana Alloy 部署指南

## 架构

```
Application (Spring Boot)                 Grafana Alloy (Sidecar)                Backend
─────────────────────────                ────────────────────────                ───────
Traces ──OTLP HTTP──> 4318 ──┐           ┌─ OTLP Receiver (4317/4318)           Traces  → Tempo
Metrics ─OTLP HTTP──> 4318 ──┤           ├─ Memory Limiter                      Metrics → Mimir
Logs ──File write──> /var/log ─┤           ├─ Batch Processor                    Logs    → Loki
                              │           ├─ File Tailer
                              └──────────>├─ OTLP Exporter ──> Backend
                                          └─ Loki Writer   ──> Backend
```

## 快速开始

### 环境变量

| 变量 | 必需 | 说明 | 示例 |
|------|------|------|------|
| `OTLP_BACKEND_ENDPOINT` | 是 | Traces/Metrics 后端 OTLP 接入点 | `https://tempo.example.com:4318` |
| `LOKI_ENDPOINT` | 是 | Loki 日志写入地址 | `http://loki:3100/loki/api/v1/push` |
| `OTLP_ENDPOINT` | 否 | 应用侧上报端点（应用环境变量） | `http://alloy:4318` |

### Docker Compose 示例

```yaml
services:
    alloy:
        image: grafana/alloy:latest
        container_name: alloy
        command:
            - run
            - --server.http.listen-addr=0.0.0.0:12345
            - --storage.path=/var/lib/alloy/data
            - /etc/alloy/config.alloy
        volumes:
            # Alloy 配置文件
            - ./DevOps/alloy/config.alloy:/etc/alloy/config.alloy:ro
            # 共享日志目录，Alloy 从此目录采集应用日志
            - app-logs:/var/log/awesome-springboot-boilerplate:ro
            # Alloy 数据持久化
            - alloy-data:/var/lib/alloy/data
        ports:
            - "4317:4317"   # OTLP gRPC
            - "4318:4318"   # OTLP HTTP
            - "12345:12345" # Alloy 调试 UI
        environment:
            - OTLP_BACKEND_ENDPOINT=${OTLP_BACKEND_ENDPOINT}
            - LOKI_ENDPOINT=${LOKI_ENDPOINT}
        restart: unless-stopped

    app:
        image: awesome-springboot-boilerplate:latest
        container_name: app
        volumes:
            # 与 Alloy 共享日志目录
            - app-logs:/var/log/awesome-springboot-boilerplate
        ports:
            - "8080:8080"
        environment:
            # 应用遥测数据指向本地 Alloy
            - OTLP_ENDPOINT=http://alloy:4318

volumes:
    app-logs:
    alloy-data:
```

### 本地调试

本地开发时无需启动 Alloy，应用配置中 `OTLP_ENDPOINT` 默认值为 `http://localhost:4318`，Alloy 未运行时上报静默失败，不影响应用启动。

需要调试遥测链路时，启动 Alloy：

```bash
# macOS
brew install grafana-alloy
alloy run --server.http.listen-addr=0.0.0.0:12345 DevOps/alloy/config.alloy

# Docker
docker run --rm \
    -v $(pwd)/DevOps/alloy/config.alloy:/etc/alloy/config.alloy:ro \
    -p 4317:4317 -p 4318:4318 -p 12345:12345 \
    -e OTLP_BACKEND_ENDPOINT=http://host.docker.internal:4318 \
    -e LOKI_ENDPOINT=http://host.docker.internal:3100/loki/api/v1/push \
    grafana/alloy:latest \
    run --server.http.listen-addr=0.0.0.0:12345 /etc/alloy/config.alloy
```

## 调试

### Alloy 调试 UI

访问 `http://localhost:12345` 查看 Alloy 运行状态和组件图。

### 启用实时调试

在 `config.alloy` 顶部添加：

```alloy
logging {
    level = "debug"
}

livedebugging {
    enabled = true
}
```

启用后可在调试 UI 中查看每个组件接收和输出的数据。

### 验证数据流

```bash
# 确认 Alloy 正在接收 OTLP 数据
curl http://localhost:12345/debug/api/v1/traces

# 确认日志文件存在且可读
ls -la /var/log/awesome-springboot-boilerplate/
```

## 生产环境建议

1. **资源限制**：`memory_limiter` 的 `limit_mib` 根据容器内存配额调整，建议不超过容器内存的 60%
2. **日志卷**：使用 tmpfs 或 SSD 支持的高 IOPS 卷存放日志，避免日志写入成为瓶颈
3. **TLS**：Alloy 与后端通信启用 TLS，配置 `tls` 块
4. **认证**：通过 `otelcol.auth.basic` 和 Loki `basic_auth` 配置后端鉴权
5. **属性脱敏**：添加 `otelcol.processor.attributes` 过滤或脱敏敏感属性
