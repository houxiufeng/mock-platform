package com.mockplatform.service;

import com.mockplatform.entity.MockApi;

import java.util.Optional;

/**
 * Mock 核心路由调度业务接口
 *
 * @author mockplatform
 */
public interface MockDispatcherService {

    /**
     * 根据请求上下文匹配对应的 Mock 规则
     *
     * @param projectCode 项目编码
     * @param method HTTP 请求方法
     * @param apiPath 请求 API 路径
     * @return 匹配到的 MockApi 规则（若存在）
     */
    Optional<MockApi> matchMockApi(String projectCode, String method, String apiPath);
}
