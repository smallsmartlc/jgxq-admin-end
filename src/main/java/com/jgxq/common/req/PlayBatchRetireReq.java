package com.jgxq.common.req;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author LuCong
 * @since 2021-12-27
 **/
@Data
public class PlayBatchRetireReq {

    @NotNull
    private Integer teamId;

    @Size(min = 1,message = "列表不能为空")
    private List<Integer> playerIdList;


}
