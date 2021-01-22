package com.jgxq.common.utils;

import com.alibaba.fastjson.JSON;
import com.jgxq.admin.entity.Match;
import com.jgxq.common.dto.Action;
import com.jgxq.common.dto.MatchInfo;
import com.jgxq.common.res.MatchRes;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author LuCong
 * @since 2020-12-11
 **/
@Component
public class ResUtils {

    public static MatchRes matchToMatchRes(Match match) {

        List<Action> actions = JSON.parseArray(match.getAction(), Action.class);
        MatchInfo matchInfo = JSON.parseObject(match.getMatchInfo(), MatchInfo.class);
        MatchRes matchRes = new MatchRes();
        BeanUtils.copyProperties(match, matchRes);
        matchRes.setMatchInfo(matchInfo);
        matchRes.setActions(actions);
        return matchRes;
    }
}
