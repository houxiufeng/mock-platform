package com.mockplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mockplatform.dto.MockApiCreateDTO;
import com.mockplatform.dto.MockApiQueryDTO;
import com.mockplatform.dto.MockApiUpdateDTO;
import com.mockplatform.entity.MockApi;

import java.util.List;

/**
 * Mock 接口配置业务接口
 *
 * @author mockplatform
 */
public interface MockApiService extends IService<MockApi> {

    /**
     * 根据查询条件获取 Mock 接口列表
     *
     * @param query 查询条件
     * @return 接口列表
     */
    List<MockApi> listApis(MockApiQueryDTO query);

    /**
     * 创建 Mock 接口
     *
     * @param dto 创建参数
     * @return 创建后的实体
     */
    MockApi createApi(MockApiCreateDTO dto);

    /**
     * 更新 Mock 接口
     *
     * @param dto 更新参数
     * @return 是否成功
     */
    boolean updateApi(MockApiUpdateDTO dto);

    /**
     * 切换启停状态
     *
     * @param id 接口ID
     * @param enabled 目标状态：1 启用，0 停用
     * @return 是否成功
     */
    boolean toggleStatus(Long id, Integer enabled);

    /**
     * 删除 Mock 接口
     *
     * @param id 接口ID
     * @return 是否成功
     */
    boolean deleteApi(Long id);
}
