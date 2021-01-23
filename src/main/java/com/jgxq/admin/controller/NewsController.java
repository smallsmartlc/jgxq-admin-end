package com.jgxq.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.client.RedisCache;
import com.jgxq.admin.entity.News;
import com.jgxq.admin.entity.Tag;
import com.jgxq.admin.service.NewsService;
import com.jgxq.admin.service.impl.TagServiceImpl;
import com.jgxq.admin.service.impl.UserServiceImpl;
import com.jgxq.common.req.NewsReq;
import com.jgxq.common.res.*;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.enums.CommonErrorCode;
import com.jgxq.core.enums.RedisKeys;
import com.jgxq.core.resp.ResponseMessage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-12
 */
@RestController
@RequestMapping("/news")
@UserPermissionConf
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TagServiceImpl tagService;

    @Autowired
    private RedisCache redisCache;

    @PutMapping("{id}")
    @Transactional
    public ResponseMessage updateNews(@PathVariable("id") Integer id,
                                      @RequestBody @Validated NewsReq newsReq) {
        News news = new News();
        BeanUtils.copyProperties(newsReq, news);
        UpdateWrapper<News> newsUpdate = new UpdateWrapper<>();
        newsUpdate.eq("id",id);
        boolean flag = newsService.update(news,newsUpdate);
        if(flag){
            QueryWrapper<Tag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("news_id", id);
            tagService.remove(tagQuery);
            List<Tag> tagList = newsReq.getTags().stream().map(t -> {
                Tag tag = new Tag();
                BeanUtils.copyProperties(t, tag);
                tag.setObjectType(t.getType().byteValue());
                tag.setNewsId(id);
                return tag;
            }).collect(Collectors.toList());
            tagService.saveBatch(tagList);
        }
        return new ResponseMessage(flag);
    }

    @DeleteMapping("{id}")
    public ResponseMessage deleteNews(@PathVariable("id") Integer id) {
        UpdateWrapper<News> newsUpdate = new UpdateWrapper<>();
        newsUpdate.eq("id",id);
        boolean flag = newsService.remove(newsUpdate);
        QueryWrapper<Tag> tagQuery = new QueryWrapper<>();
        tagQuery.eq("news_id", id);
        tagService.remove(tagQuery);
        return new ResponseMessage(flag);
    }

    @GetMapping("{id}")
    public ResponseMessage getNews(@PathVariable("id") Integer id) {
        QueryWrapper<News> newsQuery = new QueryWrapper<>();
        newsQuery.eq("id", id);
        News news = newsService.getOne(newsQuery);
        if (news == null) {
            return new ResponseMessage(CommonErrorCode.BAD_PARAMETERS.getErrorCode(), "没有该记录");
        }
        String author = userService.getAuthorInfo(news.getAuthor());
        List<TagSearchRes> tagList = tagService.getTagList(id);
        NewsRes newsRes = new NewsRes();
        BeanUtils.copyProperties(news, newsRes);
        newsRes.setAuthor(author);
        newsRes.setTags(tagList);

        return new ResponseMessage(newsRes);
    }


    @GetMapping("page/{pageNum}/{pageSize}")
    public ResponseMessage pageNews(@PathVariable("pageNum") Integer pageNum,
                                    @PathVariable("pageSize") Integer pageSize) {
        Page<NewsBasicRes> list = newsService.pageNews(pageNum, pageSize);
        return new ResponseMessage(list);
    }

    @GetMapping("top")
    public ResponseMessage topNews() {
        List<Integer> ids = redisCache.lrangeInt(RedisKeys.top_news.getKey());
        List<NewsSearchRes> res = newsService.listNewsInIds(ids);
        return new ResponseMessage(res);
    }
    @PostMapping("top/{id}")
    public ResponseMessage addTop(@PathVariable("id") Integer id) {
        Long flag = redisCache.lPush(RedisKeys.top_news.getKey(), id);
        return new ResponseMessage(flag>0);
    }
    @DeleteMapping("top/{id}")
    public ResponseMessage deleteTop(@PathVariable("id") Integer id) {
        Long flag = redisCache.lRem(RedisKeys.top_news.getKey(), id);
        return new ResponseMessage(flag>0);
    }

    @GetMapping("search")
    public ResponseMessage searchNews(@RequestParam("keyword") String keyword){
        List<NewsSearchRes> list = newsService.searchPlayer(keyword);
        return new ResponseMessage(list);
    }

}
