package com.xyt.xojbackendquestionfavourthumbservice.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.oxibackendserviceclient.service.UserFeignClient;
import com.xyt.xojbackendjudgeservice.common.BaseResponse;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.common.ResultUtils;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionfavourthumbservice.service.CommentService;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentDeleteRequest;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentQueryRequest;
import com.xyt.xojbackendquestionservice.model.dto.comment.CommentUpdateThumbRequest;
import com.xyt.xojbackendquestionservice.model.entity.Comment;
import com.xyt.xojbackendquestionservice.model.entity.User;
import com.xyt.xojbackendquestionservice.model.vo.CommentThumbVO;
import com.xyt.xojbackendquestionservice.model.vo.CommentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
@Slf4j
public class CommentController {

    @Resource
    private CommentService commentService;

    @Resource
    private UserFeignClient userFeignClient;

    @PostMapping("/add/comment")
    public BaseResponse<Comment> addComment(@RequestBody CommentAddRequest commentAddRequest, HttpServletRequest request) {
        User loginUser = userFeignClient.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (commentAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = loginUser.getId();
        Comment comment = commentService.addComment(commentAddRequest, userId);

        return ResultUtils.success(comment);
    }

    @PostMapping("/getCommentByPage")
    public BaseResponse<Page<CommentVO>> listCommentVOByPage(@RequestBody CommentQueryRequest commentQueryRequest
            , HttpServletRequest request) {

        System.out.println(commentQueryRequest);
        if (commentQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = commentQueryRequest.getCurrent();
        long size = commentQueryRequest.getPageSize();
        Long questionId = commentQueryRequest.getQuestionId();
        if (questionId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<Comment> commentIPage = new Page<>(current, size);
        IPage<Comment> commentPage = commentService.listCommentByPage(commentIPage, questionId);
        return ResultUtils.success(commentService.getCommentVOPage(commentPage, request));
    }

    //todo redis 做缓存
    @PostMapping("/changeThumbNum")
    public BaseResponse<CommentThumbVO> changeThumbNum(@RequestBody CommentUpdateThumbRequest commentUpdateThumbRequest
            , HttpServletRequest request) {
        if (commentUpdateThumbRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        CommentThumbVO commentThumbVO = commentService.changeThumbNum(commentUpdateThumbRequest, request);
        if (commentThumbVO == null) {
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(commentThumbVO);
    }

    @PostMapping("removeCommentById")
    public BaseResponse<Boolean> removeCommentById(@RequestBody CommentDeleteRequest commentDeleteRequest
                                                    , HttpServletRequest request){
        if (commentDeleteRequest ==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        Long commentId = commentDeleteRequest.getId();
        boolean remove = commentService.removeCommentById(commentId,request);
        return ResultUtils.success(remove);
    }
}
