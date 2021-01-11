package com.jgxq.core.enums;

public enum RoleType {
    T0101("0101","查看Dashboard"),
    T0201("0201","查看团队列表");

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
