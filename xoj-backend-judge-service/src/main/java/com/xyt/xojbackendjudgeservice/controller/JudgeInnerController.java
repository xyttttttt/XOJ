package com.xyt.xojbackendjudgeservice.controller;

import com.xyt.xojbackendjudgeservice.service.JudgeService;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class JudgeInnerController {

    @Resource
    private JudgeService judgeService;
    /**
     * 判题服务
     * @param questionSubmitId
     * @return
     */
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }
}
