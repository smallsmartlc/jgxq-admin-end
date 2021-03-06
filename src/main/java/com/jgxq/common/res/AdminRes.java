package com.jgxq.common.res;

import com.jgxq.admin.entity.Role;
import lombok.Data;

/**
 * @author LuCong
 * @since 2021-01-11
 **/
@Data
public class AdminRes {
    private String adminKey;
    /**
     * 昵称
     */
    private String nickName;

    private String adminName;

    private RoleRes role;

}
