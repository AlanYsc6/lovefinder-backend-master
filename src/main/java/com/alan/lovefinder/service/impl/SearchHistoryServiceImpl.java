package com.alan.lovefinder.service.impl;

import com.alan.lovefinder.exception.BusinessException;
import com.alan.lovefinder.mapper.SearchHistoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.lovefinder.common.ErrorCode;
import com.alan.lovefinder.model.entity.SearchHistory;
import com.alan.lovefinder.service.SearchHistoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author alanli
 * @description 针对表【search_history(搜索记录)】的数据库操作Service实现
 */
@Service
public class SearchHistoryServiceImpl extends ServiceImpl<SearchHistoryMapper, SearchHistory>
        implements SearchHistoryService {

    @Override
    public boolean addSearchHistory(String word) {
        if (StringUtils.isEmpty(word)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("word", word);
        synchronized (word.intern()) {
            SearchHistory searchHistory = this.getOne(queryWrapper);
            // 无记录则插入
            if (searchHistory == null) {
                searchHistory = new SearchHistory();
                searchHistory.setWord(word);
                searchHistory.setNum(1);
                return this.save(searchHistory);
            } else {
                // 有记录则次数 + 1
                return this.lambdaUpdate()
                        .eq(SearchHistory::getWord, word)
                        .set(SearchHistory::getNum, searchHistory.getNum() + 1)
                        .update();
            }
        }
    }
}




