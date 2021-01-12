package com.jgxq.common.res;

import lombok.Data;

/**
 * @author LuCong
 * @since 2021-01-11
 **/
@Data
public class AdminBasicRes {
    private String adminKey;
    /**
     * 昵称
     */
    private String nickName;

    private String adminName;

    private String roleName;

}
