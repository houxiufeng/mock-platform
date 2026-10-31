package com.mockplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mockplatform.entity.MockApi;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mock 接口配置数据访问接口
 *
 * @author mockplatform
 */
@Mapper
public interface MockApiMapper extends BaseMapper<MockApi> {
}
