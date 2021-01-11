package com.jgxq.admin.controller.user;

import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.RoleType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LuCong
 * @since 2021-01-11
 **/
@RestController
@UserPermissionConf
public class Test {

    @RolePermissionConf("0101")
    @GetMapping("test")
    public String getSth(){
        return  "something";
    }
}
