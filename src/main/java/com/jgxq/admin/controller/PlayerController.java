package com.jgxq.admin.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelCommonException;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.client.RedisCache;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.service.PlayerService;
import com.jgxq.admin.service.impl.TeamServiceImpl;
import com.jgxq.common.define.Position;
import com.jgxq.common.dto.PlayerExcelDto;
import com.jgxq.common.dto.PlayerInfos;
import com.jgxq.common.dto.PlayerTeam;
import com.jgxq.common.req.PlayBatchRetireReq;
import com.jgxq.common.req.PlayerBatchAddReq;
import com.jgxq.common.req.PlayerReq;
import com.jgxq.common.req.TransferReq;
import com.jgxq.common.res.*;
import com.jgxq.common.utils.DateUtils;
import com.jgxq.core.anotation.AllowAccess;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.CommonErrorCode;
import com.jgxq.core.enums.RedisKeys;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.*;
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
@UserPermissionConf
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamServiceImpl teamService;

    @Autowired
    private RedisCache redisCache;

    @RolePermissionConf("0602")
    @PostMapping
    public ResponseMessage addPlayer(@RequestBody @Validated PlayerReq playerReq) {
        String infos = JSON.toJSONString(playerReq.getInfos());

        Player player = new Player();
        player.setInfos(infos);
        BeanUtils.copyProperties(playerReq, player);

        playerService.save(player);

        return new ResponseMessage(player.getId());
    }

    @RolePermissionConf("0604")
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

    @RolePermissionConf("0603")
    @DeleteMapping("{id}")
    public ResponseMessage deletePlayer(@PathVariable("id") Integer id) {
        boolean flag = playerService.removeById(id);
        return new ResponseMessage(flag);
    }

    //    @RolePermissionConf("0600")
    @GetMapping("{id}")
    @AllowAccess
    public ResponseMessage getPlayerById(@PathVariable("id") Integer id) {
        Player player = playerService.getById(id);
        if (player == null) {
            return new ResponseMessage(player);
        }
        PlayerRes playerRes = new PlayerRes();
        BeanUtils.copyProperties(player, playerRes);
        playerRes.setTeam(teamService.getBasicTeamById(player.getTeam()));
        PlayerInfos infos = JSON.parseObject(player.getInfos(), PlayerInfos.class);
        playerRes.setInfos(infos);
        return new ResponseMessage(playerRes);
    }


    @PutMapping("user/{id}")
    @AllowAccess
    public ResponseMessage playerUpdate(@PathVariable("id") Integer id,
                                        @RequestBody @Validated PlayerReq playerReq) {
        String infos = JSON.toJSONString(playerReq.getInfos());

        Player player = new Player();
        BeanUtils.copyProperties(playerReq, player);
        player.setInfos(infos);
        player.setId(null);

        List<Integer> ids = new ArrayList<>();
        try {
            ids = redisCache.lrangeInt(RedisKeys.edit_team.getKey());
        } catch (Exception e) {
            System.err.println("redis服务器异常");
        }
        if (ids.isEmpty()) {
            return new ResponseMessage(false);
        }
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("id", id).in("team", ids);

        boolean flag = playerService.update(player, playerQuery);

        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0505")
    @GetMapping("team/{teamId}")
    public ResponseMessage getTeamMembers(@PathVariable("teamId") Integer teamId) {
        QueryWrapper<Player> playQuery = new QueryWrapper<>();
        playQuery.select("id", "name", "head_image", "nation", "number", "position", "birthday")
                .eq("team", teamId).orderByAsc("name");
        List<Player> list = playerService.list(playQuery);
        List<PlayerTeam> res = list.stream().map(player -> {
            //转为PlayerTeamList
            PlayerTeam playerTeam = new PlayerTeam();
            BeanUtils.copyProperties(player, playerTeam);
            playerTeam.setPosition(player.getPosition().intValue());
            playerTeam.setAge(DateUtils.getAgeByBirth(player.getBirthday()));
            return playerTeam;
        }).collect(Collectors.toList());
        return new ResponseMessage(res);
    }

    @RolePermissionConf("0505")
    @GetMapping("match/{teamId}")
    public ResponseMessage getMatchTeamMembers(@PathVariable("teamId") Integer teamId) {
        QueryWrapper<Player> playQuery = new QueryWrapper<>();
        playQuery.select("id", "name", "number", "position")
                .eq("team", teamId).orderByAsc("name");
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

    @RolePermissionConf("0601")
    @GetMapping("page/{cur}/{size}")
    public ResponseMessage pagePlayer(@PathVariable("cur") Integer cur,
                                      @PathVariable("size") Integer size,
                                      @RequestParam(value = "keyword", required = false) String keyword) {
        Page<Player> page = new Page<>(cur, size);
        QueryWrapper<Player> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.likeRight("name", keyword);
        }
        playerService.page(page, wrapper);
        List<Player> playerList = page.getRecords();
        List<Integer> teamIds = playerList.stream().map(Player::getTeam).collect(Collectors.toList());
        Map<Integer, TeamBasicRes> map = teamService.getBasicTeamsByIds(teamIds)
                .stream().collect(Collectors.toMap(TeamBasicRes::getId, t -> t));
        List<PlayerBasicRes> resList = playerList.stream().map(p -> {
            PlayerBasicRes playerBasic = new PlayerBasicRes();
            BeanUtils.copyProperties(p, playerBasic);
            playerBasic.setTeam(map.get(p.getTeam()));
            playerBasic.setPosition(Position.getPositionByVal(p.getPosition()));
            if (p.getBirthday() != null) {
                playerBasic.setAge(DateUtils.getAgeByBirth(p.getBirthday()));
            }
            return playerBasic;
        }).collect(Collectors.toList());
        Page<PlayerBasicRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(resList);
        return new ResponseMessage(resPage);
    }

    @RolePermissionConf("0602")
    @PutMapping("transfer")
    public ResponseMessage transfer(@RequestBody TransferReq transferReq) {
        UpdateWrapper<Player> playerUpdate = new UpdateWrapper<>();
        playerUpdate.eq("id", transferReq.getPlayerId());
        if (transferReq.getSourceTeam() != null) {
            playerUpdate.eq("team", transferReq.getSourceTeam());
        }
        playerUpdate.set("team", transferReq.getTargetTeam());
        boolean flag = playerService.update(playerUpdate);
        return new ResponseMessage(flag);
    }

    @GetMapping("search")
    public ResponseMessage searchPlayer(@RequestParam("keyword") @NotBlank String keyword) {
        List<PlayerSearchRes> list = playerService.searchPlayer(keyword);
        return new ResponseMessage(list);
    }

    @RolePermissionConf("0606")
    @PutMapping("batchRetire")
    public ResponseMessage batchRetire(@RequestBody @Validated PlayBatchRetireReq retireReq) {
        UpdateWrapper<Player> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("team",retireReq.getTeamId());
        updateWrapper.in("id",retireReq.getPlayerIdList());
        updateWrapper.set("team",0);
        boolean flag = playerService.update(updateWrapper);
        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0607")
    @PostMapping("upload/{teamId}")
    public ResponseMessage importPlayer(MultipartFile file,@PathVariable Integer teamId){
        List<PlayerExcelDto> list = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), PlayerExcelDto.class,new PageReadListener<PlayerExcelDto>(dataList -> {
                 list.addAll(dataList);
            })).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ExcelCommonException e){
            throw new SmartException(CommonErrorCode.BAD_REQUEST.getErrorCode(),"只允许上传Excel文件");
        }
        if(list.isEmpty()){
            throw new SmartException(CommonErrorCode.BAD_PARAMETERS.getErrorCode(),"球员表不能为空");
        }
        List<String> nameList = list.stream()
                .map(PlayerExcelDto::getName).collect(Collectors.toList());

        QueryWrapper query = new QueryWrapper();
        query.in("name",nameList);
        query.eq("team",teamId);

        List<Player> existPlayerList = playerService.list(query);

        List<Player> notExistPlayerList = null;
        if(!existPlayerList.isEmpty()){
            Set<String> existPlayerSet = existPlayerList.stream().map(Player::getName).collect(Collectors.toSet());
            notExistPlayerList = list.stream().filter(p->!existPlayerSet.contains(p.getName()))
                    .map(p-> {
                        Player entity = p.getEntity();
                        entity.setTeam(teamId);
                        return entity;
                    }).collect(Collectors.toList());
        }else{
            notExistPlayerList = list.stream().map(p-> {
                Player entity = p.getEntity();
                entity.setTeam(teamId);
                return entity;
            }).collect(Collectors.toList());
        }
        if(!notExistPlayerList.isEmpty()){
            playerService.saveBatch(notExistPlayerList);
        }
        return new ResponseMessage(notExistPlayerList.size());
    }

    @RolePermissionConf("0607")
    @PostMapping("batch/add")
    public ResponseMessage batchAddPlayer(@RequestBody @Validated PlayerBatchAddReq batchReq){
        List<Player> players = batchReq.getPlayers();
        if (players.isEmpty()){
            throw new SmartException(CommonErrorCode.BAD_REQUEST.getErrorCode(),"列表不能为空");
        }
        List<String> playerIds = players.stream().map(Player::getName).collect(Collectors.toList());
        QueryWrapper<Player> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("team",batchReq.getTeamId())
                .in("name",playerIds);
        Set<String> nameSet = playerService.list(queryWrapper)
                .stream().map(Player::getName).collect(Collectors.toSet());
        List<Player> saveList = new ArrayList<>(nameSet.size());

        for (Player p : players) {
            if(nameSet.contains(p.getName())){
                continue;
            }
            p.setEnName("");
            p.setInfos("{\"normal\": [{\"name\": \"身价\", \"value\": \"60万欧元\"}]}");
            p.setTeam(batchReq.getTeamId());
            p.setPosition((byte) Position.AM.getValue());
            p.setWeight(0);
            p.setHeight(0);
            p.setStrongFoot((byte)0);
            saveList.add(p);
        }
        playerService.saveBatch(saveList);
        return new ResponseMessage(saveList.size());
    }

}
