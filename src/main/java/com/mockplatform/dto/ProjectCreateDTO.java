package com.mockplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建项目请求参数
 *
 * @author mockplatform
 */
@Data
public class ProjectCreateDTO {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 64, message = "项目名称长度不能超过64字符")
    private String name;

    @NotBlank(message = "项目编码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{2,32}$", message = "项目编码只能包含2-32位字母、数字、下划线或连字符")
    private String projectCode;

    @Size(max = 255, message = "项目描述长度不能超过255字符")
    private String description;
}
