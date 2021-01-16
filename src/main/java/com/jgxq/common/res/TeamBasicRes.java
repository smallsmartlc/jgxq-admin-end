package com.jgxq.common.res;

import lombok.Data;

import java.util.Date;

/**
 * @author LuCong
 * @since 2020-12-09
 **/
@Data
public class TeamBasicRes {
    private Integer id;
    private String name;
    private String logo;
    private Date createAt;
}
