package com.xyt.xojbackendquestionservice.model.dto.questionthumb;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子点赞请求
 *
 */
@Data
public class QuestionThumbAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long QuestionId;

    private static final long serialVersionUID = 1L;
}