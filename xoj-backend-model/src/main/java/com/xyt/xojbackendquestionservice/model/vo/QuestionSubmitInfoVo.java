package com.xyt.xojbackendquestionservice.model.vo;

import lombok.Data;

@Data
public class QuestionSubmitInfoVo {

    /**
     * 提交总数
     */
    private Long submitTotal;
    /**
     * 提交通过总数
     */
    private Long submitAcceptTotal;
    /**
     *题目通过总数
     */
    private Long QuestionSubmitAcceptTotal;
    /**
     * 题目提交总数
     */
    private Long questionSubmitTotal;
}
