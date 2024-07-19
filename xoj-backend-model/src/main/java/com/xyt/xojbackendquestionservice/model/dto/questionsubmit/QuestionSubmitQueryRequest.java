package com.xyt.xojbackendquestionservice.model.dto.questionsubmit;

import com.xyt.xojbackendjudgeservice.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {
    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 提交状态
     */
    private Integer status;

    /**
     * 用户id
     */
    private Long userId;


    private Long questionSubmitId;

    private static final long serialVersionUID = 1L;
}