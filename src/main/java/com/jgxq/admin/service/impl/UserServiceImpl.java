package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.entity.User;
import com.jgxq.admin.mapper.UserMapper;
import com.jgxq.admin.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    public String getAuthorInfo(String userKey) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("userkey", "nick_name")
                .eq("userkey", userKey);
        User user = userMapper.selectOne(wrapper);
        return user.getNickName();
    }
    public Map<String, String> getAuthorInfo(Collection<String> userKey) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("userkey", "nick_name")
                .in("userkey", userKey);
        List<User> userList = userMapper.selectList(wrapper);
        Map<String, String> resMap = userList.stream().collect(Collectors.toMap(User::getUserkey, User::getNickName));
        return resMap;
    }

    public User getUserByPK(String col, String PK) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq(col, PK);
        User user = userMapper.selectOne(wrapper);
        return user;
    }

    public List<User> getUserByKeyList(Set<String> userKeys) {
        if(userKeys.isEmpty()){
            return Collections.EMPTY_LIST;
        }
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("userkey",userKeys);
        return userMapper.selectList(userQuery);
    }
}
