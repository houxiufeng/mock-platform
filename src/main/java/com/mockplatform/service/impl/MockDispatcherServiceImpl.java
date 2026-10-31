package com.mockplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mockplatform.entity.MockApi;
import com.mockplatform.mapper.MockApiMapper;
import com.mockplatform.service.MockDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Mock 核心路由调度业务实现
 *
 * @author mockplatform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockDispatcherServiceImpl implements MockDispatcherService {

    private final MockApiMapper mockApiMapper;

    @Override
    public Optional<MockApi> matchMockApi(String projectCode, String method, String apiPath) {
        var normalizedPath = normalizePath(apiPath);
        var upperMethod = method.toUpperCase();

        // 1. 精确匹配 projectCode + method + apiPath + enabled=1
        var exactApi = mockApiMapper.selectOne(new LambdaQueryWrapper<MockApi>()
                .eq(MockApi::getProjectCode, projectCode)
                .eq(MockApi::getMethod, upperMethod)
                .eq(MockApi::getApiPath, normalizedPath)
                .eq(MockApi::getEnabled, 1));

        if (exactApi != null) {
            log.debug("精准命中 Mock 规则: project={}, method={}, path={}, id={}",
                    projectCode, upperMethod, normalizedPath, exactApi.getId());
            return Optional.of(exactApi);
        }

        // 2. 泛匹配 projectCode + ANY + apiPath + enabled=1
        var anyApi = mockApiMapper.selectOne(new LambdaQueryWrapper<MockApi>()
                .eq(MockApi::getProjectCode, projectCode)
                .eq(MockApi::getMethod, "ANY")
                .eq(MockApi::getApiPath, normalizedPath)
                .eq(MockApi::getEnabled, 1));

        if (anyApi != null) {
            log.debug("ANY方法命中 Mock 规则: project={}, method={}, path={}, id={}",
                    projectCode, upperMethod, normalizedPath, anyApi.getId());
            return Optional.of(anyApi);
        }

        log.warn("未命中任何 Mock 规则: project={}, method={}, path={}",
                projectCode, upperMethod, normalizedPath);
        return Optional.empty();
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        var trimmed = path.trim();
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
