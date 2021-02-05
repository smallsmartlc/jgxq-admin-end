package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jgxq.admin.client.EsUtils;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.mapper.PlayerMapper;
import com.jgxq.admin.service.PlayerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.define.Position;
import com.jgxq.common.res.PlayerBasicRes;
import com.jgxq.common.res.PlayerMatchRes;
import com.jgxq.common.res.PlayerSearchRes;
import com.jgxq.common.res.TagSearchRes;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
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

    @Autowired
    private EsUtils esClient;

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
    public List<PlayerSearchRes> searchPlayer(String keyword) {
        List<Integer> playerIds = searchPlayerEs(keyword);
        if (playerIds.isEmpty()){
            return Collections.emptyList();
        }
        List<Player> list = playerMapper.selectBatchIds(playerIds);
        List<PlayerSearchRes> resList = list.stream().map(p -> {
            //转为PlayerTeamList
            PlayerSearchRes player = new PlayerSearchRes();
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

    private List<Integer> searchPlayerEs(String keyword) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        MatchPhraseQueryBuilder matchPhraseQuery = QueryBuilders.matchPhraseQuery("name.pinyin", keyword);
        TermQueryBuilder termQuery1 = QueryBuilders.termQuery("status", 1);
        TermQueryBuilder termQuery2 = QueryBuilders.termQuery("type",1);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchPhraseQuery);
        boolQueryBuilder.must(termQuery1);
        boolQueryBuilder.must(termQuery2);

        builder.query(boolQueryBuilder);
        List<TagSearchRes> resList = esClient.search("jgxq_tag", builder, TagSearchRes.class, 10);
        return resList.stream().map(TagSearchRes::getObjectId).collect(Collectors.toList());
    }
}
