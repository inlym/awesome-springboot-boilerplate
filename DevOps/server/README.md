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
2. 按需创建 `/etc/default/awesome-springboot-boilerplate` 配置环境变量：

   ```text
   # JVM 参数
   JAVA_OPTS=-Xmx512m
   # Spring 激活的 profile（需对应已存在的 application-*.yml）
   SPRING_PROFILES_ACTIVE=local
   ```

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
