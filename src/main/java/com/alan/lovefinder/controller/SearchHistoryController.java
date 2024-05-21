package com.alan.lovefinder.controller;

import com.alan.lovefinder.annotation.AuthCheck;
import com.alan.lovefinder.common.BaseResponse;
import com.alan.lovefinder.common.DeleteRequest;
import com.alan.lovefinder.common.ErrorCode;
import com.alan.lovefinder.common.ResultUtils;
import com.alan.lovefinder.exception.BusinessException;
import com.alan.lovefinder.model.dto.SearchHistoryQueryRequest;
import com.alan.lovefinder.model.entity.SearchHistory;
import com.alan.lovefinder.service.SearchHistoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查看搜索记录接口
 *
 * @author alan
 */
@RestController
@RequestMapping("/search_history")
public class SearchHistoryController {

    @Resource
    private SearchHistoryService searchHistoryService;

    /**
     * 创建
     *
     * @param tagSearchHistory
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addSearchHistory(@RequestBody SearchHistory tagSearchHistory) {
        if (tagSearchHistory == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String word = tagSearchHistory.getWord();
        return ResultUtils.success(searchHistoryService.addSearchHistory(word));
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSearchHistory(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        boolean b = searchHistoryService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 分页获取列表
     *
     * @param searchHistoryQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list/page")
    public BaseResponse<Page<SearchHistory>> listSearchHistoryByPage(
            SearchHistoryQueryRequest searchHistoryQueryRequest) {
        long current = 1;
        long size = 10;
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();
        if (searchHistoryQueryRequest != null) {
            // 根据标签名称模糊查询
            String word = searchHistoryQueryRequest.getWord();
            if (StringUtils.isNotBlank(word)) {
                queryWrapper.like("word", word);
            }
        }
        Page<SearchHistory> searchHistoryPage = searchHistoryService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(searchHistoryPage);
    }

}
