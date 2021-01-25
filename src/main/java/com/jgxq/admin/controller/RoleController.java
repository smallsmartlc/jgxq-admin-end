package com.jgxq.admin.controller;


import com.alibaba.fastjson.JSON;
import com.jgxq.admin.entity.Permission;
import com.jgxq.admin.entity.Role;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.req.RoleReq;
import com.jgxq.common.res.RoleBasicRes;
import com.jgxq.common.res.RoleRes;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.RoleType;
import com.jgxq.core.resp.ResponseMessage;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
@RequestMapping("/role")
@UserPermissionConf
public class RoleController {

    @Autowired
    private RoleService roleService;

    @RolePermissionConf("0301")
    @GetMapping("list")
    public ResponseMessage listRole(){
        List<Role> roles = roleService.list();
        List<RoleBasicRes> resList = roles.stream().map(r -> {
            RoleBasicRes temp = new RoleBasicRes();
            BeanUtils.copyProperties(r, temp);
            temp.setId(r.getId());
            return temp;
        }).collect(Collectors.toList());
        return new ResponseMessage(resList);
    }

    @RolePermissionConf("0300")
    @GetMapping("{id}")
    public ResponseMessage getRole(@PathVariable Integer id){
        Role role = roleService.getById(id);
        List<String> permissonStrs = JSON.parseArray(role.getPermissions(), String.class);
//        List<Permission> permissions = permissonStrs.stream().map(s -> {
//            Permission permission = new Permission();
//            permission.setCode(s);
//            permission.setName(RoleType.getNameByCode(s));
//            return permission;
//        }).collect(Collectors.toList());
        RoleRes roleRes = new RoleRes();
        BeanUtils.copyProperties(role,roleRes);
        roleRes.setPermissions(permissonStrs);
        return new ResponseMessage(roleRes);
    }

    @RolePermissionConf("0302")
    @PostMapping("")
    public ResponseMessage addRole(@RequestBody @Validated RoleReq roleReq){

        Set<String> permissions = roleReq.getPermissions();
        permissions.remove(RoleType.T0000.getVal());
        String permissionStr = JSON.toJSONString(permissions);
        Role role = new Role();
        role.setPermissions(permissionStr);
        BeanUtils.copyProperties(roleReq,role);
        roleService.save(role);
        return new ResponseMessage(role.getId());
    }

    @RolePermissionConf("0304")
    @PutMapping("{id}")
    public ResponseMessage editRole(@RequestBody @Validated RoleReq roleReq,
                                    @PathVariable Integer id){
        Role role = new Role();
        BeanUtils.copyProperties(roleReq,role);
        Set<String> permissions = roleReq.getPermissions();
        permissions.remove(RoleType.T0000.getVal());
        String permissionStr = JSON.toJSONString(permissions);
        role.setPermissions(permissionStr);
        role.setId(id);
        boolean flag = roleService.updateById(role);
        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0303")
    @DeleteMapping("{id}")
    public ResponseMessage deleteRole(@PathVariable Integer id){
        boolean flag = roleService.removeById(id);
        return new ResponseMessage(flag);
    }

}
