package com.jgxq.common.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author LuCong
 * @since 2020-12-07
 **/
@Data
public class AdminUpdateReq {

    @NotBlank(message = "昵称不能为空哦")
    private String nickName;

    @NotBlank
    private String adminName;

    private Set<String> permissions;

}