package com.jgxq.admin.service.impl;

import com.jgxq.admin.entity.News;
import com.jgxq.admin.mapper.NewsMapper;
import com.jgxq.admin.service.NewsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
