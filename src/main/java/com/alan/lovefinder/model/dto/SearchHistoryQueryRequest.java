package com.alan.lovefinder.model.dto;

import com.alan.lovefinder.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 * @author alan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchHistoryQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索关键词
     */
    private String word;

    private static final long serialVersionUID = 1L;
}