package com.jgxq.common.res;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author LuCong
 * @since 2021-01-11
 **/
@Data
public class RoleRes {

    private Integer id;

    private String name;

    private List<String> permissions;

}
