# 服务器初始化

Ubuntu 服务器初始化脚本，安装 OpenJDK 25 JRE 并注册 systemd 服务。

## 前置条件

- Ubuntu Server（已在 26.04 LTS 验证）
- root 权限

## 初始化

将本目录上传到服务器后执行：

```bash
sudo bash init-server.sh
```

脚本完成以下工作：

1. 安装 `openjdk-25-jre-headless`
2. 创建应用部署目录 `/opt/awesome-springboot-boilerplate`
3. 安装 systemd 单元到 `/etc/systemd/system/`
4. 注册开机自启（不启动服务）

## 部署应用

初始化完成后，按以下步骤部署 jar：

1. 上传 jar 到 `/opt/awesome-springboot-boilerplate/app.jar`
2. 创建密钥配置文件 `~/.config/application-prod-secret.yml`，填入 `application.yml` 中 `${...}` 占位符的真实值（数据库、Redis、OpenAI、OTLP 接入点等）：

   ```yaml
   MYSQL_HOST: <MySQL 主机地址>
   MYSQL_USERNAME: <MySQL 用户名>
   MYSQL_PASSWORD: <MySQL 密码>

   REDIS_HOST: <Redis 主机地址>
   REDIS_USERNAME: <Redis 用户名，无则留空>
   REDIS_PASSWORD: <Redis 密码>

   OPENAI_API_KEY: <OpenAI API Key>

   OTLP_HTTP_TRACING_ENDPOINT: <阿里云 Trace 接入点>
   OTLP_HTTP_METRICS_ENDPOINT: <阿里云 Metric 接入点>
   ```

   密钥文件含敏感信息，收紧权限：

   ```bash
   chmod 600 ~/.config/application-prod-secret.yml
   ```

   > 路径由 `application-prod.yml` 中 `spring.config.import: file:${user.home}/.config/application-prod-secret.yml` 决定，需与服务运行用户（systemd unit 中的 `User`）的家目录一致。降权运行时同步迁移该文件。

3. 启动服务：

   ```bash
   systemctl start awesome-springboot-boilerplate
   ```

## 常用命令

```bash
systemctl status awesome-springboot-boilerplate    # 查看状态
systemctl restart awesome-springboot-boilerplate   # 重启
journalctl -u awesome-springboot-boilerplate -f    # 查看日志
```
