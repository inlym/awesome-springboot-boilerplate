# awesome-springboot-boilerplate

基于 Spring Boot 4.x 的多模块项目启动模板，封装了通用的后端基础设施（异常处理、参数校验、日志、监控、安全管理、Redis、MyBatis-Flex
等），可用于快速启动一个新的后端项目。

## 技术栈

- **Spring Boot** 4.1.0 + **Java** 25
- **MyBatis-Flex** 1.11.7（数据访问）
- **MySQL** + **HikariCP**（数据库与连接池）
- **Redis**（缓存与会话）
- **Spring Security** + 自定义注解（鉴权）
- **Spring AI** 2.0.0（OpenAI / Bedrock 集成）
- **Micrometer** + **Prometheus**（指标监控）
- **Logback** + **Logstash JSON Encoder**（结构化日志）
- **Maven** 多模块 + `flatten-maven-plugin`（统一版本管理）

## 模块结构

```
awesome-springboot-boilerplate/
├── pom.xml                          # 根聚合 pom，统一依赖与插件管理
├── .apifox-helper.properties        # Apifox Helper 插件配置
├── mybatis-flex.config              # MyBatis-Flex APT 配置
├── project-core/                    # 核心共享组件和配置（异常、注解、工具类、配置类）
├── project-system/                  # 系统运行模块（运维相关：管理员鉴权、Redis 管理、系统信息、健康检查）
└── project-bootstrap/               # 启动模块（主类、配置文件、日志配置）
```

### project-core

承载与业务无关的通用后端能力：

- **异常体系**：`BaseException` 及多个子类，配合 `GlobalExceptionHandler` 统一转换
- **响应模型**：`StandardResponse` / `EmptyResponse` / `PageableListResponse`
- **自定义注解**：`@UserId`、`@AdminPermission`、`@LogExecution` 等，配套 MethodArgumentResolver 与 AOP 切面
- **配置类**：Jackson、Redis、Async、Schedule、I18n、Security、WebSocket、MyBatis-Flex
- **工具类**：`JsonUtils`、`DateTimeUtils`、`RandomUtils`、`LogUtils`
- **上下文传播**：基于 Micrometer Context Propagation 的 MDC / Observation 跨线程传播

### project-system

承载与系统运行相关的能力（不含业务逻辑）：

- **actuator**：MySQL / Redis 健康检查、应用元数据、运行时信息、系统健康指标
- **admin**：管理员令牌签发与鉴权（基于 Redis 的 token 存储）
- **redis**：Redis 管理 API（键值查询、概览）
- **runtime**：系统资源信息（内存、磁盘）
- **echo**：请求回显（调试用）

### project-bootstrap

启动入口模块，包含：

- `Application.java`：主启动类，扫描 `com.example` 包
- `application.yml`：通用配置
- `application-{local,dev,prod}.yml`：环境配置（占位符凭证，需替换）
- `logback-spring.xml`：结构化日志配置
- `i18n/messages_{zh,en}.properties`：通用响应消息

## 快速开始

### 1. 环境要求

- JDK 25+
- Maven 3.9+
- MySQL 8+
- Redis 7+

### 2. 配置数据库与 Redis

编辑 `project-bootstrap/src/main/resources/application-local.yml`，替换占位符：

```yaml
spring:
    datasource:
        url: jdbc:mysql://your-mysql-host:3306/your-database?...
        username: "your-username"
        password: "your-password"
    data:
        redis:
            host: "your-redis-host"
            password: "your-password"
```

### 3. 启动应用

```bash
cd project-bootstrap
mvn spring-boot:run
```

默认端口 `35025`，访问 `http://localhost:35025/actuator/health` 验证启动。

> 跨模块构建请先在根目录执行 `mvn clean install -DskipTests`，否则本地仓库残留 jar 可能导致 `TypeNotPresentException`。

## 定制化指南

将本模板改造为你自己的项目时，需要替换以下内容。

### 1. 包名（`com.example` → `com.yourbrand`）

| 位置                                     | 说明                                                                     |
|----------------------------------------|------------------------------------------------------------------------|
| `project-*/src/main/java/com/example/` | 重命名目录为 `com/yourbrand/`                                                |
| 所有 Java 文件                             | `package com.example.*` 与 `import com.example.*` 中的 `com.example` 全局替换 |
| `pom.xml`（根与子模块）                       | `<groupId>com.example</groupId>`                                       |
| `Application.java`                     | `scanBasePackages = {"com.example"}` 和 `@MapperScan` 的 `basePackages`  |
| `logback-spring.xml`                   | `logging.level.com.example` 与 `<logger name="com.example">`            |
| `application-*.yml`                    | `logging.level.com.example`                                            |
| `.apifox-helper.properties`            | `com.example.core.annotation.*` 引用                                     |

替换命令示例：

```bash
find . -type f \( -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.properties" \) \
  -exec sed -i '' 's/com\.example/com.yourbrand/g' {} +
mv project-core/src/main/java/com/example project-core/src/main/java/com/yourbrand
# 其余模块同理
```

### 2. 项目主题名（`project` → `yourproduct`）

| 位置                                            | 说明                                                                  |
|-----------------------------------------------|---------------------------------------------------------------------|
| `pom.xml`（根与子模块）                              | `<artifactId>project-*</artifactId>` 与 `<module>project-*</module>` |
| 模块目录名                                         | `project-core/`、`project-system/`、`project-bootstrap/`              |
| `bootstrap/pom.xml`                           | `<finalName>awesome-springboot-boilerplate</finalName>`                             |
| `application.yml`                             | `management.metrics.tags.application`                               |
| `application-{local,dev,prod}.yml`            | `spring.application.name`                                           |
| `I18nProperties.java`                         | `@ConfigurationProperties(prefix = "project.i18n")`                 |
| `application.yml` 中的 `project.openai.api-key` | 配置前缀                                                                |
| `CustomHttpHeader.java`                       | `x-project-*` HTTP 头常量                                              |
| `SystemHealthMetrics.java`                    | `project.system.health.ping.requests` 指标名                           |
| `AppInfoContributor.java`                     | `APP_NAME` 与 `APP_DESCRIPTION`                                      |
| `logback-spring.xml`                          | 默认日志路径 `/var/log/awesome-springboot-boilerplate`                                    |
| `CLAUDE.md`                                   | 模块结构图                                                               |

### 3. 业务标识（应用名、描述）

| 位置                                   | 当前值                        | 替换建议   |
|--------------------------------------|----------------------------|--------|
| `AppInfoContributor.APP_NAME`        | `"Awesome Springboot Boilerplate"`         | 你的应用名  |
| `AppInfoContributor.APP_DESCRIPTION` | `"Spring Boot 4.x 项目后端服务"` | 你的业务描述 |
| `README.md`                          | 当前文件                       | 你的项目说明 |

### 4. Spring AI（可选）

`pom.xml` 默认引入 `spring-ai-starter-model-openai` 和 `spring-ai-starter-model-bedrock-converse`。若不需要 AI 能力，从根
`pom.xml` 的 `<dependencies>` 中移除这两个 starter 及 `spring-ai-bom`。

替换 OpenAI API 密钥：编辑 `application.yml` 中的 `project.openai.api-key`。

## 配置说明

### Profile 切换

- `local`（默认）：本地开发

切换方式：修改 `application.yml` 的 `spring.profiles.active`，或启动时通过 `--spring.profiles.active=<profile>` 指定。新增环境时在
`project-bootstrap/src/main/resources/` 下创建对应的 `application-<profile>.yml`。

### 日志

- 控制台：彩色格式，包含 trace ID（若有）
- 文件：`/var/log/awesome-springboot-boilerplate/app.log`（默认路径，可在 yml 中通过 `logging.file.path` 覆盖）
- 滚动：单文件 5MB，保留 30 天，总上限 1GB
- JSON 格式：包含 timestamp、level、thread、logger、message、traceId、CLIENT_IP、stackTrace

### 监控端点

`/actuator/health`、`/actuator/info`、`/actuator/metrics`、`/actuator/prometheus`（local 环境暴露全部）。

## 项目约定

详细的代码规范、命名约定、工作流规范见 [CLAUDE.md](CLAUDE.md) 及 `.claude/rules/` 目录（如已配置）。
