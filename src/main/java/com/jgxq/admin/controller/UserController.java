package com.jgxq.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.entity.Team;
import com.jgxq.admin.entity.User;
import com.jgxq.admin.service.UserService;
import com.jgxq.admin.service.impl.TeamServiceImpl;
import com.jgxq.common.define.BooleanEnum;
import com.jgxq.common.req.ResetPasswordReq;
import com.jgxq.common.res.UserRes;
import com.jgxq.common.req.UserUpdateReq;
import com.jgxq.common.utils.LoginUtils;
import com.jgxq.common.utils.PasswordHash;
import com.jgxq.core.enums.CommonErrorCode;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
 * @since 2021-01-14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TeamServiceImpl teamService;

    @GetMapping("page/{cur}/{size}")
    private ResponseMessage pageUser(@PathVariable Integer cur,
                                     @PathVariable Integer size,
                                     @RequestParam(value = "keyWord",required = false) String keyWord){
        Page<User> page = new Page<>(cur, size);
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        if(keyWord != null && keyWord.trim().length()>0){
            userQuery.like("nick_name",keyWord);
        }
        userQuery.orderByDesc("id");
        userService.page(page, userQuery);
        List<User> userList = page.getRecords();
        Set<Integer> teamIds = userList.stream().map(User::getHomeTeam).collect(Collectors.toSet());
        Map<Integer, String> teamMap = new HashMap<>();
        if(!teamIds.isEmpty()){
            List<Team> teams = teamService.listByIds(teamIds);
            teamMap = teams.stream().collect(Collectors.toMap(Team::getId, Team::getName));
        }
        Map<Integer, String> finalTeamMap = teamMap;
        List<UserRes> resList = userList.stream().map(user -> {
            UserRes userRes = new UserRes();
            BeanUtils.copyProperties(user, userRes);
            userRes.setAuthor(user.getAuthor().equals(BooleanEnum.True.getValue()));
            if (user.getHomeTeam() != null) {
                userRes.setHomeTeam(finalTeamMap.get(user.getHomeTeam()));
            }
            return userRes;
        }).collect(Collectors.toList());
        Page<UserRes> resPage = new Page<>(page.getCurrent(),page.getSize(),page.getTotal());
        resPage.setRecords(resList);
        return new ResponseMessage(resPage);
    }

    @PutMapping("{userKey}")
    private ResponseMessage updateUser(@PathVariable String userKey,
                                       @RequestBody UserUpdateReq userReq){
        UpdateWrapper<User> userUpdate = new UpdateWrapper<>();
        userUpdate.eq("userkey",userKey);
        User user = new User();
        user.setAuthor(userReq.getAuthor()?BooleanEnum.True.getValue():BooleanEnum.False.getValue());
        BeanUtils.copyProperties(userReq,user);
        boolean flag = userService.update(user, userUpdate);
        return new ResponseMessage(flag);
    }

    @PutMapping("/resetPwd/{userKey}")
    private ResponseMessage updateUser(@PathVariable String userKey){
        String password = "abc123";
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
        UpdateWrapper<User> userUpdate = new UpdateWrapper<>();
        userUpdate.eq("userkey",userKey).set("password",newPwd);
        boolean flag = userService.update(userUpdate);
        return new ResponseMessage(flag);
    }

}
