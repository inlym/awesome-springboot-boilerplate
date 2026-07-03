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
- **Micrometer** + **OpenTelemetry**（指标与链路追踪）
- **Logback** + **Logstash JSON Encoder**（结构化日志）
- **Maven** 多模块 + `flatten-maven-plugin`（统一版本管理）

## 模块结构

```
awesome-springboot-boilerplate/
├── pom.xml                          # 根聚合 pom，统一依赖与插件管理
├── .apifox-helper.properties        # Apifox Helper 插件配置
├── mybatis-flex.config              # MyBatis-Flex APT 配置
├── project-core/                    # 核心共享组件和配置（异常、注解、工具类、配置类）
├── project-system/                  # 系统运行模块（健康检查、应用与运行时信息、健康指标、请求回显）
├── project-account/                 # 账户模块（业务模块示例：用户信息、认证凭证、用户设置）
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

- **actuator**
    - `health`：MySQL / Redis 健康检查
    - `info`：应用元数据（AppInfo）、运行时信息（内存、磁盘、JDK）、服务器 IP
    - `metrics`：系统健康指标（ping 请求计数）
- **echo**：请求回显（调试用）

### project-account

业务模块示例，演示如何在模板之上构建实际业务能力：

- **user**：用户信息维护、账户注销（含状态枚举、专属异常）
- **credential**：用户认证凭证（token）签发、查询、续期，基于 Redis 缓存 + DB 双写；配套 `UserTokenInterceptor` 完成请求级鉴权
- **setting**：用户偏好设置（key-value 结构，未设置时使用系统默认值）

### project-bootstrap

启动入口模块，包含：

- `Application.java`：主启动类，扫描 `com.example` 包，`@MapperScan` 扫描 `com.example.**.mapper`
- `application.yml`：通用配置
- `application-local.yml`：本地环境配置，敏感凭证通过 `spring.config.import` 从外部文件引入
- `logback-spring.xml`：结构化日志配置
- `i18n/messages_{zh,en}.properties`：通用响应消息

## 快速开始

### 1. 环境要求

- JDK 25+
- Maven 3.9+
- MySQL 8+
- Redis 7+

### 2. 配置密钥文件

敏感凭证（数据库密码、API 密钥等）存放在项目目录之外的密钥文件中，通过 `spring.config.import` 引入，避免凭证泄露到版本控制。

在 `~/.config/` 目录下创建 `application-local-secret.yml`，填入真实值：

```yaml
MYSQL_HOST: "localhost"
MYSQL_USERNAME: "root"
MYSQL_PASSWORD: "your-password"
REDIS_HOST: "localhost"
REDIS_USERNAME: ""
REDIS_PASSWORD: ""
OPENAI_API_KEY: "sk-your-key"
OTLP_HTTP_TRACING_ENDPOINT: "http://tracing-analysis-dc-hz.aliyuncs.com/adapt_<instance>@<token>/api/otlp/traces"
OTLP_HTTP_METRICS_ENDPOINT: "http://tracing-analysis-dc-hz.aliyuncs.com/adapt_<instance>@<token>/api/otlp/metrics"
```

`application-local.yml` 已通过 `file:${user.home}/.config/application-local-secret.yml` 导入该文件，并使用 `${MYSQL_HOST}` 等占位符引用其中的值。只需确保密钥文件路径正确、变量名匹配即可。

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
| `bootstrap/pom.xml`                           | `<finalName>awesome-springboot-boilerplate</finalName>`             |
| `application.yml`                             | `management.metrics.tags.application`                               |
| `application-local.yml`                       | `spring.application.name`                                           |
| `I18nProperties.java`                         | `@ConfigurationProperties(prefix = "project.i18n")`                 |
| `application.yml` 中的 `project.openai.api-key` | 配置前缀                                                                |
| `CustomHttpHeader.java`                       | `x-project-*` HTTP 头常量                                              |
| `SystemHealthMetrics.java`                    | `project.system.health.ping.requests` 指标名                           |
| `AppInfoContributor.java`                     | `APP_NAME` 与 `APP_DESCRIPTION`                                      |
| `logback-spring.xml`                          | 默认日志路径 `/var/log/awesome-springboot-boilerplate`                    |
| `CLAUDE.md`                                   | 模块结构图                                                               |

### 3. 业务标识（应用名、描述）

| 位置                                   | 当前值                                | 替换建议   |
|--------------------------------------|------------------------------------|--------|
| `AppInfoContributor.APP_NAME`        | `"Awesome Springboot Boilerplate"` | 你的应用名  |
| `AppInfoContributor.APP_DESCRIPTION` | `"Spring Boot 4.x 项目后端服务"`         | 你的业务描述 |
| `README.md`                          | 当前文件                               | 你的项目说明 |

### 4. Spring AI（可选）

`pom.xml` 默认引入 `spring-ai-starter-model-openai` 和 `spring-ai-starter-model-bedrock-converse`。若不需要 AI 能力，从根
`pom.xml` 的 `<dependencies>` 中移除这两个 starter 及 `spring-ai-bom`。

替换 OpenAI API 密钥：编辑 `application.yml` 中的 `project.openai.api-key`。

## 配置说明

### Profile 切换

模板当前仅提供 `local` profile（默认）。切换方式：修改 `application.yml` 的 `spring.profiles.active`，或启动时通过
`--spring.profiles.active=<profile>` 指定。新增环境时在 `project-bootstrap/src/main/resources/` 下创建对应的
`application-<profile>.yml`，并按需通过 `spring.config.import` 引入外部密钥文件。

### 日志

- 控制台：彩色格式，包含 trace ID（若有）
- 文件：`/var/log/awesome-springboot-boilerplate/app.log`（默认路径，可在 yml 中通过 `logging.file.path` 覆盖）
- 滚动：单文件 5MB，保留 30 天，总上限 1GB
- JSON 格式：包含 timestamp、level、thread、logger、message、traceId、CLIENT_IP、stackTrace

### 监控端点

`/actuator/health`、`/actuator/info`、`/actuator/metrics`（local 环境暴露全部）。

## 项目约定

详细的代码规范、命名约定、工作流规范见 [CLAUDE.md](CLAUDE.md) 及 `.claude/rules/` 目录（如已配置）。
