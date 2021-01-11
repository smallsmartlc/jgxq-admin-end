package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.entity.Admin;
import com.jgxq.admin.mapper.AdminMapper;
import com.jgxq.admin.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.define.KeyLength;
import com.jgxq.common.req.AdminRegReq;
import com.jgxq.common.utils.LoginUtils;
import com.jgxq.common.utils.PasswordHash;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-11
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getAdminByPK(String col, String PK) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq(col, PK);
        Admin admin = adminMapper.selectOne(wrapper);
        return admin;
    }

    @Override
    public String addAdmin(AdminRegReq userReq) {
        try {
            userReq.setPassword(PasswordHash.createHash(userReq.getPassword()));
        } catch (Exception e) {
            return null;
        }

        Admin user = new Admin();
        user.setAdminKey(LoginUtils.getRandomUserKey(KeyLength.USER_KEY_LEN.getLength()));
        BeanUtils.copyProperties(userReq, user);

        adminMapper.insert(user);
        return user.getAdminKey();
    }

    @Override
    public Admin login(String adminName, String password) {
        Admin admin = new Admin();
        admin.setAdminName(adminName);

        Admin user = adminMapper.selectOne(new QueryWrapper<>(admin));
        if (user == null) {
            return null;
        }
        boolean equal;
        try {
            equal = PasswordHash.validatePassword(password, user.getPassword());
        } catch (Exception e) {
            equal = false;
        }
        if (equal) {
            return user;
        }
        return null;
    }


}
