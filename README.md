# API Mock 平台 (Mock Platform)

轻量级单体全栈 API Mock 平台，基于 Java 21 与 Spring Boot 3 构建。用于在前后端协同开发时，根据约定的接口契约快速提供真实的 HTTP 响应。

---

## 一、核心特性

- **项目隔离**：支持多项目管理，请求路径规范为 `http://host:port/{projectCode}/{path}`。
- **自定义响应**：支持针对任意 Method（GET、POST、PUT、DELETE、PATCH、ANY）自定义写入 Response Body（默认 JSON）。
- **丰富配置**：支持配置 HTTP 状态码（如 200、201、400、500 等）、网络延迟模拟（delayMs）、一键启用/停用。
- **开箱即用与跨域友好**：
  - 自动注入 CORS 响应头（支持任何前端客户端直接联调，支持 OPTIONS 预检请求）。
  - 内置 H2 文件数据库（数据自动保存在本地 `./data` 目录），无需安装外部数据库。
  - 前端管理界面内嵌在单一 JAR 中，零额外前端构建依赖，离线完全可用。
- **在线测试**：管理界面内置在线调用测试面板与一键复制调用链接。

---

## 二、技术栈

- **JDK**：Java 21
- **后端框架**：Spring Boot 3.3.4
- **ORM**：MyBatis-Plus 3.5.7
- **持久化**：H2 Database（MySQL 兼容模式，文件持久化）
- **规范**：遵循阿里巴巴 Java 开发规约、SLF4J 日志、强类型异常处理
- **前端**：自包含现代响应式单页 + 本地内置 Vue 3

---

## 三、快速开始

### 1. 编译与打包
```bash
mvn clean package
```

### 2. 启动服务
```bash
java -jar target/mock-platform-1.0.0.jar
```
启动后终端输出：
```
API Mock 平台启动成功! 访问地址: http://localhost:8080
```

### 3. 打开管理界面
在浏览器中访问：`http://localhost:8080/`

---

## 四、接口调用规范

### 1. Mock 调用入口
所有 Mock 接口通过以下格式调用：
```http
METHOD /{projectCode}/{path}
```
例如：
- 演示项目接口：`GET http://localhost:8080/demo/api/v1/user/info`
- 自定义接口：`POST http://localhost:8080/demo/user-defined-api-path`

### 2. 未匹配响应（404）
当请求的路径或方法未在平台中定义或被停用时，平台返回 404 状态码及 JSON 说明：
```json
{
  "code": 404,
  "error": "Mock Not Found",
  "message": "未找到匹配的 Mock 接口: [POST] /user-defined-api-path (项目: demo)",
  "projectCode": "demo",
  "method": "POST",
  "path": "/user-defined-api-path"
}
```
