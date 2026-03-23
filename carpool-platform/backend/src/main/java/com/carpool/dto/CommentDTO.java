package com.carpool.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

@Data
public class CommentDTO {
    
    @NotBlank(message = "建议内容不能为空")
    @Size(min = 5, max = 500, message = "建议内容长度5-500位")
    private String content;
    
    @Min(value = 1, message = "评分最低1星")
    @Max(value = 5, message = "评分最高5星")
    private Integer rating;
}