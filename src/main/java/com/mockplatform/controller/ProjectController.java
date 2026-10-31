package com.mockplatform.controller;

import com.mockplatform.common.Result;
import com.mockplatform.dto.ProjectCreateDTO;
import com.mockplatform.dto.ProjectUpdateDTO;
import com.mockplatform.entity.Project;
import com.mockplatform.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目管理控制器
 *
 * @author mockplatform
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public Result<List<Project>> listProjects() {
        return Result.success(projectService.listProjects());
    }

    @GetMapping("/{projectCode}")
    public Result<Project> getProject(@PathVariable("projectCode") String projectCode) {
        return Result.success(projectService.getByProjectCode(projectCode));
    }

    @PostMapping
    public Result<Project> createProject(@Valid @RequestBody ProjectCreateDTO dto) {
        return Result.success(projectService.createProject(dto));
    }

    @PutMapping
    public Result<Boolean> updateProject(@Valid @RequestBody ProjectUpdateDTO dto) {
        return Result.success(projectService.updateProject(dto));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProject(@PathVariable("id") Long id) {
        return Result.success(projectService.deleteProject(id));
    }
}
