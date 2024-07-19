package com.xyt.xojbackendquestionfavourthumbservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendjudgeservice.utils.WordTreeUtils;
import com.xyt.xojbackendquestionfavourthumbservice.mapper.CommentMapper;
import com.xyt.xojbackendquestionfavourthumbservice.service.CommentService;
import com.xyt.xojbackendquestionfavourthumbservice.service.CommentThumbService;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentUpdateThumbRequest;
import com.xyt.xojbackendquestionservice.model.entity.Comment;
import com.xyt.xojbackendquestionservice.model.entity.CommentThumb;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.vo.CommentThumbVO;
import com.xyt.xojbackendquestionservice.model.vo.CommentVO;
import com.xyt.xojbackendquestionservice.model.vo.UserVO;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 16048
* @description 针对表【comment(用户)】的数据库操作Service实现
* @createDate 2023-11-14 19:25:30
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private CommentThumbService commentThumbService;
    @Resource
    private Redisson redisson;

    @Override
    public Comment addComment(CommentAddRequest commentSubmitAddRequest, Long userId) {
        Comment comment = new Comment();

        Long questionId = commentSubmitAddRequest.getQuestionId();
        String content = commentSubmitAddRequest.getComment();
        boolean matchInText = WordTreeUtils.isMatchInText(content);
        if (matchInText){
            throw new BusinessException(ErrorCode.COMMENT_INPUT_ERROR,"评论内容不当");
        }
        comment.setComment(content);
        comment.setQuestionId(questionId);
        comment.setUserId(userId);
        boolean save = this.save(comment);
        if (!save){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"评论失败");
        }
        return comment;
    }

    @Override
    public IPage<Comment> listCommentByPage(Page<Comment> commentIPage, Long questionId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getQuestionId,questionId);
        wrapper.eq(Comment::getParentCommentId,0);
        wrapper.orderByDesc(Comment::getThumbNum);
        wrapper.orderByDesc(Comment::getCreateTime);
        IPage<Comment> selectPage = baseMapper.selectPage(commentIPage, wrapper);
        return selectPage;
    }

    @Override
    public Page<CommentVO> getCommentVOPage(IPage<Comment> commentPage, HttpServletRequest request) {
        List<Comment> commentList = commentPage.getRecords();
        Page<CommentVO> commentVOPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        if (CollectionUtils.isEmpty(commentList)) {
            return commentVOPage;
        }
        List<CommentVO> commentVOList = commentList.stream().map(comment -> {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);


            User loginUser = userFeignClient.getLoginUser(request);
            if (loginUser==null){
                throw new BusinessException(ErrorCode.OPERATION_ERROR,"用户不存在");
            }
            UserVO userVO = new UserVO();
            Long userId = comment.getUserId();
            User user = userFeignClient.getById(userId);
            BeanUtils.copyProperties(user, userVO);
            commentVO.setUserVO(userVO);
            boolean exist = commentThumbService.isExist(comment.getId(), loginUser.getId());
            commentVO.setLike(exist);
            return commentVO;
        }).collect(Collectors.toList());
        commentVOPage.setRecords(commentVOList);
        return commentVOPage;
    }

    @Override
    public CommentThumbVO changeThumbNum(CommentUpdateThumbRequest commentUpdateThumbRequest, HttpServletRequest request) {
        Long commentId = commentUpdateThumbRequest.getId();
        if (commentId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"点赞失败，参数异常");
        }
        //评论表
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId,commentId);
        //评论点赞记录表
        User loginUser = userFeignClient.getLoginUser(request);
        Long userId = loginUser.getId();
        LambdaQueryWrapper<CommentThumb> thumbWrapper = new LambdaQueryWrapper<>();
        thumbWrapper.eq(CommentThumb::getCommentId,commentId);
        thumbWrapper.eq(CommentThumb::getUserId,userId);
        RLock lock = redisson.getLock("comment-add-thumbNum");
        boolean success = true;
        int currentThumbNum = 0;
        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)){
                try {
                    CommentThumb commentThumb  = commentThumbService.getOne(thumbWrapper);
                    Comment comment = baseMapper.selectById(commentId);
                    if (commentThumb == null){
                        commentThumb = new CommentThumb();
                        comment.setThumbNum(comment.getThumbNum() + 1);
                        commentThumb.setCommentId(commentId);
                        commentThumb.setUserId(userId);
                        commentThumbService.save(commentThumb);
                    }else {
                        comment.setThumbNum(comment.getThumbNum() - 1);
                        commentThumbService.removeById(commentThumb);
                        success = false;
                    }
                    currentThumbNum = comment.getThumbNum();
                    int updateById = baseMapper.updateById(comment);
                    if (updateById == 0){
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR,"点赞失败");
                    }
                }finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CommentThumbVO commentThumbVO = new CommentThumbVO();
        commentThumbVO.setThumbNum(currentThumbNum);
        commentThumbVO.setStatus(success);
        return commentThumbVO;
    }

    @Override
    public boolean removeCommentById(Long commentId, HttpServletRequest request) {

        User loginUser = userFeignClient.getLoginUser(request);
        Comment comment = baseMapper.selectById(commentId);
        if (loginUser ==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (!loginUser.getId().equals(comment.getUserId())  && !userFeignClient.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        int delete = baseMapper.deleteById(commentId);
        if (delete == 0){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        LambdaQueryWrapper<CommentThumb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentThumb::getCommentId,commentId);
        commentThumbService.remove(wrapper);
        return true;
    }
}




