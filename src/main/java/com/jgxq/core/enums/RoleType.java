package com.jgxq.core.enums;


public enum RoleType {
    T0000("0000","超级管理员"),
    T0101("0101","查看Dashboard"),
    T0200("0200","查看管理员"),
    T0201("0201","查看管理员列表"),
    T0202("0202","增加管理员"),
    T0203("0203","删除管理员"),
    T0204("0204","编辑管理员"),
    T0205("0205","重置管理员密码"),
    T0300("0300","查看角色"),
    T0301("0301","查看角色列表"),
    T0302("0302","添加角色"),
    T0303("0303","删除角色"),
    T0304("0304","编辑角色"),
    T0401("0401","查看用户列表"),
    T0402("0402","编辑用户信息"),
    T0403("0403","重置用户密码"),
    T0500("0500","查看球队"),
    T0501("0501","查看球队列表"),
    T0502("0502","添加球队"),
    T0503("0503","删除球队"),
    T0504("0504","编辑球队信息"),
    T0505("0505","查看球队队员"),
//    T0600("0600","查看球员"),
    T0601("0601","查看球员列表"),
    T0602("0602","添加球员"),
    T0603("0603","删除球员"),
    T0604("0604","编辑球员信息"),
    T0605("0605","球员转会"),
    T0606("0606","球员退役"),
    T0607("0607","批量插入球员"),
    T0700("0700","查看比赛"),
    T0701("0701","查看比赛列表"),
    T0702("0702","添加比赛"),
    T0703("0703","删除比赛"),
    T0704("0704","编辑比赛信息"),
    T0800("0800","查看新闻"),
    T0801("0801","查看新闻列表"),
    T0803("0803","删除新闻"),
    T0804("0804","编辑新闻信息"),
    T0805("0805","查看置顶文章"),
    T0806("0806","将文章置顶"),
    T0807("0807","取消文章置顶"),
    T0900("0900","查看闲聊"),
    T0901("0901","查看闲聊列表"),
    T0902("0902","添加闲聊"),
    T0903("0903","删除闲聊"),
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

    public static String getNameByCode(String code) {
        RoleType[] values = values();

        for (RoleType value : values) {
            if(value.val.equals(code)) {
                return value.getName();
            }
        }
        return "未知权限";
    }

}
