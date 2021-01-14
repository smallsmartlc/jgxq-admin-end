package com.jgxq.common.req;

import com.jgxq.admin.entity.Permission;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

/**
 * @author LuCong
 * @since 2021-01-14
 **/
@Data
public class RoleReq {

    @NotBlank(message = "用户名不能为空")
    private String name;

    private Set<String> permissions;
}
