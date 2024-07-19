package com.xyt.xojbackendquestionfavourthumbservice.controller;

import com.xyt.xojbackendjudgeservice.common.BaseResponse;
import com.xyt.xojbackendjudgeservice.common.ErrorCode;
import com.xyt.xojbackendjudgeservice.common.ResultUtils;
import com.xyt.xojbackendjudgeservice.exception.BusinessException;
import com.xyt.xojbackendquestionfavourthumbservice.service.QuestionThumbService;
import com.xyt.xojbackendquestionservice.model.dto.questionfavour.QuestionFavourAddRequest;
import com.xyt.xojbackendquestionservice.model.dto.questionthumb.QuestionThumbAddRequest;
import com.xyt.xojbackendquestionservice.model.vo.QuestionFavourVO;
import com.xyt.xojbackendquestionservice.model.vo.QuestionThumbVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class QuestionThumbController {

    @Resource
    private QuestionThumbService questionThumbService;

    @PostMapping("/change/thumb")
    public BaseResponse<QuestionThumbVO> changeQuestionThumb(@RequestBody QuestionThumbAddRequest questionThumbAddRequest
            , HttpServletRequest request){
        if (questionThumbAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionId = questionThumbAddRequest.getQuestionId();
        QuestionThumbVO questionThumbVO =  questionThumbService.changeQuestionThumb(questionId,request);

        return ResultUtils.success(questionThumbVO);
    }
}
