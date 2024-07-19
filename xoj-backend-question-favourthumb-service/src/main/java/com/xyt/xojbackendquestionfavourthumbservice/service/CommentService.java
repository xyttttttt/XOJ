package com.xyt.xojbackendquestionfavourthumbservice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentUpdateThumbRequest;
import com.xyt.xojbackendquestionservice.model.entity.Comment;
import com.xyt.xojbackendquestionservice.model.vo.CommentThumbVO;
import com.xyt.xojbackendquestionservice.model.vo.CommentVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 16048
* @description 针对表【comment(用户)】的数据库操作Service
* @createDate 2023-11-14 19:25:31
*/
public interface CommentService extends IService<Comment> {

    Comment addComment(CommentAddRequest commentSubmitAddRequest, Long userId);

    IPage<Comment> listCommentByPage(Page<Comment> commentIPage, Long questionId);

    Page<CommentVO> getCommentVOPage(IPage<Comment> commentPage, HttpServletRequest request);

    CommentThumbVO changeThumbNum(CommentUpdateThumbRequest commentUpdateThumbRequest, HttpServletRequest request);

    boolean removeCommentById(Long commentId, HttpServletRequest request);
}
