#!/usr/bin/env bash
# 初始化 Ubuntu 服务器：安装 OpenJDK 25 JRE 并注册 systemd 服务
# 用法：sudo bash init-server.sh
set -euo pipefail

# ================================ 常量 ================================
# 应用部署目录，systemd unit 的 WorkingDirectory 指向此路径
APP_DIR="/opt/awesome-springboot-boilerplate"
# systemd 单元名
UNIT_NAME="awesome-springboot-boilerplate.service"
# 待安装的 JRE 包名
JRE_PACKAGE="openjdk-25-jre-headless"

# 脚本所在目录，用于定位同目录的 unit 文件
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 本地 unit 文件路径
UNIT_SOURCE="${SCRIPT_DIR}/${UNIT_NAME}"
# 系统单元安装目标路径
UNIT_TARGET="/etc/systemd/system/${UNIT_NAME}"

# ================================ 前置检查 ================================
# 必须以 root 身份运行
if [[ "${EUID}" -ne 0 ]]; then
    echo "错误：请以 root 身份运行（sudo bash init-server.sh）" >&2
    exit 1
fi

# 仅支持 Ubuntu 系统
if [[ ! -f /etc/os-release ]]; then
    echo "错误：无法读取 /etc/os-release，仅支持 Ubuntu" >&2
    exit 1
fi
# shellcheck disable=SC1091
source /etc/os-release
if [[ "${ID:-}" != "ubuntu" ]]; then
    echo "错误：当前系统为 ${ID:-未知}，仅支持 Ubuntu" >&2
    exit 1
fi

# unit 文件必须与脚本同目录存在
if [[ ! -f "${UNIT_SOURCE}" ]]; then
    echo "错误：未找到单元文件 ${UNIT_SOURCE}" >&2
    exit 1
fi

# ================================ 安装 JRE ================================
echo "==> 安装 ${JRE_PACKAGE} ..."
apt update
apt install -y "${JRE_PACKAGE}"

# 清除命令哈希，确保 java 命令可被即时调用
hash -r

# 验证 Java 可用
if ! command -v java >/dev/null 2>&1; then
    echo "错误：Java 安装后仍无法调用 java 命令" >&2
    exit 1
fi
echo "==> Java 版本："
java -version

# ================================ 准备部署目录 ================================
echo "==> 创建应用部署目录 ${APP_DIR} ..."
mkdir -p "${APP_DIR}"

# ================================ 安装 systemd 单元 ================================
echo "==> 安装 systemd 单元到 ${UNIT_TARGET} ..."
cp "${UNIT_SOURCE}" "${UNIT_TARGET}"
chmod 644 "${UNIT_TARGET}"

# 重新加载 systemd 配置
systemctl daemon-reload

# 注册开机自启（不启动服务，jar 尚未部署）
systemctl enable "${UNIT_NAME}"

# ================================ 完成 ================================
echo ""
echo "初始化完成。"
echo ""
echo "后续部署步骤："
echo "  1. 上传 jar 到 ${APP_DIR}/app.jar"
echo "  2. 按需创建 /etc/default/awesome-springboot-boilerplate 配置 JAVA_OPTS 等环境变量"
echo "  3. 启动服务：systemctl start ${UNIT_NAME}"
echo "  4. 查看状态：systemctl status ${UNIT_NAME}"
