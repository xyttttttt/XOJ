package com.xyt.xojbackendjudgeservice.strategy;

import com.xyt.xojbackendquestionservice.model.codesandBox.JudgeInfo;
import com.xyt.xojbackendquestionservice.model.dto.question.JudgeCase;
import com.xyt.xojbackendquestionservice.model.entity.Question;
import com.xyt.xojbackendquestionservice.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文(用于定义策略中传递的参数)
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
