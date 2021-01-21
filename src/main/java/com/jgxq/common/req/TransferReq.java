package com.jgxq.common.req;

import lombok.Data;

/**
 * @author LuCong
 * @since 2021-01-21
 **/
@Data
public class TransferReq {
    private Integer sourceTeam;
    private Integer targetTeam;
    private Integer playerId;
}
