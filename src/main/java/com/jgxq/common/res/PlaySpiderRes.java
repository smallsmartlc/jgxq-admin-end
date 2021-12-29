package com.jgxq.common.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LuCong
 * @since 2021-12-29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaySpiderRes {
    List<PlayerSpiderDto> homeList;
    List<PlayerSpiderDto> subList;
}
