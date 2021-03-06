package com.jgxq.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.entity.Role;
import com.jgxq.admin.service.AdminService;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.define.ForumErrorCode;
import com.jgxq.common.req.AdminRegReq;
import com.jgxq.common.req.AdminUpdateReq;
import com.jgxq.common.req.ResetPasswordReq;
import com.jgxq.common.res.AdminBasicRes;
import com.jgxq.common.res.AdminRes;
import com.jgxq.common.res.RoleBasicRes;
import com.jgxq.common.res.RoleRes;
import com.jgxq.common.utils.LoginUtils;
import com.jgxq.common.utils.PasswordHash;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.CommonErrorCode;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-11
 */
@RestController
@RequestMapping("/admin")
@UserPermissionConf
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @GetMapping("info")
    public ResponseMessage getAdminInfo(@RequestAttribute("userKey") String userKey){
        return getAdmin(userKey);
    }


    @RolePermissionConf("0201")
    @GetMapping("page/{cur}/{size}")
    public ResponseMessage pageAdmin(@PathVariable Integer cur,
                                     @PathVariable Integer size){
        Page<Admin> page = new Page<>(cur,size);
        adminService.page(page);
        List<Admin> adminList = page.getRecords();
        Set<Integer> roleIds = adminList.stream().map(Admin::getRoleId).collect(Collectors.toSet());
        Map<Integer, String> roleMap = roleService.listByIds(roleIds).stream().collect(Collectors.toMap(Role::getId, Role::getName));
        List<AdminBasicRes> resList = adminList.stream().map(a -> {
            AdminBasicRes adminRes = new AdminBasicRes();
            BeanUtils.copyProperties(a, adminRes);
            String roleName = roleMap.get(a.getRoleId());
            if(roleName==null){
                roleName = "已删除角色";
            }
            RoleBasicRes role = new RoleBasicRes(a.getRoleId(), roleName);
            adminRes.setRole(role);
            return adminRes;
        }).collect(Collectors.toList());
        Page<AdminBasicRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(resList);
        return new ResponseMessage(resPage);
    }

    @RolePermissionConf("0200")
    @GetMapping("{userKey}")
    public ResponseMessage getAdmin(@PathVariable("userKey") String userKey){
        Admin admin = adminService.getAdminByPK("admin_key",userKey);
        AdminRes adminRes = new AdminRes();
        BeanUtils.copyProperties(admin,adminRes);
        Role role = roleService.getById(admin.getRoleId());
        RoleRes roleRes = new RoleRes();
        BeanUtils.copyProperties(role,roleRes);
        adminRes.setRole(roleRes);
        return new ResponseMessage(adminRes);
    }

    @RolePermissionConf("0202")
    @PostMapping("")
    public ResponseMessage register(@RequestBody @Validated AdminRegReq userReq) {

        if (!LoginUtils.checkPassword(userReq.getPassword())) {
            // 判断密码规则是否合法，字母、数字、特殊字符最少2种组合（不能有中文和空格）
            return new ResponseMessage(CommonErrorCode.BAD_PARAMETERS.getErrorCode(), "密码必须含有字母,数字,特殊字符最少两种组合!");
        }

        int count = adminService.count(new QueryWrapper<Admin>().eq("admin_name", userReq.getAdminName()));
        if (count > 0) {
            return new ResponseMessage(ForumErrorCode.User_Exists.getErrorCode(), "用户已存在");
        }

        String userKey = adminService.addAdmin(userReq);

        if (userKey == null) {
            return new ResponseMessage(CommonErrorCode.UNKNOWN_ERROR.getErrorCode(), "注册失败");
        }
        return new ResponseMessage(userKey);
    }

    @RolePermissionConf("0205")
    @PutMapping("resetPwd")
    public ResponseMessage resetPassword(@RequestBody @Validated ResetPasswordReq resetReq){
        String password = resetReq.getPassword();
        String userKey = resetReq.getAdminKey();
        if (!LoginUtils.checkPassword(password)) {
            // 判断密码规则是否合法，字母、数字、特殊字符最少2种组合（不能有中文和空格）
            return new ResponseMessage(CommonErrorCode.BAD_PARAMETERS.getErrorCode(), "密码必须含有字母,数字,特殊字符最少两种组合!");
        }
        String newPwd = null;
        try {
            newPwd = PasswordHash.createHash(password);
        } catch (Exception e) {
            throw new SmartException("密码处理异常",e);
        }
        UpdateWrapper<Admin> adminUpdate = new UpdateWrapper<>();
        adminUpdate.eq("admin_key",userKey).set("password",newPwd);
        boolean flag = adminService.update(adminUpdate);
        return new ResponseMessage(flag);
    }

    @DeleteMapping("{userKey}")
    @RolePermissionConf("0203")
    public ResponseMessage deleteAdmin(@PathVariable String userKey){
        UpdateWrapper<Admin> adminUpdate = new UpdateWrapper<>();
        adminUpdate.eq("admin_key",userKey);
        boolean flag = adminService.remove(adminUpdate);
        return new ResponseMessage(flag);
    }

    @PutMapping("{adminKey}")
    @RolePermissionConf("0204")
    public ResponseMessage updateAdmin(@RequestBody @Validated AdminUpdateReq userReq,
                                       @PathVariable String adminKey){
        Admin admin = new Admin();
        BeanUtils.copyProperties(userReq,admin);
        UpdateWrapper<Admin> adminUpdate = new UpdateWrapper<>();
        adminUpdate.eq("admin_key",adminKey);
        boolean flag = adminService.update(admin,adminUpdate);
        return new ResponseMessage(flag);
    }
}
