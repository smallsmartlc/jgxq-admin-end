package com.jgxq.core.intercepter;

import com.alibaba.fastjson.JSON;
import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.entity.Role;
import com.jgxq.admin.service.AdminService;
import com.jgxq.admin.service.RoleService;
import com.jgxq.common.utils.JwtUtil;
import com.jgxq.core.anotation.AllowAccess;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.UserPermissionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LuCong
 * @since 2020-12-08
 **/
@Component
public class JwtLoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AdminService adminService;

    @Autowired
    private RoleService roleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            if (handler instanceof ResourceHttpRequestHandler) {
                // 静态资源bug,小心点
                return true;
            }
            return false;
        }
        //先想办法拿到这个handler
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        AllowAccess allowAccess = handlerMethod.getMethodAnnotation(AllowAccess.class);


        //获取类上是否有需要权限验证的注解
        UserPermissionConf userPermissionConf = handlerMethod.getBeanType().getAnnotation(UserPermissionConf.class);
        if (userPermissionConf == null) {
            // 如果没有注解应该怎么办呢
            // TODO :暂时先放行,如果后期改的话再调整
            return true;
        }

        Cookie[] cookies = request.getCookies();
        if (allowAccess != null) {
            //有直接访问注解,放行
            String key = getUserKey(cookies,response);
            request.setAttribute("userKey", key);
            return true;
        }

        if (userPermissionConf.Type().equals(UserPermissionType.ALLOW)) {
            // 放行
            String key = getUserKey(cookies,response);
            request.setAttribute("userKey", key);
            return true;
        }

        //获取header
        String authHeader = "";

        if (cookies == null) {
            setRespError(response, HttpStatus.UNAUTHORIZED.value(), "login expired");
            return false;
        }
        for (Cookie cookie : cookies) {
            if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                authHeader = cookie.getValue();
            }
        }

        if (StringUtils.isBlank(authHeader)) {
            //cookie消失了
            setRespError(response, HttpStatus.UNAUTHORIZED.value(), "login expired");
            return false;
        }

        String userkey = null;
        try {
            userkey = JwtUtil.dealRequests(authHeader);
        } catch (Exception e) {
            //说明这是一个垃圾Cookie,解析出一个没用的key,可能用户已经被删除无法登陆了
            // 删掉这个垃圾Cookie
            for (Cookie cookie : cookies) {
                if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            setRespError(response, HttpStatus.UNAUTHORIZED.value(), "invalid token");
            return false;
        }
        if (StringUtils.isBlank(userkey)) {
            // 错误的token,无法解析userkey
            //说明这是一个垃圾Cookie,解析出一个没用的key,可能用户已经被删除无法登陆了
            // 删掉这个垃圾Cookie
            for (Cookie cookie : cookies) {
                if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            setRespError(response, HttpStatus.UNAUTHORIZED.value(), "invalid token");
            return false;
        }
        Admin admin = adminService.getAdminByPK("admin_key",userkey);
        if (admin == null) {
            //说明这是一个垃圾Cookie,解析出一个没用的key,可能用户已经被删除无法登陆了
            // 删掉这个垃圾Cookie
            for (Cookie cookie : cookies) {
                if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            setRespError(response, HttpStatus.UNAUTHORIZED.value(), "token expired");
            return false;
        }

        Role role = roleService.getById(admin.getRoleId());
        String permissionsStr = role==null?"[]":role.getPermissions();
        Set<String> permissions = new HashSet<>(JSON.parseArray(permissionsStr,String.class));

//        对管理员权限验证
        RolePermissionConf rolePermissionConf = handlerMethod.getMethodAnnotation(RolePermissionConf.class);
        if(rolePermissionConf!=null && (!permissions.contains(rolePermissionConf.value()))){
            setRespError(response, HttpStatus.FORBIDDEN.value(), "暂无该操作权限");
            return false;
        }


        // 现在终于查出一个正常的用户了
        // 将用户信息放进request中
        //  待优化传userkey方式
        //  优化完成,使用@RequestAttribute接收
        request.setAttribute("userKey", admin.getAdminKey());

        return true;
    }


    public void setRespError(HttpServletResponse response, int code, String msg) {
        try {
            response.sendError(code, msg);
        } catch (Exception e) {

        }

    }

    private String getUserKey(Cookie[] cookies, HttpServletResponse response) {
        //放行也拿一下看有没有userkey
        if (cookies == null) {
            return null;
        }
        String authHeader = "";
        for (Cookie cookie : cookies) {
            if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                authHeader = cookie.getValue();
            }
        }

        if (StringUtils.isBlank(authHeader)) {
            //cookie消失了
            return null;
        }

        String userkey = null;
        try {
            userkey = JwtUtil.dealRequests(authHeader);
        } catch (Exception e) {
            return null;
        }
        if (StringUtils.isBlank(userkey)) {
            // 错误的token,无法解析userkey
            return "";
        }
        Admin admin = adminService.getAdminByPK("admin_key",userkey);
        if (admin == null) {
            //说明这是一个垃圾Cookie,解析出一个没用的key,可能用户已经被删除无法登陆了
            // 删掉这个垃圾Cookie
            for (Cookie cookie : cookies) {
                if ((JwtUtil.JG_COOKIE).equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            return null;
        }
        // 现在终于查出一个正常的用户了
        // 将用户信息放进request中
        //  待优化传userkey方式
        //  优化完成,使用@RequestAttribute接收
        return null;
    }

}
