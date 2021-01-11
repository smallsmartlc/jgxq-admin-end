package com.jgxq.admin.service;

import com.jgxq.admin.entity.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.req.AdminRegReq;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-11
 */
public interface AdminService extends IService<Admin> {

    Admin getAdminByPK(String col, String PK);

    String addAdmin(AdminRegReq userReq);

    Admin login(String email, String password);
}
