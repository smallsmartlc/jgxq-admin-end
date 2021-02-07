package com.jgxq.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.res.TeamBasicRes;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
public interface TeamService extends IService<Team> {

    Page<TeamBasicRes> pageTeamEs(Integer pageNum, Integer pageSize, String keyword);
}
