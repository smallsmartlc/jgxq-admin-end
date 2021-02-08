package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.client.EsUtils;
import com.jgxq.admin.entity.News;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.mapper.NewsMapper;
import com.jgxq.admin.service.NewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.res.NewsBasicRes;
import com.jgxq.common.res.NewsSearchRes;
import com.jgxq.common.res.PlayerMatchRes;
import com.jgxq.common.res.TagSearchRes;
import com.sun.xml.internal.bind.v2.TODO;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private EsUtils esClient;

    @Override
    public Page<NewsBasicRes> pageNews(Integer pageNum, Integer pageSize) {
        Page<News> page = new Page<>(pageNum, pageSize);
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.select("id", "title", "cover", "author", "create_at").orderByDesc("create_at");
        newsMapper.selectPage(page, wrapper);
        List<News> newsList = page.getRecords();
        List<String> userKeys = newsList.stream().map(News::getAuthor).collect(Collectors.toList());
        Map<String, String> authorMap = userService.getAuthorInfo(userKeys);
        List<NewsBasicRes> newsBasicList = newsList.stream().map(news -> {
            NewsBasicRes newsBasicRes = new NewsBasicRes();
            BeanUtils.copyProperties(news, newsBasicRes);
            newsBasicRes.setAuthor(authorMap.get(news.getAuthor()));
            return newsBasicRes;
        }).collect(Collectors.toList());
        Page<NewsBasicRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(newsBasicList);
        return resPage;
    }

    @Override
    public Page<NewsBasicRes> searchNewsPage(Integer pageNum, Integer pageSize, String keyword) {
//        return searchNewsPageEs(pageNum, pageSize, keyword);//es
        Page<News> page = new Page<>(pageNum, pageSize); //fixme sql
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.like("title", keyword).orderByAsc("LENGTH(title)");
        newsMapper.selectPage(page, wrapper);
        List<News> newsList = page.getRecords();
        List<String> userKeys = newsList.stream().map(News::getAuthor).collect(Collectors.toList());
        Map<String, String> authorMap = userService.getAuthorInfo(userKeys);
        List<NewsBasicRes> newsBasicList = newsList.stream().map(news -> {
            NewsBasicRes newsBasicRes = new NewsBasicRes();
            BeanUtils.copyProperties(news, newsBasicRes);
            newsBasicRes.setAuthor(authorMap.get(news.getAuthor()));
            return newsBasicRes;
        }).collect(Collectors.toList());
        Page<NewsBasicRes> resPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resPage.setRecords(newsBasicList);
        return resPage;
    }

    @Override
    public List<NewsSearchRes> searchNews(String keyword) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.like("title", keyword).orderByAsc("LENGTH(title)");
        List<News> list = newsMapper.selectList(wrapper);//fixme 使用sql搜索
//        List<News> list = searchNewsEs(keyword); //使用es搜索
        List<NewsSearchRes> res = list.stream().map(n -> {
            NewsSearchRes temp = new NewsSearchRes();
            BeanUtils.copyProperties(n, temp);
            return temp;
        }).collect(Collectors.toList());
        return res;
    }

    @Override
    public List<NewsSearchRes> listNewsInIds(List<Integer> ids) {
        if (ids.isEmpty()) return Collections.emptyList();
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.select("id", "title", "cover").in("id", ids).orderByDesc("create_at");
        List<News> newsList = newsMapper.selectList(wrapper);
        List<NewsSearchRes> res = newsList.stream().map(n -> {
            NewsSearchRes temp = new NewsSearchRes();
            BeanUtils.copyProperties(n, temp);
            return temp;
        }).collect(Collectors.toList());
        return res;
    }

    private List<News> searchNewsEs(String keyword) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        MatchQueryBuilder matchQuery1 = QueryBuilders.matchQuery("title", keyword);
        MatchQueryBuilder matchQuery2 = QueryBuilders.matchQuery("text", keyword);
        matchQuery1.boost(2);
        matchQuery2.boost(1);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("status", 1);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(new BoolQueryBuilder().should(matchQuery1).should(matchQuery2));

        builder.query(boolQueryBuilder);
        List<News> resList = esClient.search("jgxq_news", builder, News.class, 10);
        return resList;
    }

    private Page<NewsBasicRes> searchNewsPageEs(Integer pageNum, Integer pageSize, String keyword) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        MatchQueryBuilder matchQuery1 = QueryBuilders.matchQuery("title", keyword);
        MatchQueryBuilder matchQuery2 = QueryBuilders.matchQuery("text", keyword);
        matchQuery1.boost(2);
        matchQuery2.boost(1);

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("status", 1);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(termQueryBuilder);
        boolQueryBuilder.must(new BoolQueryBuilder().should(matchQuery1).should(matchQuery2));

        builder.query(boolQueryBuilder);
        Page<NewsBasicRes> resPage = esClient.search("jgxq_news", builder, NewsBasicRes.class, pageNum, pageSize);
        return resPage;
    }

}
