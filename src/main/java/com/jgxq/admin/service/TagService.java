package com.jgxq.admin.service;

import com.jgxq.admin.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jgxq.common.res.TagSearchRes;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author smallsmart
 * @since 2021-01-21
 */
public interface TagService extends IService<Tag> {
    List<TagSearchRes> searchTag(String keyword);
}
