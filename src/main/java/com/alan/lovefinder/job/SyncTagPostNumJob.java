package com.alan.lovefinder.job;

import com.alan.lovefinder.model.entity.Post;
import com.alan.lovefinder.model.entity.Tag;
import com.alan.lovefinder.service.PostService;
import com.alan.lovefinder.service.TagService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定期同步每个标签的帖子数量
 *
 * @author alan
 */
@Component
@Slf4j
public class SyncTagPostNumJob {

    @Resource
    private PostService postService;

    @Resource
    private TagService tagService;

    /**
     * 每次同步间隔 30 分钟
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void doStart() {
        log.info("SyncTagPostNumJob start");
        // 全量同步
        List<Post> postList = postService.list();
        // 断言 tagName 不能重复
        Map<String, Integer> tagNameCountMap = new HashMap<>();
        postList.forEach(post -> {
            inc(tagNameCountMap, post.getEducation());
            inc(tagNameCountMap, post.getPlace());
            inc(tagNameCountMap, post.getJob());
            inc(tagNameCountMap, post.getLoveExp());
        });
        // 更新 tag
        for (Entry<String, Integer> tagNameCountEntry : tagNameCountMap.entrySet()) {
            UpdateWrapper<Tag> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("tagName", tagNameCountEntry.getKey())
                    .set("postNum", tagNameCountEntry.getValue());
            tagService.update(updateWrapper);
        }
        log.info("SyncTagPostNumJob end");
    }

    /**
     * 值 + 1
     *
     * @param map
     * @param key
     */
    private void inc(Map<String, Integer> map, String key) {
        map.put(key, map.getOrDefault(key, 0) + 1);
    }

}
