package com.jgxq.admin.entity;

import lombok.Data;

import java.util.Set;

/**
 * @author LuCong
 * @since 2021-01-11
 **/
@Data
public class AdminDto {

    private Set<String> permissions;
}
