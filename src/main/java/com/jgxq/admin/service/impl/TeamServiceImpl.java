package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.client.EsUtils;
import com.jgxq.admin.entity.Team;
import com.jgxq.admin.mapper.TeamMapper;
import com.jgxq.admin.service.TeamService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.res.TagSearchRes;
import com.jgxq.common.res.TeamBasicRes;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
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
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private EsUtils esClient;

    public TeamBasicRes getBasicTeamById(Integer id) {
        Team team = teamMapper.selectById(id);
        if(team == null){
            return null;
        }
        TeamBasicRes teamRes = new TeamBasicRes();
        BeanUtils.copyProperties(team,teamRes);
        return teamRes;
    }
    public List<TeamBasicRes> getBasicTeamsByIds(Collection<Integer> ids) {
        List<Team> teams = teamMapper.selectBatchIds(ids);
        List<TeamBasicRes> resList = teams.stream().map(t -> {
            TeamBasicRes teamRes = new TeamBasicRes();
            BeanUtils.copyProperties(t, teamRes);
            return teamRes;
        }).collect(Collectors.toList());

        return resList;
    }

    @Override
    public Page<TeamBasicRes> pageTeamEs(Integer pageNum, Integer pageSize, String keyword) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        MatchPhraseQueryBuilder matchPhraseQuery = QueryBuilders.matchPhraseQuery("name.pinyin", keyword);
        TermQueryBuilder termQuery1 = QueryBuilders.termQuery("status", 1);
        TermQueryBuilder termQuery2 = QueryBuilders.termQuery("type",0);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchPhraseQuery);
        boolQueryBuilder.must(termQuery1);
        boolQueryBuilder.must(termQuery2);

        builder.query(boolQueryBuilder);
        Page<TagSearchRes> page = esClient.search("jgxq_tag", builder, TagSearchRes.class, pageNum, pageSize);
        Page resPage = new Page<TeamBasicRes>(page.getCurrent(),page.getSize(),page.getTotal());
        resPage.setRecords(page.getRecords().stream().map(t->{
            TeamBasicRes teamBasicRes = new TeamBasicRes();
            BeanUtils.copyProperties(t,teamBasicRes);
            teamBasicRes.setId(t.getObjectId());
            return teamBasicRes;
        }).collect(Collectors.toList()));
        return resPage;
    }
}
