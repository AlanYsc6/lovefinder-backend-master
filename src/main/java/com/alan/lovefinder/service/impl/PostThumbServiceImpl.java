package com.alan.lovefinder.service.impl;

import com.alan.lovefinder.exception.BusinessException;
import com.alan.lovefinder.mapper.PostThumbMapper;
import com.alan.lovefinder.service.PostService;
import com.alan.lovefinder.service.PostThumbService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.lovefinder.common.ErrorCode;
import com.alan.lovefinder.model.entity.Post;
import com.alan.lovefinder.model.entity.PostThumb;
import com.alan.lovefinder.model.entity.User;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author alanli
 * @description 针对表【post_thumb(帖子点赞记录)】的数据库操作Service实现
 * @createDate 2022-09-13 16:04:30
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    @Override
    public int doThumb(long postId, User loginUser) {
        // 帖子是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        synchronized (String.valueOf(userId).intern()) {
            return doThumbInner(userId, postId);
        }
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param postId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int doThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>(postThumb);
        long count = this.count(postThumbQueryWrapper);
        boolean result;
        // 已点赞
        if (count > 0) {
            result = this.remove(postThumbQueryWrapper);
            if (result) {
                // 帖子点赞数 - 1
                result = postService.update()
                        .eq("id", postId)
                        .gt("thumbNum", 0)
                        .setSql("thumbNum = thumbNum - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 帖子点赞数 + 1
                result = postService.update()
                        .eq("id", postId)
                        .setSql("thumbNum = thumbNum + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
    }
}




