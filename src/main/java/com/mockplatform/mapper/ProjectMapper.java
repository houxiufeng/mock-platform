package com.mockplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mockplatform.entity.Project;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目数据访问接口
 *
 * @author mockplatform
 */
@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
