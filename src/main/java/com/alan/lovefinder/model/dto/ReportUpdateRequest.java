package com.alan.lovefinder.model.dto;

import java.io.Serializable;
import lombok.Data;

/**
 * 更新请求
 *
 * @TableName report
 */
@Data
public class ReportUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态（0-未处理, 1-已处理）
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}