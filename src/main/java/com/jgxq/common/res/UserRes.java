package com.jgxq.common.res;

import lombok.Data;

import java.util.Date;

/**
 * @author LuCong
 * @since 2021-01-14
 **/
@Data
public class UserRes {

    private String userkey;
    /**
     * 昵称
     */
    private String nickName;

    private String email;

    /**
     * 城市
     */
    private String city;

    /**
     * 默认0,作者为1
     */
    private Boolean author;

    /**
     * 主队id
     */
    private String homeTeam;

    private Date createAt;
}
