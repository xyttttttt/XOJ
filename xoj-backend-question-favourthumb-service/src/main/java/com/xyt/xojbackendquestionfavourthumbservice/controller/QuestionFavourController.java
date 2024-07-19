package com.xyt.xojbackendquestionfavourthumbservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyt.xojbackendjudgeservice.common.BaseResponse;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.common.ResultUtils;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionFavourService;
import com.xyt.xojbackendquestionservice.model.dto.questionfavour.QuestionFavourAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.questionfavour.QuestionFavourQueryRequest;
import com.xyt.xojbackendquestionservice.model.entity.QuestionFavour;
import com.xyt.xojbackendquestionservice.model.vo.QuestionFavourVO;
import com.xyt.xojbackendquestionservice.model.vo.QuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class QuestionFavourController {

    @Autowired
    private QuestionFavourService questionFavourService;

    /**
     * 题目收藏和取消收藏    用户中心的删除收藏 操作
     * @param questionFavourAddRequest
     * @param request
     * @return
     */

    @PostMapping("/change/favour")
    public BaseResponse<QuestionFavourVO> changeQuestionFavour(@RequestBody QuestionFavourAddRequest questionFavourAddRequest
                                                            , HttpServletRequest request){
        if (questionFavourAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionId = questionFavourAddRequest.getQuestionId();
        QuestionFavourVO questionFavourVO =  questionFavourService.changeQuestionFavour(questionId,request);

        return ResultUtils.success(questionFavourVO);
    }


    @PostMapping("/getUser/favour")
    public BaseResponse<Page<QuestionVO>> getUserQuestionFavourByPage(@RequestBody QuestionFavourQueryRequest questionFavourQueryRequest){
        if (questionFavourQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionFavourQueryRequest.getCurrent();
        long size = questionFavourQueryRequest.getPageSize();
        LambdaQueryWrapper<QuestionFavour> wrapper = new LambdaQueryWrapper<>();
        Long userId = questionFavourQueryRequest.getUserId();
        wrapper.eq(QuestionFavour::getUserId,userId);
        wrapper.orderByDesc(QuestionFavour::getCreateTime);
        Page<QuestionFavour> favourPage = questionFavourService.page(new Page<>(current, size), wrapper);
        Page<QuestionVO> questionVOPage =  questionFavourService.listUserQuestionFavourByPage(favourPage,userId);

        return ResultUtils.success(questionVOPage);
    }
}
