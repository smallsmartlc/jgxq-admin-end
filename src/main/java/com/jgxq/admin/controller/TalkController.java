package com.jgxq.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jgxq.admin.entity.Talk;
import com.jgxq.admin.entity.User;
import com.jgxq.admin.service.TalkService;
import com.jgxq.admin.service.impl.UserServiceImpl;
import com.jgxq.common.res.TalkRes;
import com.jgxq.core.anotation.AllowAccess;
import com.jgxq.core.anotation.RolePermissionConf;
import com.jgxq.core.anotation.UserPermissionConf;
import com.jgxq.core.resp.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-14
 */
@RestController
@RequestMapping("/talk")
@UserPermissionConf
public class TalkController {

    @Autowired
    private TalkService talkService;

    @Autowired
    private UserServiceImpl userService;

    @RolePermissionConf("0902")
    @PostMapping
    public ResponseMessage addTalk(@RequestParam String text,
                                   @RequestAttribute("userKey") String userKey) {
        Talk talk = new Talk();
        talk.setAuthor(userKey);
        talk.setText(text);
        talkService.save(talk);
        return new ResponseMessage(talk.getId());
    }

    @RolePermissionConf("0903")
    @DeleteMapping("{id}")
    public ResponseMessage deleteTalk(@PathVariable("id") Integer id,
                                      @RequestAttribute("userKey") String userKey) {
        QueryWrapper<Talk> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id)
                .eq("author", userKey);
        boolean flag = talkService.remove(wrapper);

        return new ResponseMessage(flag);
    }

    @RolePermissionConf("0900")
    @GetMapping("{id}")
    public ResponseMessage getTalk(@PathVariable Integer id,
                                   @RequestAttribute("userKey") String userKey) {

        Talk talk = talkService.getById(id);
        if(talk == null) return new ResponseMessage(null);
        User user = userService.getUserByPK("userkey", talk.getAuthor());
        TalkRes talkRes = new TalkRes();
        BeanUtils.copyProperties(talk, talkRes);
        talkRes.setAuthor(user.getNickName());
        return new ResponseMessage(talkRes);
    }

    @RolePermissionConf("0901")
    @GetMapping("/page/{pageNum}/{pageSize}")
    @AllowAccess
    public ResponseMessage pageTalk(@PathVariable("pageNum") Integer pageNum,
                                    @PathVariable("pageSize") Integer pageSize){
        Page<Talk> talkPage = new Page<>(pageNum, pageSize);

        QueryWrapper<Talk> talkQuery = new QueryWrapper<>();
        talkQuery.orderByDesc("create_at");

        talkService.page(talkPage, talkQuery);

        List<Talk> records = talkPage.getRecords();
        List<Integer> ids = records.stream().map(Talk::getId).collect(Collectors.toList());
        Set<String> userKeys = records.stream().map(Talk::getAuthor).collect(Collectors.toSet());
        List<User> userBasicList = userService.getUserByKeyList(userKeys);
        Map<String, String> map = userBasicList.stream().collect(Collectors.toMap(User::getUserkey, User::getNickName));
        List<TalkRes> resList = records.stream().map(t -> {
            TalkRes talkBasicRes = new TalkRes();
            BeanUtils.copyProperties(t, talkBasicRes);
            talkBasicRes.setAuthor(map.get(t.getAuthor()));
            talkBasicRes.setText(StringUtils.abbreviate(t.getText().replaceAll("<img.*?(?:>|\\/>)","[图片]").replaceAll("<[^>]+>|&[^>]+;",""), 60));
            return talkBasicRes;
        }).collect(Collectors.toList());
        Page<TalkRes> resPage = new Page<>(talkPage.getCurrent(), talkPage.getSize(), talkPage.getTotal());
        resPage.setRecords(resList);
        return new ResponseMessage(resPage);
    }

}
