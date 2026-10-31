package com.mockplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mockplatform.common.BusinessException;
import com.mockplatform.common.ErrorCode;
import com.mockplatform.dto.ProjectCreateDTO;
import com.mockplatform.dto.ProjectUpdateDTO;
import com.mockplatform.entity.MockApi;
import com.mockplatform.entity.Project;
import com.mockplatform.mapper.MockApiMapper;
import com.mockplatform.mapper.ProjectMapper;
import com.mockplatform.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 项目业务实现类
 *
 * @author mockplatform
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    private final MockApiMapper mockApiMapper;

    /**
     * 系统保留项目编码，防止与管理端及静态资源路由冲突
     */
    private static final Set<String> RESERVED_PROJECT_CODES = Set.of(
            "api", "static", "assets", "public", "favicon.ico", "index.html", "error", "actuator"
    );

    @Override
    public List<Project> listProjects() {
        var queryWrapper = new LambdaQueryWrapper<Project>()
                .orderByDesc(Project::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public Project getByProjectCode(String projectCode) {
        var queryWrapper = new LambdaQueryWrapper<Project>()
                .eq(Project::getProjectCode, projectCode);
        return getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Project createProject(ProjectCreateDTO dto) {
        var projectCode = dto.getProjectCode().trim().toLowerCase();

        if (RESERVED_PROJECT_CODES.contains(projectCode)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST.getCode(), "项目编码 [" + projectCode + "] 为系统保留前缀，请使用其他名称");
        }

        var exists = count(new LambdaQueryWrapper<Project>().eq(Project::getProjectCode, projectCode)) > 0;
        if (exists) {
            throw new BusinessException(ErrorCode.CONFLICT.getCode(), "项目编码 [" + projectCode + "] 已存在");
        }

        var project = new Project();
        project.setName(dto.getName().trim());
        project.setProjectCode(projectCode);
        project.setDescription(dto.getDescription());
        project.setCreateTime(LocalDateTime.now());
        project.setUpdateTime(LocalDateTime.now());

        save(project);
        log.info("成功创建项目: id={}, projectCode={}, name={}", project.getId(), projectCode, project.getName());
        return project;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProject(ProjectUpdateDTO dto) {
        var project = Optional.ofNullable(getById(dto.getId()))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "项目不存在或已被删除"));

        project.setName(dto.getName().trim());
        project.setDescription(dto.getDescription());
        project.setUpdateTime(LocalDateTime.now());

        var updated = updateById(project);
        log.info("更新项目成功: id={}, name={}", project.getId(), project.getName());
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProject(Long id) {
        var project = Optional.ofNullable(getById(id))
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND.getCode(), "项目不存在或已被删除"));

        // 删除项目下的所有 Mock 接口
        mockApiMapper.delete(new LambdaQueryWrapper<MockApi>().eq(MockApi::getProjectCode, project.getProjectCode()));
        var removed = removeById(id);
        log.info("删除项目及其接口: id={}, projectCode={}", id, project.getProjectCode());
        return removed;
    }
}
