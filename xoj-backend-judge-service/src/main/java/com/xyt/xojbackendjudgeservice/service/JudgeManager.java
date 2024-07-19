package com.xyt.xojbackendjudgeservice.service;

import com.xyt.xojbackendjudgeservice.strategy.*;
import com.xyt.xojbackendquestionservice.model.codesandBox.JudgeInfo;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理 简化调用
 *
 */
@Service
public class JudgeManager {

    /**
     * 判题服务
     * @param judgeContext
     * @return
     */
    public JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        if ("cpp".equals(language)){
            judgeStrategy = new CjiajiaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
