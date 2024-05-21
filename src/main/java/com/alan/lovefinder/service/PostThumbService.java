package com.alan.lovefinder.service;

import com.alan.lovefinder.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.alan.lovefinder.model.entity.User;

/**
 * @author alanli
 * @description 针对表【post_thumb(帖子点赞记录)】的数据库操作Service
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞 / 取消点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doThumb(long postId, User loginUser);
}
