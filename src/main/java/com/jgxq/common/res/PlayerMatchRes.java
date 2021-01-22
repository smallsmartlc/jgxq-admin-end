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
public class PlayerMatchRes {
    private Integer id;
    private String name;
    private Integer number;
    private Integer matchPos;
}