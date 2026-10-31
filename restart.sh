#!/bin/bash
# 重启 mock-platform 服务
# 用法: ./restart.sh [profile] [port]
# 示例:
#   ./restart.sh                  # 默认 local profile，自动获取该 profile 配置的端口 (默认 8080)
#   ./restart.sh prod             # 使用 prod profile，自动获取该 profile 配置的端口
#   ./restart.sh prod 8080        # 使用 prod profile 并显式指定 8080 端口
#   ./restart.sh local 8081       # 使用 local profile 并显式指定 8081 端口

set -u

PROFILE="${1:-local}"
APP_DIR="$(cd "$(dirname "$0")" && pwd)"

# 解析端口：优先使用入参，未指定时尝试读取配置文件，读取失败则按 profile 回退默认值
get_default_port() {
  local port=""
  if [ -f "src/main/resources/application-${PROFILE}.yml" ]; then
    port=$(grep -E '^[[:space:]]*port:[[:space:]]*[0-9]+' "src/main/resources/application-${PROFILE}.yml" | head -n 1 | awk '{print $2}')
  fi
  if [ -z "$port" ] && [ -f "src/main/resources/application.yml" ]; then
    port=$(grep -E '^[[:space:]]*port:[[:space:]]*[0-9]+' "src/main/resources/application.yml" | head -n 1 | awk '{print $2}')
  fi
  if [ -z "$port" ]; then
    port="8080"
  fi
  echo "$port"
}

PORT="${2:-$(get_default_port)}"
LOG_FILE="$APP_DIR/app.log"

echo "[restart] 当前工作目录: $APP_DIR"
echo "[restart] 当前目标 Profile: $PROFILE, 目标端口: $PORT"

cd "$APP_DIR" || exit 1

# 1. 精准查找当前实例的进程：
# - 优先查找监听指定端口的 LISTEN 进程
# - 结合限定当前目录路径 ($APP_DIR) 的 maven/java 进程，避免误伤其他目录实例
find_pids() {
  local pids=""
  if [ -n "$PORT" ]; then
    local port_pids
    port_pids=$(lsof -ti tcp:"$PORT" -sTCP:LISTEN 2>/dev/null || true)
    if [ -n "$port_pids" ]; then
      pids="$pids $port_pids"
    fi
  fi

  local dir_pids
  dir_pids=$(pgrep -f "$APP_DIR.*(spring-boot:run|MockPlatformApplication|mock-platform)" 2>/dev/null || true)
  if [ -n "$dir_pids" ]; then
    pids="$pids $dir_pids"
  fi

  echo "$pids" | tr " " "\n" | grep -v '^$' | sort -u
}

echo "[restart] 停止旧进程 (端口: $PORT)..."
PIDS=$(find_pids | tr "\n" " ")
if [ -n "$PIDS" ]; then
  echo "[restart] 命中进程: $PIDS"
  kill $PIDS 2>/dev/null || true
  # 等待最多 30s 优雅退出
  for _ in $(seq 1 30); do
    sleep 1
    [ -z "$(find_pids)" ] && break
  done
  # 仍存活则强杀
  REMAIN=$(find_pids | tr "\n" " ")
  if [ -n "$REMAIN" ]; then
    echo "[restart] 仍有残留进程，强杀: $REMAIN"
    kill -9 $REMAIN 2>/dev/null || true
  fi
else
  echo "[restart] 无旧进程"
fi

# 2. 启动
echo "[restart] 启动新进程 (profile=$PROFILE, port=$PORT)..."
nohup bash -c "mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE -Dspring-boot.run.arguments=\"--server.port=$PORT\"" > "$LOG_FILE" 2>&1 &
NEW_PID=$!
echo "[restart] 已启动，shell PID=$NEW_PID, 日志: $LOG_FILE"
echo "[restart] 实时日志: tail -f $LOG_FILE"
