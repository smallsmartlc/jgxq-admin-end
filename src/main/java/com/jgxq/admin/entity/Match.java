package com.jgxq.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("`match`")
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 比赛
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 主队
     */
    private Integer homeTeam;

    /**
     * 主队分数
     */
    private Integer homeScore;

    /**
     * 客队
     */
    private Integer visitingTeam;

    /**
     * 客队分数
     */
    private Integer visitingScore;

    /**
     * 比赛时间
     */
    private Date startTime;

    /**
     * news_id
     */
    private Integer matchNews;

    /**
     * 比赛信息,首发,替补
     */
    private String matchInfo;

    /**
     * 直播地址
     */
    private String link;

    /**
     * 事件
     */
    private String action;

    /**
     * 1 -正常 ,-1  -删除
     */
    private Byte status;

    private Date createTime;

    private Date updateTime;


}
