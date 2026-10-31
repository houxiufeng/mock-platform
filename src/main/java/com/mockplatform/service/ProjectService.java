package com.mockplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mockplatform.dto.ProjectCreateDTO;
import com.mockplatform.dto.ProjectUpdateDTO;
import com.mockplatform.entity.Project;

import java.util.List;

/**
 * 项目业务接口
 *
 * @author mockplatform
 */
public interface ProjectService extends IService<Project> {

    /**
     * 查询所有项目列表
     *
     * @return 项目列表
     */
    List<Project> listProjects();

    /**
     * 根据 projectCode 获取项目详情
     *
     * @param projectCode 项目编码
     * @return 项目实体
     */
    Project getByProjectCode(String projectCode);

    /**
     * 创建项目
     *
     * @param dto 创建参数
     * @return 创建后的项目实体
     */
    Project createProject(ProjectCreateDTO dto);

    /**
     * 更新项目
     *
     * @param dto 更新参数
     * @return 是否成功
     */
    boolean updateProject(ProjectUpdateDTO dto);

    /**
     * 删除项目及关联的 Mock 接口
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean deleteProject(Long id);
}
