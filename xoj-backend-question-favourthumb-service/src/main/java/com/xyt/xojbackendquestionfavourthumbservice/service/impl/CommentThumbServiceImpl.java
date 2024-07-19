package com.xyt.xojbackendquestionfavourthumbservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.xojbackendquestionfavourthumbservice.mapper.CommentThumbMapper;
import com.xyt.xojbackendquestionfavourthumbservice.service.CommentThumbService;
import com.xyt.xojbackendquestionservice.model.entity.CommentThumb;
import org.springframework.stereotype.Service;

/**
* @author 16048
* @description 针对表【comment_thumb(评论点赞)】的数据库操作Service实现
* @createDate 2023-11-16 17:00:24
*/
@Service
public class CommentThumbServiceImpl extends ServiceImpl<CommentThumbMapper, CommentThumb> implements CommentThumbService {


    public boolean isExist(Long commentId,Long userId){
        LambdaQueryWrapper<CommentThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentThumb::getCommentId,commentId);
        wrapper.eq(CommentThumb::getUserId,userId);
        CommentThumb commentThumb = baseMapper.selectOne(wrapper);
        if (commentThumb == null){
            return false;
        }
        return true;
    }
}




