package com.alan.lovefinder.controller;

import com.alan.lovefinder.annotation.AuthCheck;
import com.alan.lovefinder.common.BaseResponse;
import com.alan.lovefinder.common.DeleteRequest;
import com.alan.lovefinder.common.ErrorCode;
import com.alan.lovefinder.common.ResultUtils;
import com.alan.lovefinder.exception.BusinessException;
import com.alan.lovefinder.model.dto.TagQueryRequest;
import com.alan.lovefinder.model.entity.Tag;
import com.alan.lovefinder.model.entity.User;
import com.alan.lovefinder.model.enums.TagCategoryEnum;
import com.alan.lovefinder.service.TagService;
import com.alan.lovefinder.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签接口
 *
 * @author alan
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Resource
    private TagService tagService;

    @Resource
    private UserService userService;

    // TagMap 缓存
    private final Cache<String, Map<String, List<Tag>>> tagMapCache = Caffeine.newBuilder().build();

    // 整个 TagMap 缓存 key
    private static final String FULL_TAG_MAP_KEY = "f";

    /**
     * 创建
     *
     * @param tag
     * @param request
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/add")
    public BaseResponse<Long> addTag(@RequestBody Tag tag, HttpServletRequest request) {
        if (tag == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String tagName = tag.getTagName();
        String category = tag.getCategory();
        if (StringUtils.isAllBlank(tagName, category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        tagService.count();
        User loginUser = userService.getLoginUser(request);
        tag.setUserId(loginUser.getId());
        boolean result = tagService.save(tag);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 清除缓存
        tagMapCache.invalidate(FULL_TAG_MAP_KEY);
        return ResultUtils.success(tag.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTag(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        boolean b = tagService.removeById(id);
        // 清除缓存
        tagMapCache.invalidate(FULL_TAG_MAP_KEY);
        return ResultUtils.success(b);
    }

    /**
     * 获取所有标签分组
     *
     * @return
     */
    @GetMapping("/get/map")
    public BaseResponse<Map<String, List<Tag>>> getTagMap() {
        List<Tag> tagList = tagService.list();
        if (tagList == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 优先取缓存
        Map<String, List<Tag>> tagMap = tagMapCache.get(FULL_TAG_MAP_KEY, key -> tagList.stream().map(tag -> {
            // 精简
            Tag newTag = new Tag();
            newTag.setTagName(tag.getTagName());
            newTag.setCategory(tag.getCategory());
            newTag.setPostNum(tag.getPostNum());
            return newTag;
            // 按类别分组
        }).collect(Collectors.groupingBy(Tag::getCategory)));
        return ResultUtils.success(tagMap);
    }

    /**
     * 分页查询标签（仅管理员可见）
     *
     * @return
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<Tag>> listTagByPage(TagQueryRequest tagQueryRequest) {
        if (tagQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = tagQueryRequest.getCurrent();
        long size = tagQueryRequest.getPageSize();
        String category = tagQueryRequest.getCategory();
        String tagName = tagQueryRequest.getTagName();
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.eq("category", category);
        }
        if (StringUtils.isNotBlank(tagName)) {
            queryWrapper.like("tagName", tagName);
        }
        // 默认按帖子使用数降序排序
        queryWrapper.orderByDesc("postNum");
        Page<Tag> tagPage = tagService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(tagPage);
    }

    /**
     * 分页查询标签（仅管理员可见）
     *
     * @return
     */
    @GetMapping("/category/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<String>> listTagCategory() {
        return ResultUtils.success(TagCategoryEnum.getValues());
    }

}
