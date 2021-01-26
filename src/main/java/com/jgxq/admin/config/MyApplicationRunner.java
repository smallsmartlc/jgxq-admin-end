package com.jgxq.admin.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.entity.Role;
import com.jgxq.admin.service.AdminService;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.req.AdminRegReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lucong
 * @date 2019/11/27 11:26
 * @description SpringBoot启动时，加载权限到数据库
 */
@Component
// 通过设置这里的数字来知道指定顺序
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminService adminService;

    /**
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run (ApplicationArguments args) throws Exception {

        // 初始化“管理员”角色
        QueryWrapper<Role> roleQuery = new QueryWrapper<Role>();
        roleQuery.eq("`name`", "超级管理员");
        Role role = roleService.getOne(roleQuery);
        Integer roleId = 0;
        if (role == null) {
            Role admin = new Role();
            admin.setName("超级管理员");
            admin.setPermissions("[\"0000\"]");
            roleService.save(admin);
            roleId = admin.getId();
        } else {
            roleId = role.getId();
        }

        // 初始化用户“管理员”
        QueryWrapper<Admin> adminQuery = new QueryWrapper<>();
        adminQuery.eq("admin_name", "admin");
        Admin adminUser = adminService.getOne(adminQuery);
        if (adminUser == null) {
            AdminRegReq admin = new AdminRegReq();
            admin.setNickName("超级管理员");
            admin.setAdminName("admin");
            admin.setPassword("abc123");
            admin.setRoleId(roleId);
            adminService.addAdmin(admin);
        }
    }
}


