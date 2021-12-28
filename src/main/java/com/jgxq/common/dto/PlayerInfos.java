package com.jgxq.common.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author LuCong
 * @since 2021-01-03
 **/
@Data
@NoArgsConstructor
public class PlayerInfos {
    private List<PlayerInfo> normal;

    public PlayerInfos(List<PlayerInfo> normal) {
        this.normal = normal;
    }
}
