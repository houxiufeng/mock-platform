package com.mockplatform.controller;

import com.mockplatform.common.Result;
import com.mockplatform.dto.MockApiCreateDTO;
import com.mockplatform.dto.MockApiQueryDTO;
import com.mockplatform.dto.MockApiUpdateDTO;
import com.mockplatform.entity.MockApi;
import com.mockplatform.service.MockApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Mock 接口配置控制器
 *
 * @author mockplatform
 */
@RestController
@RequestMapping("/api/mock-apis")
@RequiredArgsConstructor
public class MockApiController {

    private final MockApiService mockApiService;

    @GetMapping
    public Result<List<MockApi>> listApis(MockApiQueryDTO query) {
        return Result.success(mockApiService.listApis(query));
    }

    @GetMapping("/{id}")
    public Result<MockApi> getApi(@PathVariable("id") Long id) {
        return Result.success(mockApiService.getById(id));
    }

    @PostMapping
    public Result<MockApi> createApi(@Valid @RequestBody MockApiCreateDTO dto) {
        return Result.success(mockApiService.createApi(dto));
    }

    @PutMapping
    public Result<Boolean> updateApi(@Valid @RequestBody MockApiUpdateDTO dto) {
        return Result.success(mockApiService.updateApi(dto));
    }

    @PutMapping("/{id}/toggle")
    public Result<Boolean> toggleStatus(@PathVariable("id") Long id, @RequestParam("enabled") Integer enabled) {
        return Result.success(mockApiService.toggleStatus(id, enabled));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteApi(@PathVariable("id") Long id) {
        return Result.success(mockApiService.deleteApi(id));
    }
}
