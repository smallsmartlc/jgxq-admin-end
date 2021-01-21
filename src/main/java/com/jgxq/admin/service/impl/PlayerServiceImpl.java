package com.jgxq.admin.service.impl;

import com.jgxq.admin.entity.Player;
import com.jgxq.admin.mapper.PlayerMapper;
import com.jgxq.admin.service.PlayerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
