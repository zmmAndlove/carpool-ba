package com.carpool.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserDTO {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度3-20位")
    private String username;
    
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