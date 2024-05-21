package com.alan.lovefinder.service.impl;

import com.alan.lovefinder.mapper.TagMapper;
import com.alan.lovefinder.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alan.lovefinder.model.entity.Tag;
import org.springframework.stereotype.Service;

/**
* @author alanli
* @description 针对表【tag(标签)】的数据库操作Service实现
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService {

}




