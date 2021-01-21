package com.jgxq.common.res;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author smallsmart
 * @since 2020-12-12
 */
@Data
public class NewsBasicRes {

    private Integer id;
    private String title;

    private String cover;

    private Integer comments;

    private String author;

    private Date createAt;
}
