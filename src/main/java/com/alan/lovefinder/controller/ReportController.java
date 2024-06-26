package com.alan.lovefinder.controller;

import com.alan.lovefinder.annotation.AuthCheck;
import com.alan.lovefinder.common.BaseResponse;
import com.alan.lovefinder.common.DeleteRequest;
import com.alan.lovefinder.common.ErrorCode;
import com.alan.lovefinder.common.ResultUtils;
import com.alan.lovefinder.constant.CommonConstant;
import com.alan.lovefinder.exception.BusinessException;
import com.alan.lovefinder.model.dto.ReportAddRequest;
import com.alan.lovefinder.model.dto.ReportQueryRequest;
import com.alan.lovefinder.model.dto.ReportUpdateRequest;
import com.alan.lovefinder.model.entity.Post;
import com.alan.lovefinder.model.entity.Report;
import com.alan.lovefinder.model.entity.User;
import com.alan.lovefinder.model.enums.ReportStatusEnum;
import com.alan.lovefinder.service.PostService;
import com.alan.lovefinder.service.ReportService;
import com.alan.lovefinder.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 举报接口
 *
 * @author alan
 */
@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    @Resource
    private ReportService reportService;

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param reportAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addReport(@RequestBody ReportAddRequest reportAddRequest, HttpServletRequest request) {
        if (reportAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Report report = new Report();
        BeanUtils.copyProperties(reportAddRequest, report);
        reportService.validReport(report, true);
        User loginUser = userService.getLoginUser(request);
        Post post = postService.getById(report.getReportedId());
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "举报对象不存在");
        }
        report.setReportedUserId(post.getUserId());
        report.setUserId(loginUser.getId());
        report.setStatus(ReportStatusEnum.DEFAULT.getValue());
        boolean result = reportService.save(report);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        long newReportId = report.getId();
        return ResultUtils.success(newReportId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteReport(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Report oldReport = reportService.getById(id);
        if (oldReport == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldReport.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = reportService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param reportUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateReport(@RequestBody ReportUpdateRequest reportUpdateRequest, HttpServletRequest request) {
        if (reportUpdateRequest == null || reportUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Report report = new Report();
        BeanUtils.copyProperties(reportUpdateRequest, report);
        reportService.validReport(report, false);
        User user = userService.getLoginUser(request);
        long id = reportUpdateRequest.getId();
        // 判断是否存在
        Report oldReport = reportService.getById(id);
        if (oldReport == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldReport.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = reportService.updateById(report);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Report> getReportById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Report report = reportService.getById(id);
        return ResultUtils.success(report);
    }

    /**
     * 获取列表（仅管理员可使用）
     *
     * @param reportQueryRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<Report>> listReport(ReportQueryRequest reportQueryRequest) {
        Report reportQuery = new Report();
        if (reportQueryRequest != null) {
            BeanUtils.copyProperties(reportQueryRequest, reportQuery);
        }
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>(reportQuery);
        List<Report> reportList = reportService.list(queryWrapper);
        return ResultUtils.success(reportList);
    }

    /**
     * 分页获取列表
     *
     * @param reportQueryRequest
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Report>> listReportByPage(ReportQueryRequest reportQueryRequest) {
        if (reportQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Report reportQuery = new Report();
        BeanUtils.copyProperties(reportQueryRequest, reportQuery);
        long current = reportQueryRequest.getCurrent();
        long size = reportQueryRequest.getPageSize();
        String sortField = reportQueryRequest.getSortField();
        String sortOrder = reportQueryRequest.getSortOrder();
        String content = reportQuery.getContent();
        // content 需支持模糊搜索
        reportQuery.setContent(null);
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>(reportQuery);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Report> reportPage = reportService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(reportPage);
    }

    // endregion
}
