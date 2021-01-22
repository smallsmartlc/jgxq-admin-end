package com.jgxq.admin.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.injector.methods.SelectCount;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.service.PlayerService;
import com.jgxq.admin.service.impl.TeamServiceImpl;
import com.jgxq.common.define.Position;
import com.jgxq.common.define.StrongFoot;
import com.jgxq.common.dto.PlayerInfos;
import com.jgxq.common.dto.PlayerTeam;
import com.jgxq.common.req.PlayerReq;
import com.jgxq.common.req.TransferReq;
import com.jgxq.common.res.*;
import com.jgxq.common.utils.DateUtils;
import com.jgxq.core.enums.CommonErrorCode;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2021-1-19
 */
@RestController
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamServiceImpl teamService;

    @PostMapping
    public ResponseMessage addPlayer(@RequestBody @Validated PlayerReq playerReq) {
        String infos = JSON.toJSONString(playerReq.getInfos());

        Player player = new Player();
        player.setInfos(infos);
        BeanUtils.copyProperties(playerReq, player);

        playerService.save(player);

        return new ResponseMessage(player.getId());
    }

    @PutMapping("{id}")
    public ResponseMessage updatePlayer(@PathVariable("id") Integer id,
                                        @RequestBody @Validated PlayerReq playerReq) {
        String infos = JSON.toJSONString(playerReq.getInfos());

        Player player = new Player();
        BeanUtils.copyProperties(playerReq, player);
        player.setInfos(infos);
        player.setId(id);

        boolean flag = playerService.updateById(player);

        return new ResponseMessage(flag);
    }

    @DeleteMapping("{id}")
    public ResponseMessage deletePlayer(@PathVariable("id") Integer id) {
        boolean flag = playerService.removeById(id);
        return new ResponseMessage(flag);
    }

    @GetMapping("{id}")
    public ResponseMessage getPlayerById(@PathVariable("id") Integer id) {
        Player player = playerService.getById(id);
        if (player == null) {
            return new ResponseMessage(player);
        }
        PlayerRes playerRes = new PlayerRes();
        BeanUtils.copyProperties(player, playerRes);
        playerRes.setTeam(teamService.getBasicTeamById(player.getTeam()));
        PlayerInfos infos = JSON.parseObject(player.getInfos(),PlayerInfos.class);
        playerRes.setInfos(infos);
        return new ResponseMessage(playerRes);
    }

    @GetMapping("team/{teamId}")
    public ResponseMessage getTeamMembers(@PathVariable("teamId") Integer teamId) {
        QueryWrapper<Player> playQuery = new QueryWrapper<>();
        playQuery.select("id","name","head_image","nation","number","position","birthday")
                .eq("team",teamId).orderByAsc("name");
        List<Player> list = playerService.list(playQuery);
        List<PlayerTeamRes> res = new ArrayList<>();
        list.stream().map(player -> {
            //转为PlayerTeamList
            PlayerTeam playerTeam = new PlayerTeam();
            BeanUtils.copyProperties(player, playerTeam);
            playerTeam.setPosition(player.getPosition().intValue());
            playerTeam.setAge(DateUtils.getAgeByBirth(player.getBirthday()));
            return playerTeam;
        }).collect(Collectors.groupingBy((playerTeam -> Position.getPositionByVal(playerTeam.getPosition()))))
                //转为map
            .forEach((key, value) -> {
                //遍历map
                res.add(new PlayerTeamRes(key, value));
            });

        return new ResponseMessage(res);
    }

    @GetMapping("match/{teamId}")
    public ResponseMessage getMatchTeamMembers(@PathVariable("teamId") Integer teamId) {
        QueryWrapper<Player> playQuery = new QueryWrapper<>();
        playQuery.select("id","name","number","position")
                .eq("team",teamId).orderByAsc("name");
        List<Player> list = playerService.list(playQuery);
        List<PlayerMatchRes> resList = list.stream().map(p -> {
            //转为PlayerTeamList
            PlayerMatchRes player = new PlayerMatchRes();
            BeanUtils.copyProperties(p, player);
            int pos = p.getPosition().intValue();
            if (pos == Position.AF.getValue()) {
                //比赛阵容多一个前腰位置,需要将前锋值处理一下
                pos++;
            }
            player.setMatchPos(pos);
            return player;
        }).sorted(Comparator.comparing(PlayerMatchRes::getMatchPos)).collect(Collectors.toList());

        return new ResponseMessage(resList);
    }

    @GetMapping("page/{cur}/{size}")
    public ResponseMessage pagePlayer(@PathVariable("cur")Integer cur,
                                      @PathVariable("size") Integer size){
        Page<Player> page = new Page<>(cur, size);
        playerService.page(page);
        List<Player> playerList = page.getRecords();
        List<Integer> teamIds = playerList.stream().map(Player::getTeam).collect(Collectors.toList());
        Map<Integer, TeamBasicRes> map = teamService.getBasicTeamsByIds(teamIds)
                .stream().collect(Collectors.toMap(TeamBasicRes::getId, t -> t));
        List<PlayerBasicRes> resList = playerList.stream().map(p -> {
            PlayerBasicRes playerBasic = new PlayerBasicRes();
            BeanUtils.copyProperties(p, playerBasic);
            playerBasic.setTeam(map.get(p.getTeam()));
            playerBasic.setPosition(Position.getPositionByVal(p.getPosition()));
            if(p.getBirthday() != null){
                playerBasic.setAge(DateUtils.getAgeByBirth(p.getBirthday()));
            }
            return playerBasic;
        }).collect(Collectors.toList());
        Page<PlayerBasicRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(resList);
        return new ResponseMessage(resPage);
    }

    @PutMapping("transfer")
    public ResponseMessage transfer(@RequestBody TransferReq transferReq){
        UpdateWrapper<Player> playerUpdate = new UpdateWrapper<>();
        playerUpdate.eq("id",transferReq.getPlayerId());
        if(transferReq.getSourceTeam()!=null){
            playerUpdate.eq("team",transferReq.getSourceTeam());
        }
        playerUpdate.set("team",transferReq.getTargetTeam());
        boolean flag = playerService.update(playerUpdate);
        return new ResponseMessage(flag);
    }

    @GetMapping("search")
    public ResponseMessage searchPlayer(@RequestParam("keyword") String keyword){
        List<PlayerMatchRes> list = playerService.searchPlayer(keyword);
        return new ResponseMessage(list);
    }

}
