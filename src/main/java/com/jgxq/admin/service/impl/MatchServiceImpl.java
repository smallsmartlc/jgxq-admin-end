package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Match;
import com.jgxq.admin.mapper.MatchMapper;
import com.jgxq.admin.service.MatchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.res.MatchBasicRes;
import com.jgxq.common.res.TeamBasicRes;
import com.jgxq.common.utils.DateUtils;
import org.springframework.beans.BeanUtils;
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
public class MatchServiceImpl extends ServiceImpl<MatchMapper, Match> implements MatchService {

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private TeamServiceImpl teamService;

    @Override
    public Page<MatchBasicRes> listMatches(Date start, String teamId, Integer pageNum, Integer pageSize) {
        QueryWrapper<Match> matchQuery = new QueryWrapper<>();
        if (start == null) {
            matchQuery.orderByDesc("start_time");
        } else {
            start = DateUtils.initDateByDay(start);
            matchQuery.ge("start_time", start);
        }
        if(teamId!=null){
            matchQuery.eq("home_team",teamId).or().eq("visiting_team",teamId);
        }
        Page page = new Page(pageNum,pageSize);
        matchMapper.selectPage(page,matchQuery);
        List<MatchBasicRes> res = matchListToBasicRes(page.getRecords());
        Page<MatchBasicRes> resPage = new Page<>(page.getCurrent(),page.getSize(),page.getTotal());
        resPage.setRecords(res);
        return resPage;
    }

    private List<MatchBasicRes> matchListToBasicRes(List<Match> matchList){
        Set<Integer> teamIds = new HashSet<>();
        matchList.forEach(m -> {
            teamIds.add(m.getHomeTeam());
            teamIds.add(m.getVisitingTeam());
        });
        List<TeamBasicRes> teamList = teamService.getBasicTeamsByIds(teamIds);
        Map<Integer, TeamBasicRes> map = teamList
                .stream().collect(Collectors.toMap(TeamBasicRes::getId, v -> v));
        List<MatchBasicRes> res = matchList.stream().map(t -> {
            MatchBasicRes match = new MatchBasicRes();
            BeanUtils.copyProperties(t, match);
            match.setHomeTeam(map.get(t.getHomeTeam()));
            match.setVisitingTeam(map.get(t.getVisitingTeam()));
            return match;
        }).collect(Collectors.toList());
        return res;
    }

}
