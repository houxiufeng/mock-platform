package com.mockplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Mock 接口配置实体
 *
 * @author mockplatform
 */
@Data
@TableName("mock_api")
public class MockApi implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目标识
     */
    private String projectCode;

    /**
     * API 路径（如 /api/v1/user/info）
     */
    private String apiPath;

    /**
     * HTTP 请求方法：GET, POST, PUT, DELETE, PATCH, ANY
     */
    private String method;

    /**
     * HTTP 响应状态码，默认 200
     */
    private Integer statusCode;

    /**
     * 响应 Content-Type，默认 application/json;charset=UTF-8
     */
    private String contentType;

    /**
     * 响应体内容（自定义文本，默认为 JSON）
     */
    private String responseBody;

    /**
     * 响应延迟时间（毫秒），默认 0
     */
    private Integer delayMs;

    /**
     * 是否启用：1 启用，0 停用
     */
    private Integer enabled;

    /**
     * 接口描述/说明
     */
    private String description;

    /**
     * 逻辑删除标识：0 未删除，1 已删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
