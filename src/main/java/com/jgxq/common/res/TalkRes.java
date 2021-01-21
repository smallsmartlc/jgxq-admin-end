package com.jgxq.common.res;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-14
 */
@Data
public class TalkRes {

    private Integer id;

    private String author;

    private String text;

    private Date createAt;

}
