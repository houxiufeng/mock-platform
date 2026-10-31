package com.mockplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新项目请求参数
 *
 * @author mockplatform
 */
@Data
public class ProjectUpdateDTO {

    @NotNull(message = "项目ID不能为空")
    private Long id;

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 64, message = "项目名称长度不能超过64字符")
    private String name;

    @Size(max = 255, message = "项目描述长度不能超过255字符")
    private String description;
}
