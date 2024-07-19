package com.xyt.xojbackendquestionfavourthumbservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyt.xojbackendquestionservice.model.entity.QuestionThumb;
import com.xyt.xojbackendquestionservice.model.vo.QuestionThumbVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 16048
* @description 针对表【question_submit(题目提交表)】的数据库操作Service
* @createDate 2023-11-21 16:42:40
*/
public interface QuestionThumbService extends IService<QuestionThumb> {

    QuestionThumbVO changeQuestionThumb(Long questionId, HttpServletRequest request);
}
