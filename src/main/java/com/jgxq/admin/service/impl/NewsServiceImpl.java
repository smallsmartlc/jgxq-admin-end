package com.jgxq.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.News;
import com.jgxq.admin.entity.Player;
import com.jgxq.admin.mapper.NewsMapper;
import com.jgxq.admin.service.NewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jgxq.common.res.NewsBasicRes;
import com.jgxq.common.res.NewsSearchRes;
import com.jgxq.common.res.PlayerMatchRes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public Page<NewsBasicRes> pageNews(Integer pageNum, Integer pageSize) {
        Page<News> page = new Page<>(pageNum, pageSize);
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.select("id", "title", "cover","author","create_at").orderByDesc("create_at");
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
    public List<NewsSearchRes> searchPlayer(String keyword) {
        QueryWrapper<News> wrapper = new QueryWrapper<>();
        wrapper.like("title",keyword).orderByAsc("LENGTH(title)");
        List<News> list = newsMapper.selectList(wrapper);
        List<NewsSearchRes> res = list.stream().map(n -> {
            NewsSearchRes temp = new NewsSearchRes();
            BeanUtils.copyProperties(n, temp);
            return temp;
        }).collect(Collectors.toList());
        return res;
    }
    public List<NewsSearchRes> searchPlayerEs(String keyword) {
        //TODO 使用es搜索
        return null;
    }

}
