package com.xyt.xojbackendquestionservice.model.dto.questionsubmit;

import com.xyt.xojbackendjudgeservice.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitByMyQueryRequest extends PageRequest implements Serializable {

    /**
     * 用户id
     */
    private Long userId;


    private static final long serialVersionUID = 1L;
}