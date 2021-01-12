package com.jgxq.core.enums;

public enum RoleType {
    T0000("0000","超级管理员"),
    T0101("0101","查看Dashboard"),
    T0200("0200","查看管理员"),
    T0201("0201","查看管理员列表"),
    T0202("0202","增加管理员"),
    T0203("0203","删除管理员"),
    T0204("0204","编辑管理员"),
    T0300("0300","查看用户"),
    T0301("0301","查看用户列表"),
    T0302("0302","编辑用户信息"),
    T0303("0303","删除用户"),
    ;

    private String val;
    private String name;

    RoleType(String val, String name) {
        this.val = val;
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public String getName() {
        return name;
    }
}
