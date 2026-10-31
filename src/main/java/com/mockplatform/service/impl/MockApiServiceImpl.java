package com.mockplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockplatform.common.BusinessException;
import com.mockplatform.common.ErrorCode;
import com.mockplatform.dto.MockApiCreateDTO;
import com.mockplatform.dto.MockApiQueryDTO;
import com.mockplatform.dto.MockApiUpdateDTO;
import com.mockplatform.entity.MockApi;
import com.mockplatform.mapper.MockApiMapper;
import com.mockplatform.service.MockApiService;
import com.mockplatform.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Mock 接口配置业务实现
 *
 * @author mockplatform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockApiServiceImpl extends ServiceImpl<MockApiMapper, MockApi> implements MockApiService {

    private final ProjectService projectService;

    @Override
    public List<MockApi> listApis(MockApiQueryDTO query) {
        if (!StringUtils.hasText(query.getProjectCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "projectCode 不能为空");
        }

        var wrapper = new LambdaQueryWrapper<MockApi>()
                .eq(MockApi::getProjectCode, query.getProjectCode().trim())
                .eq(StringUtils.hasText(query.getMethod()), MockApi::getMethod, Optional.ofNullable(query.getMethod()).map(String::toUpperCase).orElse(null))
                .eq(query.getEnabled() != null, MockApi::getEnabled, query.getEnabled())
                .and(StringUtils.hasText(query.getKeyword()), w -> w.like(MockApi::getApiPath, query.getKeyword().trim())
                        .or()
                        .like(MockApi::getDescription, query.getKeyword().trim()))
                .orderByDesc(MockApi::getCreateTime);

        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MockApi createApi(MockApiCreateDTO dto) {
        var projectCode = dto.getProjectCode().trim();
        var project = projectService.getByProjectCode(projectCode);
        if (project == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "所属项目 [" + projectCode + "] 不存在");
        }

        var normalizedPath = normalizePath(dto.getApiPath());
        var method = dto.getMethod().trim().toUpperCase();

        // 校验唯一性
        checkDuplicate(projectCode, method, normalizedPath, null);

        var mockApi = new MockApi();
        mockApi.setProjectCode(projectCode);
        mockApi.setApiPath(normalizedPath);
        mockApi.setMethod(method);
        mockApi.setStatusCode(Optional.ofNullable(dto.getStatusCode()).orElse(200));
        mockApi.setContentType(Optional.ofNullable(dto.getContentType()).filter(StringUtils::hasText).orElse("application/json;charset=UTF-8"));
        mockApi.setResponseBody(Optional.ofNullable(dto.getResponseBody()).orElse("{}"));
        mockApi.setDelayMs(Optional.ofNullable(dto.getDelayMs()).orElse(0));
        mockApi.setEnabled(Optional.ofNullable(dto.getEnabled()).orElse(1));
        mockApi.setDescription(dto.getDescription());
        mockApi.setCreateTime(LocalDateTime.now());
        mockApi.setUpdateTime(LocalDateTime.now());

        save(mockApi);
        log.info("创建 Mock 接口成功: id={}, project={}, method={}, path={}",
                mockApi.getId(), projectCode, method, normalizedPath);
        return mockApi;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateApi(MockApiUpdateDTO dto) {
        var existing = Optional.ofNullable(getById(dto.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "Mock 接口不存在或已被删除"));

        var normalizedPath = normalizePath(dto.getApiPath());
        var method = dto.getMethod().trim().toUpperCase();

        // 校验唯一性（排除自身）
        checkDuplicate(existing.getProjectCode(), method, normalizedPath, existing.getId());

        existing.setApiPath(normalizedPath);
        existing.setMethod(method);
        existing.setStatusCode(dto.getStatusCode());
        existing.setContentType(dto.getContentType());
        existing.setResponseBody(dto.getResponseBody());
        existing.setDelayMs(dto.getDelayMs());
        existing.setEnabled(dto.getEnabled());
        existing.setDescription(dto.getDescription());
        existing.setUpdateTime(LocalDateTime.now());

        var updated = updateById(existing);
        log.info("更新 Mock 接口成功: id={}, method={}, path={}", existing.getId(), method, normalizedPath);
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleStatus(Long id, Integer enabled) {
        var existing = Optional.ofNullable(getById(id))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "Mock 接口不存在或已被删除"));

        existing.setEnabled(enabled != null && enabled == 1 ? 1 : 0);
        existing.setUpdateTime(LocalDateTime.now());
        var updated = updateById(existing);
        log.info("更新 Mock 接口状态: id={}, enabled={}", id, existing.getEnabled());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApi(Long id) {
        var exists = getById(id) != null;
        if (!exists) {
            throw new BusinessException(ErrorCode.NOT_FOUND.getCode(), "Mock 接口不存在或已被删除");
        }
        var removed = removeById(id);
        log.info("删除 Mock 接口: id={}", id);
        return removed;
    }

    private void checkDuplicate(String projectCode, String method, String path, Long excludeId) {
        var count = count(new LambdaQueryWrapper<MockApi>()
                .eq(MockApi::getProjectCode, projectCode)
                .eq(MockApi::getMethod, method)
                .eq(MockApi::getApiPath, path)
                .ne(excludeId != null, MockApi::getId, excludeId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.CONFLICT.getCode(),
                    "该项目下已存在相同方法与路径的 Mock 接口: [" + method + "] " + path);
        }
    }

    private String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return "/";
        }
        var trimmed = path.trim();
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        // 去除末尾冗余的斜杠（非根路径）
        if (trimmed.length() > 1 && trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
