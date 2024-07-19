package com.xyt.xojbackendquestionservice.model.dto.questionfavour;

import lombok.Data;

import java.io.Serializable;

/**
 * 帖子收藏 / 取消收藏请求
 *
 */
@Data
public class QuestionFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}