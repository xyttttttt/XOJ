package com.xyt.xojbackendjudgeservice.strategy;


import com.xyt.xojbackendquestionservice.model.codesandBox.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {


    JudgeInfo doJudge(JudgeContext judgeContext);
}
