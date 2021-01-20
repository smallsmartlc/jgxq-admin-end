package com.jgxq.admin.service.impl;

import com.jgxq.admin.entity.Team;
import com.jgxq.admin.mapper.TeamMapper;
import com.jgxq.admin.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.res.TeamBasicRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Autowired
    private TeamMapper teamMapper;

    public TeamBasicRes getBasicTeamById(Integer id) {
        Team team = teamMapper.selectById(id);
        if(team == null){
            return null;
        }
        TeamBasicRes teamRes = new TeamBasicRes();
        BeanUtils.copyProperties(team,teamRes);
        return teamRes;
    }
}
