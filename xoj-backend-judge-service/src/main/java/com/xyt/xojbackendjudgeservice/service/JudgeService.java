package com.xyt.xojbackendjudgeservice.service;


import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;

public interface JudgeService {

    /**
     * 判题服务
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
