package com.mockplatform.dto;

import lombok.Data;

/**
 * 查询 Mock 接口条件
 *
 * @author mockplatform
 */
@Data
public class MockApiQueryDTO {

    /**
     * 项目编码（必选）
     */
    private String projectCode;

    /**
     * 请求方法筛选
     */
    private String method;

    /**
     * 路径或描述模糊搜索关键词
     */
    private String keyword;

    /**
     * 状态筛选：1 启用，0 停用
     */
    private Integer enabled;
}
