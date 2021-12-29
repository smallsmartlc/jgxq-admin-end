package com.jgxq.admin.controller;

import com.jgxq.common.define.ForumErrorCode;
import com.jgxq.common.res.PlaySpiderRes;
import com.jgxq.common.res.PlayerSpiderDto;
import com.jgxq.core.exception.SmartException;
import com.jgxq.core.resp.ResponseMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LuCong
 * @since 2021-12-28
 **/
@RestController
@RequestMapping("/spider")
public class SpiderController {

    @GetMapping("/ing/player")
    public ResponseMessage getPlayerFromIng(@RequestParam Integer matchId) {
        String url = "https://wechat.ingsoccer.com.cn/web/matches/"+matchId;
        List<PlayerSpiderDto> homeList = new ArrayList<>();
        List<PlayerSpiderDto> subList = new ArrayList<>();
        try {
            Document document = Jsoup.parse(new URL(url), 10000);
            // document对象
            Element lineup = document.getElementById("lineup");
            Element leftElement = lineup.getElementsByClass("float-left").last();
            Elements leftElemets = leftElement.getElementsByClass("lineup-player");
            for (Element element : leftElemets) {
                String name = element.getElementsByClass("lineup-name").eq(0).text();
                if(name.equals("替补")) continue;
                String number = element.getElementsByClass("lineup-number").get(0)
                        .getElementsByTag("div").eq(0).text();
                homeList.add(new PlayerSpiderDto(name,Integer.parseInt(number)));
            }
            Element rightElement = lineup.getElementsByClass("float-right").last();
            Elements rightElements = rightElement.getElementsByClass("lineup-player");
            for (Element element : rightElements) {
                String name = element.getElementsByClass("lineup-name").eq(0).text();
                if(name.equals("替补")) continue;
                String number = element.getElementsByClass("lineup-number").get(0)
                        .getElementsByTag("div").eq(0).text();
                subList.add(new PlayerSpiderDto(name,Integer.parseInt(number)));
            }
        } catch (IOException e) {
            throw new SmartException(ForumErrorCode.Service_Request_Fail.getErrorCode(),"无法获取该比赛数据");
        }
        return new ResponseMessage(new PlaySpiderRes(homeList,subList));
    }
}
