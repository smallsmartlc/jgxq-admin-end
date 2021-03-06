package com.jgxq.common.req;

import com.jgxq.common.dto.PlayerInfos;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerReq {

    @NotBlank
    private String name;

    @NotBlank
    private String enName;

    private String headImage;

    private Integer height;

    /**
     * 体重-公斤
     */
    private Integer weight;

    /**
     * 国籍
     */
    private String nation;

    /**
     * 号码
     */
    @NotNull
    private Integer number;

    private Integer team;

    /**
     * 惯用脚 0右脚,1左脚,2双脚
     */
    @NotNull
    private Byte strongFoot;

    /**
     * 位置 门将0,后卫1,中场2,前锋3
     */
    @NotNull
    private Byte position;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 基本信息
     */
    private PlayerInfos infos;

}
