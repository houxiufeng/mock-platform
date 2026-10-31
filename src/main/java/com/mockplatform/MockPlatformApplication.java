package com.mockplatform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Mock 平台启动主类
 *
 * @author mockplatform
 */
@Slf4j
@SpringBootApplication
public class MockPlatformApplication {

    public static void main(String[] args) {
        var context = SpringApplication.run(MockPlatformApplication.class, args);
        var environment = context.getEnvironment();
        var port = environment.getProperty("server.port", "8080");
        log.info("API Mock 平台启动成功! 访问地址: http://localhost:{}", port);
    }
}
