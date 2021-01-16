package com.jgxq.common.req;

import com.jgxq.common.dto.TeamInfos;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeamReq {

    /**
     * 球队名
     */
    @NotBlank
    private String name;

    /**
     * 英文名
     */
    @NotBlank
    private String enName;

    @NotBlank
    private String logo;

    private TeamInfos infos;

}
