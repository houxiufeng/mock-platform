CREATE TABLE IF NOT EXISTS project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    project_code VARCHAR(64) NOT NULL,
    description VARCHAR(255),
    deleted TINYINT DEFAULT 0 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_project_code UNIQUE (project_code)
);

CREATE TABLE IF NOT EXISTS mock_api (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_code VARCHAR(64) NOT NULL,
    api_path VARCHAR(255) NOT NULL,
    method VARCHAR(16) NOT NULL,
    status_code INT DEFAULT 200 NOT NULL,
    content_type VARCHAR(64) DEFAULT 'application/json;charset=UTF-8' NOT NULL,
    response_body TEXT,
    delay_ms INT DEFAULT 0 NOT NULL,
    enabled TINYINT DEFAULT 1 NOT NULL,
    description VARCHAR(255),
    deleted TINYINT DEFAULT 0 NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_project_method_path UNIQUE (project_code, method, api_path)
);

-- 初始化示例项目（若不存在）
MERGE INTO project (id, name, project_code, description, deleted, create_time, update_time)
KEY(project_code)
VALUES (1, '演示示例项目', 'demo', '系统内置演示项目', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 初始化示例 Mock 接口（若不存在）
MERGE INTO mock_api (id, project_code, api_path, method, status_code, content_type, response_body, delay_ms, enabled, description, deleted, create_time, update_time)
KEY(project_code, method, api_path)
VALUES (
    1,
    'demo',
    '/api/v1/user/info',
    'GET',
    200,
    'application/json;charset=UTF-8',
    '{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 10001,
    "username": "developer",
    "nickname": "全栈开发者",
    "roles": ["admin", "developer"],
    "email": "dev@example.com"
  }
}',
    0,
    1,
    '获取当前用户信息',
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
