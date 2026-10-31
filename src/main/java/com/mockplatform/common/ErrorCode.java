package com.mockplatform.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误码枚举
 *
 * @author mockplatform
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(0, "操作成功"),

    /**
     * 参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),

    /**
     * 资源未找到
     */
    NOT_FOUND(404, "请求资源不存在"),

    /**
     * 冲突
     */
    CONFLICT(409, "数据冲突或已存在"),

    /**
     * 系统内部错误
     */
    INTERNAL_SERVER_ERROR(500, "系统内部异常");

    private final int code;
    private final String message;
}
