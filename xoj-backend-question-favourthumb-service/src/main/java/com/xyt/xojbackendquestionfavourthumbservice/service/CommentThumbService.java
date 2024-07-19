package com.xyt.xojbackendquestionfavourthumbservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.xojbackendquestionservice.model.entity.CommentThumb;

/**
* @author 16048
* @description 针对表【comment_thumb(评论点赞)】的数据库操作Service
* @createDate 2023-11-16 17:00:24
*/
public interface CommentThumbService extends IService<CommentThumb> {
    boolean isExist(Long commentId,Long userId);
}
