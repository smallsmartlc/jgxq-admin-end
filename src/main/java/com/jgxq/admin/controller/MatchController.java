package com.jgxq.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.common.req.MatchReq;
import com.jgxq.common.res.MatchBasicRes;
import com.jgxq.common.res.MatchRes;
import com.jgxq.common.res.NewsBasicRes;
import com.jgxq.common.res.TeamBasicRes;
import com.jgxq.common.utils.DateUtils;
import com.jgxq.common.utils.ReqUtils;
import com.jgxq.common.utils.ResUtils;
import com.jgxq.core.resp.ResponseMessage;
import com.jgxq.admin.entity.Match;
import com.jgxq.admin.entity.News;
import com.jgxq.admin.service.MatchService;
import com.jgxq.admin.service.TeamService;
import com.jgxq.admin.service.impl.MatchServiceImpl;
import com.jgxq.admin.service.impl.NewsServiceImpl;
import com.jgxq.admin.service.impl.TeamServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-11
 */
@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamServiceImpl teamService;

    @Autowired
    private NewsServiceImpl newsService;

    @PostMapping
    public ResponseMessage addMatch(@RequestBody @Validated MatchReq matchReq) {

        Match match = ReqUtils.matchReqToMatch(matchReq);
        matchService.save(match);

        return new ResponseMessage(match.getId());
    }

    @PutMapping("{id}")
    public ResponseMessage updateMatch(@PathVariable("id") Integer id,
                                       @RequestBody @Validated MatchReq matchReq) {

        Match match = ReqUtils.matchReqToMatch(matchReq);
        match.setId(id);
        boolean flag = matchService.updateById(match);

        return new ResponseMessage(flag);
    }

    @DeleteMapping("{id}")
    public ResponseMessage deleteMatch(@PathVariable("id") Integer id) {
        boolean remove = matchService.removeById(id);
        return new ResponseMessage(remove);
    }

    @GetMapping("{id}")
    public ResponseMessage getMatch(@PathVariable("id") Integer id) {
        Match match = matchService.getById(id);
        if (match == null) {
            return new ResponseMessage(match);
        }
        MatchRes res = ResUtils.matchToMatchRes(match);

        NewsBasicRes newsBasic = null;
        if(match.getMatchNews()!=null){
            News news = newsService.getById(match.getMatchNews());
            newsBasic = new NewsBasicRes();
            BeanUtils.copyProperties(news,newsBasic);
        }
        res.setMatchNews(newsBasic);

        TeamBasicRes homeTeam = teamService.getBasicTeamById(match.getHomeTeam());
        TeamBasicRes visitingTeam = teamService.getBasicTeamById(match.getVisitingTeam());
        res.setHomeTeam(homeTeam);
        res.setVisitingTeam(visitingTeam);

        return new ResponseMessage(res);
    }

    @GetMapping("/page")
    public ResponseMessage PageMatches(@RequestParam(value = "start", required = false) Date start,
                                       @RequestParam(value = "teamId", required = false) String teamId,
                                       @RequestParam("pageNum") Integer pageNum,
                                       @RequestParam("pageSize") Integer pageSize) {

        Page<MatchBasicRes> res = matchService.listMatches(start, teamId, pageNum, pageSize);

        return new ResponseMessage(res);
    }

}
