package com.jgxq.admin.service;

import com.jgxq.admin.entity.Player;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.res.PlayerMatchRes;
import com.jgxq.common.res.PlayerSearchRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-19
 */
public interface PlayerService extends IService<Player> {

    List<PlayerSearchRes> searchPlayer(String keyword);
}
