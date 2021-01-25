package com.jgxq.admin.controller;

import com.jgxq.admin.entity.Permission;
import com.jgxq.core.enums.RoleType;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LuCong
 * @since 2021-01-14
 **/
@RestController
@RequestMapping("permission")
public class PermissionController {

    @GetMapping("list")
    public ResponseMessage listPermission(){
        RoleType[] values = RoleType.values();
        List<Permission> resList = new ArrayList<>();
        for (RoleType value : values) {
            if(value == RoleType.T0000){
                continue;
            }
            Permission permission = new Permission();
            permission.setCode(value.getVal());
            permission.setName(value.getName());
            resList.add(permission);
        }
        return new ResponseMessage(resList);
    }

}
