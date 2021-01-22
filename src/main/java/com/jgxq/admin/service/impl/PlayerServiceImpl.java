package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.mapper.PlayerMapper;
import com.jgxq.admin.service.PlayerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.define.Position;
import com.jgxq.common.res.PlayerBasicRes;
import com.jgxq.common.res.PlayerMatchRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-19
 */
@Service
public class PlayerServiceImpl extends ServiceImpl<PlayerMapper, Player> implements PlayerService {
    @Autowired
    private PlayerMapper playerMapper;

    public List<PlayerBasicRes> getBasicByIds(Collection<Integer> ids){
        QueryWrapper<Player> wrapper = new QueryWrapper<>();
        wrapper.select("id","name","head_image")
                .in("id",ids);
        List<Player> players = playerMapper.selectList(wrapper);
        List<PlayerBasicRes> playRes = players.stream().map(p -> {
            PlayerBasicRes res = new PlayerBasicRes();
            BeanUtils.copyProperties(p, res);
            return res;
        }).collect(Collectors.toList());
        return playRes;
    }

    @Override
    public List<PlayerMatchRes> searchPlayer(String keyword) {
        QueryWrapper<Player> wrapper = new QueryWrapper<>();
        wrapper.like("name",keyword).orderByAsc("LENGTH(name)");
        List<Player> list = playerMapper.selectList(wrapper);
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
        }).collect(Collectors.toList());
        return resList;
    }

    public List<PlayerMatchRes> searchPlayerEs(String keyword) {
        //TODO 使用es搜索
        return null;
    }
}
