package com.alan.lovefinder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.alan.lovefinder.model.entity.TagSearchHistory;

/**
 * @author alanli
 * @description 针对表【tag_search_history(标签筛选记录)】的数据库操作Service
 */
public interface TagSearchHistoryService extends IService<TagSearchHistory> {

    /**
     * 添加标签搜索记录
     *
     * @param tagName
     * @return
     */
    boolean addTagSearchHistory(String tagName);
}
