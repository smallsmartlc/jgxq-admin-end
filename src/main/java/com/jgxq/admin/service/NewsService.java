package com.jgxq.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.News;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.res.NewsBasicRes;
import com.jgxq.common.res.NewsSearchRes;
import com.jgxq.common.res.PlayerMatchRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
public interface NewsService extends IService<News> {
    Page<NewsBasicRes> pageNews(Integer pageNum, Integer pageSize);

    List<NewsSearchRes> searchPlayer(String keyword);

    List<NewsSearchRes> listNewsInIds(List<Integer> ids);
}
