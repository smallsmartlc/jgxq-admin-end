package com.jgxq.admin.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Team;
import com.jgxq.admin.service.TeamService;
import com.jgxq.common.dto.TeamInfos;
import com.jgxq.common.req.TeamReq;
import com.jgxq.common.res.TeamBasicRes;
import com.jgxq.common.res.TeamRes;
import com.jgxq.common.utils.ReqUtils;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.resp.PageResponse;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
@RestController
@RequestMapping("/team")
@UserPermissionConf
public class TeamController {

    @Autowired
    private TeamService teamService;

    @RolePermissionConf("0502")
    @PostMapping
    public ResponseMessage addTeam(@RequestBody @Validated TeamReq teamReq) {

        Team team = ReqUtils.teamReqToTeam(teamReq);
        teamService.save(team);

        return new ResponseMessage(team.getId());
    }

    @RolePermissionConf("0504")
    @PutMapping("{id}")
    public ResponseMessage updateTeamInfo(@PathVariable("id") Integer id,
                                          @RequestBody @Validated TeamReq teamReq) {
        Team team = ReqUtils.teamReqToTeam(teamReq);
        team.setId(id);
        boolean flag = teamService.updateById(team);

        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0503")
    @DeleteMapping("{id}")
    public ResponseMessage deleteTeamById(@PathVariable("id") Integer id) {
        boolean flag = teamService.removeById(id);
        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0501")
    @GetMapping("page/{pageNum}/{pageSize}")
    public ResponseMessage PageTeams(@PathVariable("pageNum") Integer pageNum,
                                     @PathVariable("pageSize") Integer pageSize) {

        Page<Team> page = new Page<>(pageNum,pageSize);
        teamService.page(page);
        List<Team> records = page.getRecords();
        List<TeamBasicRes> res = new ArrayList<>(records.size());
        records.forEach((team) -> {
            TeamBasicRes teamBasic = new TeamBasicRes();
            BeanUtils.copyProperties(team, teamBasic);
            res.add(teamBasic);
        });

        return new ResponseMessage(new PageResponse(res, pageNum, pageSize, page.getTotal()));

    }

    @RolePermissionConf("0500")
    @GetMapping("infos/{id}")
    public ResponseMessage getTeamById(@PathVariable("id") Integer id) {
        Team team = teamService.getById(id);
        if(team == null){
            return new ResponseMessage(team);
        }
        TeamRes teamRes = new TeamRes();
        BeanUtils.copyProperties(team, teamRes);
        TeamInfos infos = JSON.parseObject(team.getInfos(), TeamInfos.class);
        teamRes.setInfos(infos);
        return new ResponseMessage(teamRes);
    }

}
