package com.jgxq.common.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author LuCong
 * @since 2020-12-07
 **/
@Data
public class AdminUpdateReq {

    private String nickName;

    private String adminName;

    private Integer roleId;

}