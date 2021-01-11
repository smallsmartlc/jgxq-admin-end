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
 * @since 2021-01-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 管理员对外唯一标识
     */
    private String adminKey;

    /**
     * 昵称
     */
    private String nickName;

    private String password;

    private String adminName;

    private Integer roleId;
    /**
     * 1 -1 dddd
     */
    private Byte status;

    private Date createAt;

    private Date updateAt;


}
