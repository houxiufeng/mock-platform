package com.mockplatform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockplatform.entity.MockApi;
import com.mockplatform.service.MockDispatcherService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 动态 Mock 请求调度控制器
 * 处理 /{projectCode}/** 形式的所有外部 Mock 调用
 *
 * @author mockplatform
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class DynamicMockDispatcherController {

    private final MockDispatcherService mockDispatcherService;
    private final ObjectMapper objectMapper;

    /**
     * 系统保留字，禁止作为 projectCode 进行 Mock 匹配
     */
    private static final Set<String> IGNORED_PREFIXES = Set.of(
            "api", "static", "assets", "public", "favicon.ico", "index.html", "error", "actuator"
    );

    @RequestMapping(
            value = "/{projectCode}/**",
            method = {
                    RequestMethod.GET,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.DELETE,
                    RequestMethod.PATCH,
                    RequestMethod.HEAD,
                    RequestMethod.OPTIONS
            }
    )
    public ResponseEntity<String> dispatchMock(
            @PathVariable("projectCode") String projectCode,
            HttpServletRequest request) {

        // 排除系统保留前缀
        if (IGNORED_PREFIXES.contains(projectCode.toLowerCase())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        var method = request.getMethod().toUpperCase();
        var requestUri = request.getRequestURI();

        // 截取 projectCode 之后的实际 apiPath
        var prefix = "/" + projectCode;
        var apiPath = requestUri.startsWith(prefix) ? requestUri.substring(prefix.length()) : requestUri;
        if (!StringUtils.hasText(apiPath)) {
            apiPath = "/";
        }

        // 处理 CORS 预检请求（OPTIONS）
        if ("OPTIONS".equalsIgnoreCase(method)) {
            var corsHeaders = buildCorsHeaders();
            return ResponseEntity.ok().headers(corsHeaders).build();
        }

        log.info("收到 Mock 请求: project={}, method={}, path={}", projectCode, method, apiPath);

        // 匹配规则
        var mockApiOpt = mockDispatcherService.matchMockApi(projectCode, method, apiPath);
        if (mockApiOpt.isEmpty()) {
            return buildNotFoundResponse(projectCode, method, apiPath);
        }

        var mockApi = mockApiOpt.get();

        // 模拟响应延迟
        if (mockApi.getDelayMs() != null && mockApi.getDelayMs() > 0) {
            try {
                Thread.sleep(mockApi.getDelayMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Mock 延迟等待被中断: id={}, path={}", mockApi.getId(), mockApi.getApiPath());
            }
        }

        // 组装响应 Headers
        var headers = buildCorsHeaders();
        var contentTypeStr = StringUtils.hasText(mockApi.getContentType())
                ? mockApi.getContentType()
                : "application/json;charset=UTF-8";
        headers.set(HttpHeaders.CONTENT_TYPE, contentTypeStr);

        var statusCode = mockApi.getStatusCode() != null ? mockApi.getStatusCode() : 200;
        var body = mockApi.getResponseBody() != null ? mockApi.getResponseBody() : "";

        return ResponseEntity.status(statusCode).headers(headers).body(body);
    }

    private HttpHeaders buildCorsHeaders() {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS");
        headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
        return headers;
    }

    private ResponseEntity<String> buildNotFoundResponse(String projectCode, String method, String apiPath) {
        var errorBody = new LinkedHashMap<String, Object>();
        errorBody.put("code", 404);
        errorBody.put("error", "Mock Not Found");
        errorBody.put("message", "未找到匹配的 Mock 接口: [" + method + "] " + apiPath + " (项目: " + projectCode + ")");
        errorBody.put("projectCode", projectCode);
        errorBody.put("method", method);
        errorBody.put("path", apiPath);

        String jsonStr;
        try {
            jsonStr = objectMapper.writeValueAsString(errorBody);
        } catch (JsonProcessingException e) {
            log.error("格式化 404 响应 JSON 失败", e);
            jsonStr = "{\"code\":404,\"message\":\"Mock Not Found\"}";
        }

        var headers = buildCorsHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).headers(headers).body(jsonStr);
    }
}
