package com.jgxq.common.res;

import com.jgxq.common.dto.PlayerInfos;
import lombok.Data;

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
public class PlayerBasicRes{
    private Integer id;
    private String name;
    private String headImage;
    private String nation;
    private Integer number;
    private TeamBasicRes team;
    private String position;
    private Integer age;
    private PlayerInfos infos;
}