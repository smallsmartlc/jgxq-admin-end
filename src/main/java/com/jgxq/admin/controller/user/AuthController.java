package com.jgxq.admin.controller.user;


import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.service.AdminService;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.define.ForumErrorCode;
import com.jgxq.common.res.AdminLoginRes;
import com.jgxq.common.res.AdminBasicRes;
import com.jgxq.common.req.AdminLoginReq;
import com.jgxq.common.utils.CookieUtils;
import com.jgxq.common.utils.JwtUtil;
import com.jgxq.common.utils.LoginUtils;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.UserPermissionType;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-06
 */
@RestController
@RequestMapping("/check")
@Validated
@UserPermissionConf(Type = UserPermissionType.ALLOW)
public class AuthController {

    @Value("${JWTParam.cookieSecure}")
    private boolean cookieSecure;

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseMessage checkUser(@RequestAttribute(value = "userKey", required = false) String userKey) {
        if (userKey == null) {
            return new ResponseMessage(null);
        }
        Admin user = adminService.getAdminByPK("userkey", userKey);
        AdminBasicRes userRes = new AdminBasicRes();
        BeanUtils.copyProperties(user, userRes);
        return new ResponseMessage(userRes);
    }

    @PostMapping("logout")
    public ResponseMessage logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "log out");
        return null;
    }


    @PostMapping("login")
    public ResponseMessage login(@RequestBody @Validated AdminLoginReq userReq,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        String email = userReq.getAdminName();
        String password = userReq.getPassword();
        Admin user = adminService.login(email, password);

        if (user == null) {
            //登陆失败
            return new ResponseMessage(ForumErrorCode.TelOrPassword_Error.getErrorCode(), "邮箱或密码错误");
        }
        //登陆成功,生成token
        String token = LoginUtils.generateToken(user.getAdminName(), user.getAdminKey());

        Cookie cookie = new Cookie(JwtUtil.JG_COOKIE, token);
        cookie.setMaxAge((int) (CookieUtils.TOKEN_EXP / 1000));
        if (cookieSecure) {
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
        }
        String host = request.getServerName();
        if (!CookieUtils.LOCALHOST.equals(host)) {
            cookie.setDomain(host.substring(host.indexOf(".") + 1));
//            cookie.setDomain(host);
        }
        cookie.setPath("/");
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        response.setHeader("Set-Cookie", response.getHeader("Set-Cookie") + "; SameSite=Lax");
        AdminLoginRes userRes = new AdminLoginRes();
        BeanUtils.copyProperties(user, userRes);
        userRes.setToken(token);
        return new ResponseMessage(userRes);
    }

}
