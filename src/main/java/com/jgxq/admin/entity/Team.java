package com.jgxq.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 球队名
     */
    private String name;

    /**
     * 英文名
     */
    private String enName;

    private String logo;

    private String infos;

    /**
     * 1 正常,-1 已删除
     */
    private Byte status;

    private Date createAt;

    private Date updateAt;


}
