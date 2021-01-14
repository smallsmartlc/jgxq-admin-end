package com.jgxq.common.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author LuCong
 * @since 2021-01-12
 **/
@Data
public class ResetPasswordReq {

    @NotBlank(message = "adminKey不能为空")
    String adminKey;

    @NotBlank(message = "密码不能为空")
    @Size(min=6, max=15, message="密码长度只能在6-15之间")
    String password;
}
