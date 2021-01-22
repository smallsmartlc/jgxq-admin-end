package com.jgxq.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Match;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.res.MatchBasicRes;

import java.util.Date;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
public interface MatchService extends IService<Match> {
    Page<MatchBasicRes> listMatches(Date start, String teamId, Integer pageNum, Integer pageSize);
}
