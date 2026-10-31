package com.mockplatform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockplatform.dto.MockApiCreateDTO;
import com.mockplatform.dto.ProjectCreateDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 核心功能全链路集成测试
 *
 * @author mockplatform
 */
@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class MockPlatformIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("测试前端页面及静态资源访问")
    void testFrontendStaticResources() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("API Mock Platform")));

        mockMvc.perform(get("/vue.global.prod.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("vue v3.")));
    }

    @Test
    @DisplayName("测试初始化 demo 项目及预设 Mock 接口调用")
    void testPresetDemoMock() throws Exception {
        // 调用预设的 GET /demo/api/v1/user/info
        mockMvc.perform(get("/demo/api/v1/user/info"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("developer"));
    }

    @Test
    @DisplayName("测试创建新项目并在其下创建和调用自定义 Mock 接口")
    void testCreateProjectAndMockApiFlow() throws Exception {
        var projectCode = "order-test";

        // 1. 创建项目
        var projectDto = new ProjectCreateDTO();
        projectDto.setName("订单系统测试");
        projectDto.setProjectCode(projectCode);
        projectDto.setDescription("单元测试专用项目");

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.projectCode").value(projectCode));

        // 2. 创建 Mock 接口
        var mockDto = new MockApiCreateDTO();
        mockDto.setProjectCode(projectCode);
        mockDto.setApiPath("/orders/create");
        mockDto.setMethod("POST");
        mockDto.setStatusCode(201);
        mockDto.setResponseBody("{\"orderId\": 999888, \"status\": \"CREATED\"}");
        mockDto.setEnabled(1);
        mockDto.setDescription("创建订单接口");

        mockMvc.perform(post("/api/mock-apis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.apiPath").value("/orders/create"));

        // 3. 真实调用 Mock 接口: POST /order-test/orders/create
        mockMvc.perform(post("/order-test/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\": 1, \"count\": 2}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(jsonPath("$.orderId").value(999888))
                .andExpect(jsonPath("$.status").value("CREATED"));

        // 4. OPTIONS 跨域预检调用
        mockMvc.perform(options("/order-test/orders/create"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));

        // 5. 访问未定义的路径返回 404
        mockMvc.perform(get("/order-test/non-existent-path"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("Mock Not Found"));
    }
}
