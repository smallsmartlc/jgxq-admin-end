package com.jgxq.admin.controller;


import com.jgxq.admin.entity.Role;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.res.RoleBasicRes;
import com.jgxq.common.res.RoleRes;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
public class RoleController {

    @Autowired
    private RoleService roleService;

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

}
