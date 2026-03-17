package com.carpool.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class UserDTO {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少6位")
    private String password;
    
    @Size(min = 2, max = 20, message = "姓名长度2-20位")
    private String realName;
    
    private String avatar;
    private String gender;
    private Integer age;
    private String idCard;
    private String hometownProvince;
    private String hometownCity;
    private String currentProvince;
    private String currentCity;
    private String university;
    private String interests;
}