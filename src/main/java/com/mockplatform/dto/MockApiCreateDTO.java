package com.mockplatform.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建 Mock 接口请求参数
 *
 * @author mockplatform
 */
@Data
public class MockApiCreateDTO {

    @NotBlank(message = "所属项目编码不能为空")
    private String projectCode;

    @NotBlank(message = "API路径不能为空")
    @Pattern(regexp = "^/.*$", message = "API路径必须以'/'开头")
    @Size(max = 255, message = "API路径长度不能超过255字符")
    private String apiPath;

    @NotBlank(message = "请求方法不能为空")
    @Pattern(regexp = "^(?i)(GET|POST|PUT|DELETE|PATCH|ANY)$", message = "请求方法仅支持 GET/POST/PUT/DELETE/PATCH/ANY")
    private String method;

    @NotNull(message = "HTTP状态码不能为空")
    @Min(value = 100, message = "HTTP状态码不能小于100")
    @Max(value = 599, message = "HTTP状态码不能大于599")
    private Integer statusCode = 200;

    @NotBlank(message = "Content-Type不能为空")
    private String contentType = "application/json;charset=UTF-8";

    private String responseBody = "{\n  \"code\": 0,\n  \"message\": \"success\",\n  \"data\": {}\n}";

    @Min(value = 0, message = "延迟毫秒数不能小于0")
    @Max(value = 60000, message = "延迟毫秒数不能大于60000")
    private Integer delayMs = 0;

    @NotNull(message = "启用状态不能为空")
    private Integer enabled = 1;

    @Size(max = 255, message = "描述长度不能超过255字符")
    private String description;
}
